package com.linghang.wusthelper.wustyjs.service.impl;

import com.auth0.jwt.JWT;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.linghang.wusthelper.base.dto.ScoreDto;
import com.linghang.wusthelper.base.entity.Score;
import com.linghang.wusthelper.base.entity.Student;
import com.linghang.wusthelper.wustyjs.enums.WustyjsUrl;
import com.linghang.wusthelper.base.exception.BaseException;
import com.linghang.wusthelper.base.response.ResponseCode;
import com.linghang.wusthelper.base.response.ResponseVO;
import com.linghang.wusthelper.base.service.IScoreService;
import com.linghang.wusthelper.base.service.IStudentService;
import com.linghang.wusthelper.wustyjs.service.WustyjsSpiderService;
import com.linghang.wusthelper.base.utils.JwtUtil;
import com.linghang.wusthelper.base.utils.Md5Util;
import lombok.extern.slf4j.Slf4j;
import net.sourceforge.tess4j.Tesseract;
import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.client5.http.entity.UrlEncodedFormEntity;
import org.apache.hc.client5.http.fluent.Form;
import org.apache.hc.client5.http.fluent.Request;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.core5.http.*;
import org.apache.hc.core5.http.io.HttpClientResponseHandler;
import org.apache.hc.core5.http.io.entity.EntityUtils;
import org.apache.hc.core5.http.message.BasicNameValuePair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.*;
import java.util.concurrent.Semaphore;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@Slf4j
public class WustyjsSpiderServiceImpl implements WustyjsSpiderService {

    Semaphore semaphore = new Semaphore(1); // ????????????????????????

    @Autowired
    private CloseableHttpClient httpClient;

    @Autowired
    private Tesseract tesseract;

    @Autowired
    private IStudentService studentService;

    @Autowired
    private IScoreService scoreService;

    private static final Pattern pattern = Pattern.compile("(?<sessionId>ASP.NET_SessionId=.*?;)");

    /**
     * ??????
     * username: ??????
     *
     * @return token
     */
    @Override
    public ResponseVO login(String username, String password) {
        Student student = studentService.getStudent(username);
        Map<String, Object> tokenMap = new HashMap<>();
        tokenMap.put("username", username);
        try {
            int cnt = 0;
            List<NameValuePair> pairList = new ArrayList<>();
            pairList.add(new BasicNameValuePair("__VIEWSTATE", "/wEPDwUENTM4MWQYAQUeX19Db250cm9sc1JlcXVpcmVQb3N0QmFja0tleV9fFgIFEl9jdGwwOkltYWdlQnV0dG9uMQUSX2N0bDA6SW1hZ2VCdXR0b24yXnZLY54iSWFQ6B2yKH0EisNqU3/eKWEJPibQUElowzU="));
            pairList.add(new BasicNameValuePair("__VIEWSTATEGENERATOR", "496CE0B8"));
            pairList.add(new BasicNameValuePair("__EVENTVALIDATION", "/wEdAAYVkBquZFuFxLpraDgB64v+UDagjadrq+xukJizXKfuf485DjYUnSc4B1y8D5WGXeCaN+cQ7B52HzGj0ueO5HRlbdfASR9MjKgO1uRUmJC5kWf476Bpzok4CsBoBh+4Dc7vLkoP0tXUghu7H8qg++pYHeGok+i2xPFtG5oj0qB2dw=="));
            pairList.add(new BasicNameValuePair("_ctl0:txtusername", username));
            pairList.add(new BasicNameValuePair("_ctl0:txtpassword", password));
            pairList.add(new BasicNameValuePair("_ctl0:ImageButton1.x", "53"));
            pairList.add(new BasicNameValuePair("_ctl0:ImageButton1.y", "9"));
            do {
                cnt++;
                try {
                    // ??????try...catch????????????????????????????????????, ????????????????????????????????????????????????????????????8?????????
                    // 1. ????????????, ??????cookie???fileName
                    Map<String, String> map = downloadYzm();
                    // 2. ????????????, ??????yzm
                    String yzm = clippingAndBinary(map.get("fileName"));
                    pairList.add(new BasicNameValuePair("_ctl0:txtyzm", yzm));
                    // 3. ????????????, ??????cookie
                    HttpPost loginHttpPost = new HttpPost(WustyjsUrl.LOGIN_URL);
                    loginHttpPost.addHeader("cookie", map.get("cookie"));
                    loginHttpPost.setEntity(new UrlEncodedFormEntity(pairList));
                    pairList.remove(7);// ???pairList???????????????????????????, ??????????????????????????????
                    int code = httpClient.execute(loginHttpPost, HttpResponse::getCode);
                    if (code == 302) {
                        httpClient.execute(new HttpGet(WustyjsUrl.DEFAULT_URL), response -> null);
                        if (student == null) {
                            log.info(username + ":" + "????????????");
                            studentService.save(new Student(username, Md5Util.MD5(password)));// ??????????????????????????????
                        } else {
                            studentService.updateById(student);// ???????????????????????????????????????
                            log.info(username + ":" + cnt + "???????????????");
                        }
                        // ???????????? ??????cookie
                        tokenMap.put("cookie", map.get("cookie"));// ??????cookie?????????????????????
                        String token = JwtUtil.createToken(tokenMap);
                        return ResponseVO.custom().success().data(token).build();
                    }
                } catch (BaseException e) {
                    if (cnt >= 5)
                        throw e;// 5???????????????????????????, ????????????, ??????????????????????????????
                } catch (Exception e) {
                    // ??????????????????????????????
                }
            } while (cnt <= 8);
            // ?????????8?????????, ???????????????, ??????????????????????????????
            return ResponseVO.custom().code(ResponseCode.PASSWORD_NOT_MATCH).message("?????????????????????").build();
        } catch (BaseException e) { // ?????????????????????????????????, ?????????????????????????????????, ????????????token
            if (student != null && student.getPassword().equals(Md5Util.MD5(password))) {
                String token = JwtUtil.createToken(tokenMap);// token????????????cookie????????????????????????, ???????????????????????????
                return ResponseVO.custom().success().data(token).build();
            } else
                throw e;
        } catch (Exception e) {
            throw new BaseException(ResponseCode.LOGIN_ERROR, "error");
        }
    }

