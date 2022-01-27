import com.linghang.wusthelper.wustyjs.enums.WustyjsUrl;
import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.client5.http.config.RequestConfig;
import org.apache.hc.client5.http.cookie.BasicCookieStore;
import org.apache.hc.client5.http.cookie.StandardCookieSpec;
import org.apache.hc.client5.http.entity.UrlEncodedFormEntity;
import org.apache.hc.client5.http.fluent.Form;
import org.apache.hc.client5.http.fluent.Request;
import org.apache.hc.client5.http.fluent.Response;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.client5.http.impl.io.PoolingHttpClientConnectionManagerBuilder;
import org.apache.hc.core5.http.*;
import org.apache.hc.core5.http.io.HttpClientResponseHandler;
import org.apache.hc.core5.http.io.SocketConfig;
import org.apache.hc.core5.http.io.entity.EntityUtils;
import org.apache.hc.core5.http.message.BasicNameValuePair;
import org.apache.hc.core5.pool.PoolConcurrencyPolicy;
import org.apache.hc.core5.pool.PoolReusePolicy;
import org.apache.hc.core5.util.Timeout;
import org.seimicrawler.xpath.JXDocument;
import org.seimicrawler.xpath.JXNode;

import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class WustYjwSpiderTest {

    public static void main(String[] args) {

        BasicCookieStore cookieStore = new BasicCookieStore();
        for (int i = 0; i < 1; i++) {
            // String fileName = UUID.randomUUID().toString().replaceAll("-", "") + ".png";
            String fileName = "a.png";
            try (CloseableHttpClient httpClient = HttpClients.custom()
                    .setConnectionManager(PoolingHttpClientConnectionManagerBuilder.create()
                            .setMaxConnTotal(100)
                            .setMaxConnPerRoute(10)
                            .setPoolConcurrencyPolicy(PoolConcurrencyPolicy.LAX)    // Lax: 不强制限制最大连接数目
                            .setConnPoolPolicy(PoolReusePolicy.LIFO)                // LIFO: 平等的重用所有连接
                            .setDefaultSocketConfig(SocketConfig.custom().setSoTimeout(Timeout.ofSeconds(5)).build()) // socket超时时间
                            .build())
                    .setDefaultRequestConfig(RequestConfig.custom()
                            .setConnectTimeout(Timeout.ofSeconds(5))
                            .setResponseTimeout(Timeout.ofSeconds(5))
                            .setCookieSpec(StandardCookieSpec.STRICT)
                            .build())
                    .setDefaultCookieStore(cookieStore)
                    .build();
                 FileOutputStream fileOutputStream = new FileOutputStream(fileName);
            ) {
                // 1. 获取验证码以及cookie
                HttpGet getYzm = new HttpGet("http://59.68.177.189/pyxx/PageTemplate/NsoftPage/yzm/createyzm.aspx");
                httpClient.execute(getYzm, new HttpClientResponseHandler<String>() {
                    @Override
                    public String handleResponse(ClassicHttpResponse yzmResponse) throws HttpException, IOException {
                        String cookie = "";
                        Header setCookie = yzmResponse.getHeader("Set-Cookie");
                        Pattern pattern = Pattern.compile("(?<sessionId>ASP.NET_SessionId=.*?;)");
                        if (setCookie != null) {
                            Matcher matcher = pattern.matcher(setCookie.getValue());
                            if (matcher.find())
                                cookie = matcher.group("sessionId");
                        }
                        yzmResponse.getEntity().writeTo(fileOutputStream);
                        fileOutputStream.close();
                        // 输入验证码
                        Scanner in = new Scanner(System.in);
                        System.out.println("输入验证码: ");
                        String yzm = in.nextLine();
                        String username = "202103703082";
                        String password = "pjw202103703082";
                        List<NameValuePair> pairList = new ArrayList<>();
                        pairList.add(new BasicNameValuePair("__VIEWSTATE", "/wEPDwUENTM4MWQYAQUeX19Db250cm9sc1JlcXVpcmVQb3N0QmFja0tleV9fFgIFEl9jdGwwOkltYWdlQnV0dG9uMQUSX2N0bDA6SW1hZ2VCdXR0b24yXnZLY54iSWFQ6B2yKH0EisNqU3/eKWEJPibQUElowzU="));
                        pairList.add(new BasicNameValuePair("__VIEWSTATEGENERATOR", "496CE0B8"));
                        pairList.add(new BasicNameValuePair("__EVENTVALIDATION", "/wEdAAYVkBquZFuFxLpraDgB64v+UDagjadrq+xukJizXKfuf485DjYUnSc4B1y8D5WGXeCaN+cQ7B52HzGj0ueO5HRlbdfASR9MjKgO1uRUmJC5kWf476Bpzok4CsBoBh+4Dc7vLkoP0tXUghu7H8qg++pYHeGok+i2xPFtG5oj0qB2dw=="));
                        pairList.add(new BasicNameValuePair("_ctl0:txtusername", username));
                        pairList.add(new BasicNameValuePair("_ctl0:txtpassword", password));
                        pairList.add(new BasicNameValuePair("_ctl0:txtyzm", yzm));
                        pairList.add(new BasicNameValuePair("_ctl0:ImageButton1.x", "53"));
                        pairList.add(new BasicNameValuePair("_ctl0:ImageButton1.y", "9"));
                        HttpPost httpPost = new HttpPost("http://59.68.177.189/pyxx/login.aspx");
                        httpPost.setEntity(new UrlEncodedFormEntity(pairList));
                        httpPost.addHeader("cookie", cookie);
                        httpClient.execute(httpPost, response -> null);
                        // 8, 15
                        // 密码位数不能少于8位, 必须包含字母和数字, 而且仅能包含字母和数字
//                        String newPassword = "pjw202103703082";
//                        Pattern patternUpdatePassword = Pattern.compile("<script language=javascript>alert\\('(?<alert>.*?)!");
//                        final Boolean aBoolean = Request.post(WustyjsUrl.UPDATE_PASSWORD_RUL)
//                                .bodyForm(Form.form()
//                                        .add("__EVENTTARGET", "_ctl0:MainWork:cmdAdd")
//                                        .add("__VIEWSTATE", "/wEPDwUKMTk3MTgxMTEzOA9kFgJmD2QWAgIBD2QWAgIFD2QWBAIFDw8WAh4EVGV4dAUMMjAyMTAzNzAzMDgyZGQCBw8PFgIfAAUJ5r2Y6YeR5LyfZGRkGmwVjN/3hxzSBuh3/0CRcXhVxXk6BycLPQ73v3C0v5s=")
//                                        .add("__EVENTARGUMENT", "")
//                                        .add("__VIEWSTATEGENERATOR", "B2668B58")
//                                        .add("__EVENTVALIDATION", "/wEdAAR6b+93tO6pC3qG/NSe+3VBxQ+YICd9r36iH34AwigxewF+RWA7EUrpP7NzBHF7brJVqbI2/gsEBfq1C5uywRHeljCUru2Aya/a2Q2Sz4NAJ21LaHTi3DwvLLH+XaYCpIY=")
//                                        .add("_ctl0:MainWork:txtpassword1", newPassword)
//                                        .add("_ctl0:MainWork:txtpassword2", newPassword)
//                                        .build())
//                                .execute(httpClient)
//                                .handleResponse(classicHttpResponse -> {
//                                    String updatePasswordHtml = EntityUtils.toString(classicHttpResponse.getEntity());
//                                    System.out.println(updatePasswordHtml);
//                                    Matcher matcherUpdatePassword = patternUpdatePassword.matcher(updatePasswordHtml);
//                                    return matcherUpdatePassword.find() && matcherUpdatePassword.group("alert").equals("保存成功");
//                                });
//                        System.out.println(aBoolean);
                        // 为确保你的登录帐号的安全，密码位数不能少于8位，必须包含字母和数字！!


                        // 修改密码
                        // :
                        //:
                        //:
                        //:
                        //:
                        //_ctl0:MainWork:txtpassword1: pjw202103703082
                        //_ctl0:MainWork:txtpassword2: pjw202103703082

                        // __EVENTTARGET: _ctl0:MainWork:cmdAdd
                        //__EVENTARGUMENT:
                        //__VIEWSTATE: /wEPDwUKMTk3MTgxMTEzOA9kFgJmD2QWAgIBD2QWAgIFD2QWBAIFDw8WAh4EVGV4dAUMMjAyMTAzNzAzMDgyZGQCBw8PFgIfAAUJ5r2Y6YeR5LyfZGRkGmwVjN/3hxzSBuh3/0CRcXhVxXk6BycLPQ73v3C0v5s=
                        //__VIEWSTATEGENERATOR: B2668B58
                        //__EVENTVALIDATION: /wEdAAR6b+93tO6pC3qG/NSe+3VBxQ+YICd9r36iH34AwigxewF+RWA7EUrpP7NzBHF7brJVqbI2/gsEBfq1C5uywRHeljCUru2Aya/a2Q2Sz4NAJ21LaHTi3DwvLLH+XaYCpIY=
                        //_ctl0:MainWork:txtpassword1: pjw202103703082
                        //_ctl0:MainWork:txtpassword2: pjw202103703082

//                        final String html = Request.get(WustyjsUrl.MAJOR_URL)
//                                .addHeader("cookie", cookie)
//                                .execute(httpClient)
//                                .returnContent().asString(StandardCharsets.UTF_8);
//                        Pattern patternMajor = Pattern.compile("院系：(?<academy>.*?)&.*?专业：(?<specialty>.*?)\\s", Pattern.DOTALL);
//                        Matcher matcher = patternMajor.matcher(html);
//                        if (matcher.find()){
//                            System.out.println(matcher.group("academy"));
//                            System.out.println(matcher.group("specialty"));
//                        }
//                        String basicInfoHtml = Request.get(WustyjsUrl.BASIC_INFO_URL)
//                                .addHeader("cookie", cookie)
//                                .execute(httpClient)
//                                .returnContent().asString(StandardCharsets.UTF_8);
//                        Pattern basicInfoPattern = Pattern.compile("学号：(?<studentNum>\\d+).*?姓名：(?<name>.*?)<.*?年级：(?<grade>\\d+).*?类别：(?<degree>.*?)<.*?导师：(?<tutorName>.*?)\\s", Pattern.DOTALL);
//                        final Matcher basicInfoMatcher = basicInfoPattern.matcher(basicInfoHtml);
//                        if (basicInfoMatcher.find()){
//                            System.out.println(basicInfoMatcher.group("studentNum"));
//                            System.out.println(basicInfoMatcher.group("name"));
//                            System.out.println(basicInfoMatcher.group("grade"));
//                            System.out.println(basicInfoMatcher.group("degree"));
//                            System.out.println(basicInfoMatcher.group("tutorName"));
//                        }
                        final String scoresHtml = Request.get(WustyjsUrl.SCORES_URL)
                                .addHeader("cookie", cookie)
                                .execute(httpClient)
                                .returnContent().asString(StandardCharsets.UTF_8);
                        Pattern patternScores = Pattern.compile("<td>(?<courseName>.*?)</td><td>(?<credit>\\d+\\.?\\d+)</td><td>(?<term>\\d+)</td><td>(?<point>.*?)</td>");
                        final Matcher matcherScores = patternScores.matcher(scoresHtml);
                        while (matcherScores.find()){
                            System.out.println(matcherScores.group("courseName") + ", " + matcherScores.group("point"));
                        }

                        return null;
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                cookieStore.clear();
            }
        }

    }

}
