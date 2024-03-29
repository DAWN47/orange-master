package com.shuai.util;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.springframework.stereotype.Component;

import java.util.Date;

/**
 * @Author: fengxin
 * @CreateTime: 2023-04-19  22:50
 * @Description: token的使用
 */
@Component
public class JwtUtil {

    public static final String AUTH_HEADER_KEY = "Authorization";

    public static final String TOKEN_PREFIX = "Bearer ";
    /**
     * 过期时间一周
     */
    private static final long EXPIRE_TIME = 7 * 24 * 60 * 60 * 1000;

    private static final String secret = "43dcbc5b-8776-429e-b122-3cae6bd97020";

    /**
     * 校验token是否正确
     * @param token 密钥
     * @return 是否正确
     */
    public static boolean verify(String token) {
        System.out.println("verify:  "+token);
        try {
            Algorithm algorithm = Algorithm.HMAC256(secret);
            JWTVerifier verifier = JWT.require(algorithm)
                    .build();
            DecodedJWT jwt = verifier.verify(token);
            return true;
        } catch (Exception exception) {
            return false;
        }
    }

    /**
     * 获得token中的信息无需secret解密也能获得
     * @return token中包含的id
     */
    public static Long getUserId(String token) {
        try {
            DecodedJWT jwt = JWT.decode(token);
            return jwt.getClaim("id").asLong();
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 签发token
     * @return 加密的token
     */
    public static String sign(Long id) {
        try {
            Date date = new Date(System.currentTimeMillis() + EXPIRE_TIME);
            Algorithm algorithm = Algorithm.HMAC256(secret);
            // 附带username信息
            String token = JWT.create()
                    .withClaim("id", id)
                    .withExpiresAt(date)
                    .sign(algorithm);
            System.out.println(token);
            System.out.println(JwtUtil.verify(token));
            System.out.println(JwtUtil.isExpire(token));
            return token;
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 判断过期
     * @param token
     * @return
     */
    public static boolean isExpire(String token) {
        DecodedJWT jwt = JWT.decode(token);
        return System.currentTimeMillis() > jwt.getExpiresAt().getTime();
    }
}
