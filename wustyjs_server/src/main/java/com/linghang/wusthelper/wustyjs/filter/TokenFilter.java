package com.linghang.wusthelper.wustyjs.filter;

import com.alibaba.fastjson.JSONObject;
import com.linghang.wusthelper.wustyjs.response.ResponseCode;
import com.linghang.wusthelper.wustyjs.response.ResponseVO;
import com.linghang.wusthelper.wustyjs.utils.JwtUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.regex.Pattern;

/**
 *
 */
@Component
@Order(Integer.MIN_VALUE + 99)
public class TokenFilter implements Filter {

    private static final Pattern PATTERN = Pattern.compile(".*().*");

    /**
     * 跳过token验证和权限验证的url清单
     */
    @Value("#{'${wusthelper.wustyjs.skip-authenticate-urls}'.split(',')}")
    private List<String> skipAuthenticateUrls;

    private Pattern skipAuthenticatePattern;

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        // 生成匹配正则，跳过token验证和权限验证的url
        skipAuthenticatePattern = fitByList(skipAuthenticateUrls);
        Filter.super.init(filterConfig);
    }

    private Pattern fitByList(List<String> skipUrlList) {
        if (skipUrlList == null || skipUrlList.size() == 0) {   // 没有配置返回默认
            return PATTERN;
        }
        StringBuffer patternString = new StringBuffer();
        patternString.append(".*(");
        skipUrlList.stream().forEach(url -> {
            patternString.append(url.trim());
            patternString.append("|");
        });
        if (skipUrlList.size() > 0) {
            patternString.deleteCharAt(patternString.length() - 1);
        }
        patternString.append(").*");
        return Pattern.compile(patternString.toString());
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;
        String uri = request.getRequestURI();

        // 不需要token验证和权限验证的url，直接放行
        boolean skipAuthenticate = skipAuthenticatePattern.matcher(uri).matches();
        if (skipAuthenticate) {
            filterChain.doFilter(request, response);
            return;
        }

        //获取token
        String token = request.getHeader("wustyjsToken");
        if (StringUtils.isBlank(token)) {
            error(response, ResponseCode.TOKEN_ERROR);
            return;
        }

        // 验证token
        try {
            JwtUtil.verify(token);
            filterChain.doFilter(request, response);
        } catch (Exception e) {
            error(response, ResponseCode.TokenExpired);
        }
    }

    // 验证失败
    private void error(HttpServletResponse response, int code) throws IOException {
        ResponseVO res = ResponseVO.custom().code(code).message("token失效").build();
        response.setContentType("application/json;charset=UTF-8");// 设置json格式, 同时指定charset防止中文乱码
        response.getWriter().print(JSONObject.toJSONString(res));
    }

    @Override
    public void destroy() {
        Filter.super.destroy();
    }
}
