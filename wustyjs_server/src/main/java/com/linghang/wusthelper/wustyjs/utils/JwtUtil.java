package com.linghang.wusthelper.wustyjs.utils;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTCreator;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;

import java.util.Calendar;
import java.util.Map;

public class JwtUtil {

    private static final String SIGN = "FJSDKLFJ23@FJDSFD$2WU2ST7YJS";// 签名

    private static final Integer MINUTES = 25;// 签名过期时间, 默认25分钟

    /**
     * 生成token
     *
     * @param map
     * @return
     */
    public static String createToken(Map<String, Object> map) {

        JWTCreator.Builder builder = JWT.create();

        // 指定payload, 先全转化为字符串
        map.forEach((k, v) -> {
            builder.withClaim(k, v.toString());
        });

        // 指定过期时间
        Calendar instance = Calendar.getInstance();
        instance.add(Calendar.MINUTE, MINUTES);// 默认1分钟
        builder.withExpiresAt(instance.getTime());

        // 指定签名, 生成token
        return builder.sign(Algorithm.HMAC256(SIGN));
    }

    /**
     * 验证token, 失败抛出异常
     *
     * @param token
     * @return
     */
    public static DecodedJWT verify(String token) throws JWTVerificationException {
        return JWT.require(Algorithm.HMAC256(SIGN)).build().verify(token);
    }

}