    /**
     * ????????????
     *
     * @return map, ??????cookie???fileName, cookie?????????
     */
    @Override
    public Map<String, String> downloadYzm() {
        String fileName = UUID.randomUUID().toString().replaceAll("-", "") + ".png";
        try (FileOutputStream fileOutputStream = new FileOutputStream(fileName)) {
            return httpClient.execute(new HttpGet(WustyjsUrl.CREATE_YZM_URL), new HttpClientResponseHandler<Map<String, String>>() {
                @Override
                public Map<String, String> handleResponse(ClassicHttpResponse yzmResponse) throws HttpException, IOException {
                    Map<String, String> map = new HashMap<>();
                    String sessionId = "";
                    Header setCookie = yzmResponse.getHeader("Set-Cookie");
                    if (setCookie != null) {
                        Matcher matcher = pattern.matcher(setCookie.getValue());
                        if (matcher.find())
                            sessionId = matcher.group("sessionId");
                    }
                    map.put("cookie", sessionId);
                    map.put("fileName", fileName);
                    yzmResponse.getEntity().writeTo(fileOutputStream);
                    return map;
                }
            });
        } catch (Exception e) {
            new File(fileName).delete();// ???????????????????????????
            throw new BaseException(ResponseCode.LOGIN_ERROR, "??????????????????");
        }

    }

