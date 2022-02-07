import com.linghang.wusthelper.wustlib.enums.WustlibUrl;
import org.apache.hc.client5.http.cookie.BasicCookieStore;
import org.apache.hc.client5.http.fluent.Form;
import org.apache.hc.client5.http.fluent.Request;
import org.apache.hc.client5.http.fluent.Response;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.client5.http.impl.io.PoolingHttpClientConnectionManager;
import org.apache.hc.client5.http.impl.io.PoolingHttpClientConnectionManagerBuilder;
import org.apache.hc.client5.http.socket.ConnectionSocketFactory;
import org.apache.hc.client5.http.socket.PlainConnectionSocketFactory;
import org.apache.hc.client5.http.ssl.SSLConnectionSocketFactory;
import org.apache.hc.client5.http.ssl.SSLConnectionSocketFactoryBuilder;
import org.apache.hc.core5.http.Header;
import org.apache.hc.core5.http.HttpResponse;
import org.apache.hc.core5.http.config.Registry;
import org.apache.hc.core5.http.config.RegistryBuilder;
import org.apache.hc.core5.http.io.SocketConfig;
import org.apache.hc.core5.http.ssl.TLS;
import org.apache.hc.core5.pool.PoolConcurrencyPolicy;
import org.apache.hc.core5.pool.PoolReusePolicy;
import org.apache.hc.core5.ssl.SSLContexts;
import org.apache.hc.core5.ssl.TrustStrategy;
import org.apache.hc.core5.util.Timeout;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import java.io.FileReader;
import java.nio.charset.StandardCharsets;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Arrays;
import java.util.Date;

public class LibrarySpiderTest {

    public static void main(String[] args) throws Exception {



        // Trust standard CA and those trusted by our custom strategy
        final SSLContext sslcontext = SSLContexts.custom()
                .loadTrustMaterial(new TrustStrategy() {
                    @Override
                    public boolean isTrusted(
                            final X509Certificate[] chain,
                            final String authType) throws CertificateException {
                        final X509Certificate cert = chain[0];
                        return true; // 我这里直接配置的信任全部
                    }
                })
                .build();

        SSLConnectionSocketFactory sslSocketFactory = SSLConnectionSocketFactoryBuilder.create()
                .setSslContext(sslcontext)
                .setTlsVersions(TLS.V_1_2)
                .build();

//        Registry<ConnectionSocketFactory> socketFactoryRegistry = RegistryBuilder.<ConnectionSocketFactory>create()
//                .register("http", PlainConnectionSocketFactory.INSTANCE)
//                .register("https", new SSLConnectionSocketFactory(sc))
//                .build();

        PoolingHttpClientConnectionManager cm = PoolingHttpClientConnectionManagerBuilder.create()
                .setMaxConnTotal(100)
                .setMaxConnPerRoute(10)
                .setPoolConcurrencyPolicy(PoolConcurrencyPolicy.LAX)    // Lax: 不强制限制最大连接数目
                .setConnPoolPolicy(PoolReusePolicy.LIFO)                // LIFO: 平等的重用所有连接
                .setDefaultSocketConfig(SocketConfig.custom().setSoTimeout(Timeout.ofSeconds(5)).build()) // socket超时时间
                .setSSLSocketFactory(sslSocketFactory)
                .build();

        // 正式流程
        Date date1 = new Date();
        BasicCookieStore basicCookieStore = new BasicCookieStore();
        try (CloseableHttpClient httpClient = HttpClients.custom()
//                .setDefaultCookieStore(basicCookieStore)
                .setConnectionManager(cm)
                .disableCookieManagement()
                .disableRedirectHandling()
                .build()) {

            String username = "201913136025";
            String password = "l3868769";
            String service = "https://libsys.wust.edu.cn:443/meta-local/opac/cas/rosetta";

            ScriptEngineManager manager = new ScriptEngineManager();
            ScriptEngine engine = manager.getEngineByName("javascript");
            // 读取js文件
            String jsFileName = "F:\\JavaProjects\\linghang\\wusthelper\\wustlib_server\\src\\test\\java\\fun.js";
            FileReader reader = new FileReader(jsFileName);   // 执行指定脚本
            engine.eval(reader);
            Invocable invoke = (Invocable) engine;    // 调用myfun方法，并传入参数
            String password_encode = (String) invoke.invokeFunction("myfun", password);
            System.out.println("====");
            // 1. 请求tickets, 获取Location
            String location = Request.post("https://auth.wust.edu.cn/lyuapServer/v1/tickets")
                    .bodyForm(Form.form()
                            .add("username", username)
                            .add("password", password_encode)
                            .add("service", service)
                            .add("loginType", "")
                            .build())
                    .execute(httpClient).returnResponse().getFirstHeader("Location").getValue();
            System.out.println(location);
            // 2. 获取ticket
            String ticket = Request.post(location)
                    .bodyForm(Form.form()
                            .add("service", service)
                            .build())
                    .execute(httpClient).returnContent().asString(StandardCharsets.UTF_8);
            // 3. 认证ticket
            HttpResponse response = Request.get(service + "?ticket=" + ticket)
                    .execute(httpClient).returnResponse();

            // 获取cookie
            String cookie = response.getFirstHeader("Set-Cookie").getValue();
            String location1 = response.getFirstHeader("Location").getValue();
            Request.get(location1)
                    .addHeader("cookie", cookie)
                    .execute(httpClient);
            System.out.println("demo-" + cookie + "--" + location1 +" --" + Arrays.toString(response.getHeaders()));
            // 4. 验证cookie 401未认证
            final int code = Request.get(WustlibUrl.VALID_COOKIE_URL)
                    .addHeader("cookie", cookie)
                    .execute(httpClient).returnResponse().getCode();
            System.out.println(code);
            Date date2 = new Date();
            System.out.println(date1 + "---" + date2);
            System.out.println(Request.get(WustlibUrl.CURRENT_BORROW_URL)
                    .addHeader("cookie", cookie)
                    .execute(httpClient).returnContent().asString(StandardCharsets.UTF_8));
            // 读取cookie信息, 用于之后的...
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
