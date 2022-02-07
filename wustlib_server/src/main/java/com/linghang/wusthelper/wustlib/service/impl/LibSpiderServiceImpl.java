package com.linghang.wusthelper.wustlib.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.auth0.jwt.JWT;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.linghang.wusthelper.base.entity.Student;
import com.linghang.wusthelper.base.exception.BaseException;
import com.linghang.wusthelper.base.response.ResponseCode;
import com.linghang.wusthelper.base.response.ResponseVO;
import com.linghang.wusthelper.base.service.IStudentService;
import com.linghang.wusthelper.base.utils.JwtUtil;
import com.linghang.wusthelper.base.utils.Md5Util;
import com.linghang.wusthelper.wustlib.entity.Book;
import com.linghang.wusthelper.wustlib.entity.BookBorrow;
import com.linghang.wusthelper.wustlib.enums.WustlibUrl;
import com.linghang.wusthelper.wustlib.mapper.BookBorrowMapper;
import com.linghang.wusthelper.wustlib.service.IBookBorrowService;
import com.linghang.wusthelper.wustlib.service.IBookService;
import com.linghang.wusthelper.wustlib.service.LibSpiderService;
import lombok.extern.slf4j.Slf4j;
import org.apache.hc.client5.http.fluent.Form;
import org.apache.hc.client5.http.fluent.Request;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.core5.http.HttpResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import springfox.documentation.spring.web.json.Json;