    /**
     * ?????????????????????, ????????????????????????
     *
     * @return OCR??????????????????
     */
    @Override
    public String clippingAndBinary(String fileName) {
        try {
            BufferedImage bufferedImage = ImageIO.read(new File(fileName));
            int width = 60, height = 24;
            // ????????????,?????????????????????
            bufferedImage = bufferedImage.getSubimage(5, 11, width, height);
            // ?????????, limit??????????????????, ?????????????????????, limit=320,335 ???????????????75%,
            int limit = 335, black = Color.BLACK.getRGB(), white = Color.WHITE.getRGB();
            for (int i = 0; i < width; ++i) {
                for (int j = 0; j < height; ++j) {
                    int rgb = bufferedImage.getRGB(i, j);
                    int tSum = ((rgb >> 16) & 0xff) + ((rgb >> 8) & 0xff) + (rgb & 0xff);// ??????r + g + b??????
                    if (tSum <= limit)
                        bufferedImage.setRGB(i, j, black);// ??????
                    else
                        bufferedImage.setRGB(i, j, white);
                }
            }
            int available = semaphore.availablePermits();
            String result = "";
            try {
                semaphore.acquire(1);
                result = tesseract.doOCR(bufferedImage).replaceAll("\\D", "");// ????????????????????????
            } catch (InterruptedException e) {
                e.printStackTrace();
            } finally {
                semaphore.release(1);
            }
//            ImageIO.write(bufferedImage, "png", new File(fileName));// ?????????????????????
            return result;
        } catch (Exception e) {
            throw new BaseException(ResponseCode.LOGIN_ERROR, "??????????????????");
        } finally {
            new File(fileName).delete();// ??????????????????
        }
    }


    /**
     * ??????????????????
     *
     * @param token
     * @return
     */

    @Override
    public ResponseVO getStudent(String token) {
        DecodedJWT decode = JWT.decode(token);
        Student student = studentService.getStudent(decode.getClaim("username").asString());
        // ??????token????????????cookie
        String cookie = decode.getClaim("cookie").asString();
        try {
            // cookie?????????, ??????cookie????????????, ??????????????????
            if (cookie != null && validCookie(cookie)) {
                // ?????????????????????
                final String html = Request.get(WustyjsUrl.MAJOR_URL)
                        .addHeader("cookie", cookie)
                        .execute(httpClient)
                        .returnContent().asString(StandardCharsets.UTF_8);
                Pattern patternMajor = Pattern.compile("?????????(?<academy>.*?)&.*??????????(?<specialty>.*?)\\s", Pattern.DOTALL);
                Matcher matcher = patternMajor.matcher(html);
                if (matcher.find()) {
                    student.setAcademy(matcher.group("academy"));
                    student.setSpecialty(matcher.group("specialty"));
                }
                // ??????????????????
                String basicInfoHtml = Request.get(WustyjsUrl.BASIC_INFO_URL)
                        .addHeader("cookie", cookie)
                        .execute(httpClient)
                        .returnContent().asString(StandardCharsets.UTF_8);
                Pattern basicInfoPattern = Pattern.compile("?????????(?<studentNum>\\d+).*??????????(?<name>.*?)<.*??????????(?<grade>\\d+).*??????????(?<degree>.*?)<.*??????????(?<tutorName>.*?)\\s", Pattern.DOTALL);
                Matcher basicInfoMatcher = basicInfoPattern.matcher(basicInfoHtml);
                if (basicInfoMatcher.find()) {
                    if (!student.getStudentNum().equals(basicInfoMatcher.group("studentNum")))
                        throw new Exception();
                    student.setDegree(basicInfoMatcher.group("degree"));
                    student.setName(basicInfoMatcher.group("name"));
                    student.setGrade(Integer.parseInt(basicInfoMatcher.group("grade")));
                    student.setTutorName(basicInfoMatcher.group("tutorName"));
                }
                studentService.updateById(student);
            }
        } catch (Exception e) {
            // ????????????????????????, ?????????????????????????????????, ????????????
        }
        return ResponseVO.custom().success().data(student).build();
    }

