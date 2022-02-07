package com.linghang.wusthelper.base.config;

import org.apache.hc.client5.http.config.RequestConfig;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.client5.http.impl.io.PoolingHttpClientConnectionManager;
import org.apache.hc.client5.http.impl.io.PoolingHttpClientConnectionManagerBuilder;
import org.apache.hc.client5.http.socket.ConnectionSocketFactory;
import org.apache.hc.client5.http.socket.PlainConnectionSocketFactory;
import org.apache.hc.client5.http.ssl.SSLConnectionSocketFactory;
import org.apache.hc.client5.http.ssl.SSLConnectionSocketFactoryBuilder;
import org.apache.hc.core5.http.config.Registry;
import org.apache.hc.core5.http.config.RegistryBuilder;
import org.apache.hc.core5.http.io.SocketConfig;
import org.apache.hc.core5.http.ssl.TLS;
import org.apache.hc.core5.pool.PoolConcurrencyPolicy;
import org.apache.hc.core5.pool.PoolReusePolicy;
import org.apache.hc.core5.ssl.SSLContexts;
import org.apache.hc.core5.ssl.TrustStrategy;
import org.apache.hc.core5.util.Timeout;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.net.ssl.SSLContext;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

@Configuration
public class HttpClientConfig {

    @Bean
    public PoolingHttpClientConnectionManager createPoolingHttpClientConnectionManager() {

        // 配置SSL, 解决https认证的问题
        SSLContext sslcontext = null;
        SSLConnectionSocketFactory sslConnectionSocketFactory = null;
        try {
            sslcontext = SSLContexts.custom()
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
            sslConnectionSocketFactory = SSLConnectionSocketFactoryBuilder.create()
                    .setSslContext(sslcontext)
                    .setTlsVersions(TLS.V_1_2)
                    .build();
        } catch (NoSuchAlgorithmException | KeyManagementException | KeyStoreException e) {
            e.printStackTrace();
        }

        return PoolingHttpClientConnectionManagerBuilder.create()
                .setMaxConnTotal(100)
                .setMaxConnPerRoute(10)
                .setPoolConcurrencyPolicy(PoolConcurrencyPolicy.LAX)    // Lax: 不强制限制最大连接数目
                .setConnPoolPolicy(PoolReusePolicy.LIFO)                // LIFO: 平等的重用所有连接
                .setDefaultSocketConfig(SocketConfig.custom().setSoTimeout(Timeout.ofSeconds(5)).build()) // socket超时时间
                .setSSLSocketFactory(sslConnectionSocketFactory)
                .build();
    }

    @Bean
    public RequestConfig createRequestConfig() {
        return RequestConfig.custom()
                .setConnectTimeout(Timeout.ofSeconds(5))
                .setResponseTimeout(Timeout.ofSeconds(5))
                .build();
    }

//    @Bean
//    public BasicCookieStore createCookieStore(){
//        return new BasicCookieStore();
//    }

    @Bean
    public CloseableHttpClient createCloseableHttpClient(PoolingHttpClientConnectionManager cm,
                                                         RequestConfig requestConfig) {
        return HttpClients.custom()
                .setConnectionManager(cm)
                .setDefaultRequestConfig(requestConfig)
                .disableCookieManagement()
                .disableRedirectHandling()
                .build();
    }

}
