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

    // ???????????????, ????????????????????????3???, ???????????????, ?????????30??????
    @Override
    public ResponseVO login(String username, String password) {
        Student student = studentService.getStudent(username);
        Map<String, Object> map = new HashMap<>();
        map.put("username", username);// ??????
        try {
            String encode_password = (String) invocable.invokeFunction("myfun", password);
            int cnt = 0;
            do {
                cnt++;
                try {
                    // 1. ??????tickets, ??????Location
                    String location = Request.post(WustlibUrl.TICKETS_URL)
                            .bodyForm(Form.form()
                                    .add("username", username)
                                    .add("password", encode_password)
                                    .add("service", WustlibUrl.LIB_SERVICE_URL)
                                    .add("loginType", "")
                                    .build())
                            .execute(httpClient).returnResponse().getFirstHeader("Location").getValue();
                    // 2. ??????ticket
                    String ticket = Request.post(location)
                            .bodyForm(Form.form()
                                    .add("service", WustlibUrl.LIB_SERVICE_URL)
                                    .build())
                            .execute(httpClient).returnContent().asString(StandardCharsets.UTF_8);
                    // 3. ??????ticket(?????????????????????????????????, ????????????)
                    HttpResponse response = Request.get(WustlibUrl.LIB_SERVICE_URL + "?ticket=" + ticket)
                            .execute(httpClient).returnResponse();
                    String cookie = response.getFirstHeader("Set-Cookie").getValue();
                    String locationFinal = response.getFirstHeader("Location").getValue();
                    Request.get(locationFinal)  // ??????cookie
                            .addHeader("cookie", cookie)
                            .execute(httpClient);
                    // 4. ??????cookie??????????????????
                    if (validCookie(cookie)) {
                        if (student == null) {
                            studentService.save(new Student(username, Md5Util.MD5(password)));// ??????????????????????????????
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
            // ????????????
            return ResponseVO.custom().code(ResponseCode.PASSWORD_NOT_MATCH).message("?????????????????????").build();
        } catch (Exception e) {
            // ????????????????????????token, ??????????????????cookie
            if (student != null && student.getPassword().equals(Md5Util.MD5(password)))
                return ResponseVO.custom().success().data(JwtUtil.createToken(map)).build();
            throw new BaseException(ResponseCode.LOGIN_ERROR, "error");
        }
    }

    /**
     * ?????????????????????????????????, ??????page=1&pageSize=100
     * ???????????????????????????30??????, pageSize?????????????????????
     *
     * @return
     */
    @Override
    @Transactional
    public ResponseVO getCurBooks(String token) {
        DecodedJWT decode = JWT.decode(token);
        String cookie = decode.getClaim("cookie").asString();
        String username = decode.getClaim("username").asString();
        // ???????????????????????????????????????
        List<Book> curBooks = bookService.getCurBooksByUsername(username);
        if (cookie != null && validCookie(cookie)) {    // ??????cookie?????????????????????
            try {
                // ????????????????????????????????????
                bookService.deleteCurBookBorrowByUsername(username);
                String html = Request.get(WustlibUrl.CURRENT_BORROW_URL)
                        .addHeader("cookie", cookie)
                        .execute(httpClient).returnContent().asString(StandardCharsets.UTF_8);
                JSONObject data = JSON.parseObject(html).getJSONObject("data");
                int total = data.getIntValue("total"); // ???????????????
                JSONArray items = data.getJSONArray("items");
                curBooks.clear(); // ?????????????????????
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
                throw new BaseException(ResponseCode.ERROR, "????????????????????????");
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

    // ??????????????????, url???, ??????page=1, pageSize=1000, ?????????????????????
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
                    log.error(username + " ???????????????" + total + "??????!");
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
                    // ??????????????????
                    borrowService.save(parseJsonObjectBorrow(bid, username, jsonObject, true));
                    hisBooks.add(tmpBook);
                }

            } catch (Exception e) {
                log.error(e.getMessage());
                throw new BaseException(ResponseCode.ERROR, "????????????????????????");
            }
        }
        return ResponseVO.custom().success().data(hisBooks).build();
    }

    @Override
    public boolean validCookie(String cookie) {
        try {
            return Request.get(WustlibUrl.VALID_COOKIE_URL)
                    .addHeader("cookie", cookie)
                    .execute(httpClient).returnResponse().getCode() == 200;// cookie??????????????????200, ?????????401?????????
        } catch (Exception e) {
            return false;
        }
    }

}
