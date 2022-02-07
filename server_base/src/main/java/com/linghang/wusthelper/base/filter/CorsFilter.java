package com.linghang.wusthelper.base.filter;

import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class CorsFilter implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse res = (HttpServletResponse) response;
        // 设置允许Cookie
        res.addHeader("Access-Control-Allow-Credentials", "true");
        // 允许http://www.xxx.com域（自行设置，这里只做示例）发起跨域请求 req.getHeader("")
        res.addHeader("Access-Control-Allow-Origin", "*");
        // 设置允许跨域请求的方法, get/post...
        res.addHeader("Access-Control-Allow-Methods", "*");
        // 允许跨域请求包含content-type
        res.addHeader("Access-Control-Allow-Headers", "*");
        res.addHeader("Access-Control-Expose-Headers", "*");
        chain.doFilter(req, res);
    }

    @Override
    public void destroy() {
    }

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
    }
}