import javax.script.Invocable;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class LibSpiderServiceImpl implements LibSpiderService {

    @Autowired
    private CloseableHttpClient httpClient;

    @Autowired
    private Invocable invocable;

    @Autowired
    private IStudentService studentService;

    @Autowired
    private IBookService bookService;

    @Autowired
    private IBookBorrowService borrowService;

    // 同一个账号, 账号密码输入错误3次, 会禁止输入, 时间为30分钟
    @Override
    public ResponseVO login(String username, String password) {
        Student student = studentService.getStudent(username);
        Map<String, Object> map = new HashMap<>();
        map.put("username", username);// 学号
        try {
            String encode_password = (String) invocable.invokeFunction("myfun", password);
            int cnt = 0;
            do {
                cnt++;
                try {
                    // 1. 请求tickets, 获取Location
                    String location = Request.post(WustlibUrl.TICKETS_URL)
                            .bodyForm(Form.form()
                                    .add("username", username)
                                    .add("password", encode_password)
                                    .add("service", WustlibUrl.LIB_SERVICE_URL)
                                    .add("loginType", "")
                                    .build())
                            .execute(httpClient).returnResponse().getFirstHeader("Location").getValue();
                    // 2. 获取ticket
                    String ticket = Request.post(location)
                            .bodyForm(Form.form()
                                    .add("service", WustlibUrl.LIB_SERVICE_URL)
                                    .build())
                            .execute(httpClient).returnContent().asString(StandardCharsets.UTF_8);
                    // 3. 认证ticket(中间有一个重定向的过程, 不可缺少)
                    HttpResponse response = Request.get(WustlibUrl.LIB_SERVICE_URL + "?ticket=" + ticket)
                            .execute(httpClient).returnResponse();
                    String cookie = response.getFirstHeader("Set-Cookie").getValue();
                    String locationFinal = response.getFirstHeader("Location").getValue();
                    Request.get(locationFinal)  // 认证cookie
                            .addHeader("cookie", cookie)
                            .execute(httpClient);
                    // 4. 检验cookie是否验证成功
                    if (validCookie(cookie)) {
                        if (student == null) {
                            studentService.save(new Student(username, Md5Util.MD5(password)));// 第一次登录则插入数据
                        } else studentService.updateById(student);
                        map.put("cookie", cookie);
                        String token = JwtUtil.createToken(map);
                        return ResponseVO.custom().success().data(token).build();
                    }
                } catch (Exception e) {
                    if (cnt == 2)
                        throw e;
                }
            } while (cnt <= 3);
            // 密码错误
            return ResponseVO.custom().code(ResponseCode.PASSWORD_NOT_MATCH).message("账号密码不匹配").build();
        } catch (Exception e) {
            // 网络错误同样返回token, 但其中不包含cookie
            if (student != null && student.getPassword().equals(Md5Util.MD5(password)))
                return ResponseVO.custom().success().data(JwtUtil.createToken(map)).build();
            throw new BaseException(ResponseCode.LOGIN_ERROR, "error");
        }
    }

    /**
     * 获取当前借阅的全部图书, 默认page=1&pageSize=100
     * 由于一个人最多借阅30本书, pageSize的默认值够用了
     *
     * @return
     */
    @Override
    @Transactional
    public ResponseVO getCurBooks(String token) {
        DecodedJWT decode = JWT.decode(token);
        String cookie = decode.getClaim("cookie").asString();
        String username = decode.getClaim("username").asString();
        // 查询数据库中已经借阅的书籍
        List<Book> curBooks = bookService.getCurBooksByUsername(username);
        if (cookie != null && validCookie(cookie)) {    // 如果cookie有效且网络良好
            try {
                // 清空数据库原有的借阅数据
                bookService.deleteCurBookBorrowByUsername(username);
                String html = Request.get(WustlibUrl.CURRENT_BORROW_URL)
                        .addHeader("cookie", cookie)
                        .execute(httpClient).returnContent().asString(StandardCharsets.UTF_8);
                JSONObject data = JSON.parseObject(html).getJSONObject("data");
                int total = data.getIntValue("total"); // 总借阅数目
                JSONArray items = data.getJSONArray("items");
                curBooks.clear(); // 清除之前的数据
                for (int i = 0; i < total; i++) {
                    JSONObject jsonObject = items.getJSONObject(i);
                    String bid = jsonObject.getString("bibId");
                    Book tmpBook = bookService.getById(bid);
                    if (tmpBook == null) {
                        tmpBook = parseJsonObject(bid, jsonObject);
                        bookService.save(tmpBook);
                    }
                    borrowService.save(parseJsonObjectBorrow(bid, username, jsonObject, false));
                    curBooks.add(tmpBook);
                }
            } catch (Exception e) {
                log.error(e.getMessage());
                throw new BaseException(ResponseCode.ERROR, "获取当前借阅异常");
            }
        }
        return ResponseVO.custom().success().data(curBooks).build();
    }


    private Book parseJsonObject(String bid, JSONObject jsonObject) {
        String barCode = jsonObject.getString("barCode");
        String bookName = jsonObject.getString("title");
        String author = jsonObject.getString("author");
        String place = jsonObject.getString("location");
        Integer pubYear = jsonObject.getJSONObject("bibAttrs").getInteger("pub_year");
        return new Book(bid, barCode, bookName, author, place, pubYear);
    }

    private BookBorrow parseJsonObjectBorrow(String bid, String username, JSONObject jsonObject, boolean flag) {
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        LocalDateTime borrowTime = LocalDateTime.parse(jsonObject.getString("loanDate"), dateTimeFormatter);
        String backName = (flag ? "returnDate" : "dueDate");
        LocalDateTime backTime = LocalDateTime.parse(jsonObject.getString(backName), dateTimeFormatter);
        return new BookBorrow()
                .setFlag(flag)
                .setStudentNum(username)
                .setBid(bid)
                .setBorrowTime(borrowTime)
                .setBackTime(backTime);
    }

    // 查询历史借阅, url中, 默认page=1, pageSize=1000, 应该能包括所有
    @Override
    @Transactional
    public ResponseVO getHisBooks(String token) {
        DecodedJWT decode = JWT.decode(token);
        String cookie = decode.getClaim("cookie").asString();
        String username = decode.getClaim("username").asString();
        List<Book> hisBooks = bookService.getHisBooksByUsername(username);
        if (cookie != null && validCookie(cookie)) {
            try {
                bookService.deleteHisBookBorrowByUsername(username);
                String html = Request.get(WustlibUrl.HISTORY_URL)
                        .addHeader("cookie", cookie)
                        .execute(httpClient).returnContent().asString(StandardCharsets.UTF_8);
                JSONObject data = JSON.parseObject(html).getJSONObject("data");
                Integer total = data.getInteger("total");
                if (total > 1000)
                    log.error(username + " 已经借阅了" + total + "本书!");
                hisBooks.clear();
                JSONArray items = data.getJSONArray("items");
                for (int i = 0; i < total; i++) {
                    JSONObject jsonObject = items.getJSONObject(i);
                    String bid = jsonObject.getString("bibId");
                    Book tmpBook = bookService.getById(bid);
                    if (tmpBook == null) {
                        tmpBook = parseJsonObject(bid, jsonObject);
                        bookService.save(tmpBook);
                    }
                    // 保存借阅信息
                    borrowService.save(parseJsonObjectBorrow(bid, username, jsonObject, true));
                    hisBooks.add(tmpBook);
                }

            } catch (Exception e) {
                log.error(e.getMessage());
                throw new BaseException(ResponseCode.ERROR, "获取历史借阅异常");
            }
        }
        return ResponseVO.custom().success().data(hisBooks).build();
    }

    @Override
    public boolean validCookie(String cookie) {
        try {
            return Request.get(WustlibUrl.VALID_COOKIE_URL)
                    .addHeader("cookie", cookie)
                    .execute(httpClient).returnResponse().getCode() == 200;// cookie有效状态码为200, 失效为401未认证
        } catch (Exception e) {
            return false;
        }
    }

}