    @Override
    public ResponseVO getScores(String token) {
        DecodedJWT decode = JWT.decode(token);
        String studentNum = decode.getClaim("username").asString();
        List<ScoreDto> scores = scoreService.getScores(studentNum);
        String cookie = decode.getClaim("cookie").asString();
        if (cookie != null && validCookie(cookie)) {
            try {
                final String scoresHtml = Request.get(WustyjsUrl.SCORES_URL)
                        .addHeader("cookie", cookie)
                        .execute(httpClient)
                        .returnContent().asString(StandardCharsets.UTF_8);
                Pattern patternScores = Pattern.compile("<td>(?<courseName>.*?)</td><td>(?<credit>\\d+\\.?\\d+)</td><td>(?<term>\\d+)</td><td>(?<point>.*?)</td>");
                Matcher matcherScores = patternScores.matcher(scoresHtml);
                while (matcherScores.find()) {
                    String courseName = matcherScores.group("courseName");
                    double credit = Double.parseDouble(matcherScores.group("credit"));
                    int term = Integer.parseInt(matcherScores.group("term"));
                    String point = matcherScores.group("point");
                    QueryWrapper<Score> scoreQueryWrapper = new QueryWrapper<>();
                    scoreQueryWrapper.eq("name", courseName);
                    scoreQueryWrapper.eq("credit", credit);
                    scoreQueryWrapper.eq("term", term);
                    scoreQueryWrapper.eq("point", point);
                    if (scoreService.getOne(scoreQueryWrapper) == null) {
                        Score score = new Score().setStudentNum(studentNum).setName(courseName).setCredit(credit).setTerm(term).setPoint(point);
                        scoreService.save(score);//??????????????????
                        scores.add(new ScoreDto(score.getId(), courseName, credit, term, point));// ??????????????????????????????
                    }
                }
            } catch (Exception e) {
                //
            }
        }
        return ResponseVO.custom().success().data(scores).build();
    }


    /**
     * ??????cookie???????????????, ???????????????, ????????????, ?????????html?????????1381
     *
     * @param cookie
     * @return
     */
    @Override
    public boolean validCookie(String cookie) {
        try {
            return Request.get(WustyjsUrl.DEFAULT_URL)
                    .addHeader("cookie", cookie)
                    .execute(httpClient)
                    .handleResponse(response -> EntityUtils.toString(response.getEntity()).length() == 1381);
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public ResponseVO updatePassword(String token, String newPassword) {
        DecodedJWT decode = JWT.decode(token);
        String username = decode.getClaim("username").asString();
        String cookie = decode.getClaim("cookie").asString();
        boolean updateBoolean = false;
        try {
            if (cookie != null && validCookie(cookie)) {
                Pattern patternUpdatePassword = Pattern.compile("<script language=javascript>alert\\('(?<alert>.*?)!");
                updateBoolean = Request.post(WustyjsUrl.UPDATE_PASSWORD_RUL)
                        .addHeader("cookie", cookie)
                        .bodyForm(Form.form()
                                .add("__EVENTTARGET", "_ctl0:MainWork:cmdAdd")
                                .add("__VIEWSTATE", "/wEPDwUKMTk3MTgxMTEzOA9kFgJmD2QWAgIBD2QWAgIFD2QWBAIFDw8WAh4EVGV4dAUMMjAyMTAzNzAzMDgyZGQCBw8PFgIfAAUJ5r2Y6YeR5LyfZGRkGmwVjN/3hxzSBuh3/0CRcXhVxXk6BycLPQ73v3C0v5s=")
                                .add("__EVENTARGUMENT", "")
                                .add("__VIEWSTATEGENERATOR", "B2668B58")
                                .add("__EVENTVALIDATION", "/wEdAAR6b+93tO6pC3qG/NSe+3VBxQ+YICd9r36iH34AwigxewF+RWA7EUrpP7NzBHF7brJVqbI2/gsEBfq1C5uywRHeljCUru2Aya/a2Q2Sz4NAJ21LaHTi3DwvLLH+XaYCpIY=")
                                .add("_ctl0:MainWork:txtpassword1", newPassword)
                                .add("_ctl0:MainWork:txtpassword2", newPassword)
                                .build())
                        .execute(httpClient)
                        .handleResponse(classicHttpResponse -> {
                            String updatePasswordHtml = EntityUtils.toString(classicHttpResponse.getEntity());
                            Matcher matcherUpdatePassword = patternUpdatePassword.matcher(updatePasswordHtml);
                            return matcherUpdatePassword.find() && matcherUpdatePassword.group("alert").equals("????????????");
                        });
                // ?????????????????????????????????????????????????????????, ?????????????????????
                if (updateBoolean) {
                    log.info(username + "???????????????");
                }
            }
        } catch (Exception e) {
            updateBoolean = false;// ??????????????????cookie??????, ????????????
        }
        return ResponseVO.custom().success().data(updateBoolean).build();
    }
}
