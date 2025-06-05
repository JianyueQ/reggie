package com.itheima.reggie.utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.extern.slf4j.Slf4j;

import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.Map;


/**
 *  JWT工具类
 */
@Slf4j
public class JwtUtils {
    /**
     *  生成JWT
     *
     *
     * @param secretKey  密钥
     * @param ttlMillis 过期时间
     * @param claims  存放的参数
     * @return 生成的JWT字符串
     */
    public static String createJWT(String secretKey, long ttlMillis, Map<String, Object>  claims) {
        //指定签名的时候使用的签名算法
        SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.HS256;

        //生成Jwt的时间
        long expMillis = System.currentTimeMillis() + ttlMillis;
        Date expDate = new Date(expMillis);

        //设置Jwt的body
        JwtBuilder builder = Jwts.builder()
                .setClaims(claims)
                // 设置签名使用的签名算法和签名使用的秘钥
                .signWith(signatureAlgorithm,secretKey.getBytes(StandardCharsets.UTF_8))
                // 设置Jwt的过期时间，单位是毫秒
                .setExpiration(expDate);

        return builder.compact();
    }

    /**
     *  解析JWT
     * @param secretKey  密钥
     * @param token 加密后的token
     * @return  解析后的JWT
     */
     public static Map<String, Object> parseJWT(String secretKey, String token) {
        // 得到DefaultJwtParser
         Claims claims = Jwts.parser()
                 // 设置签名的秘钥
                 .setSigningKey(secretKey.getBytes(StandardCharsets.UTF_8))
                 // 设置需要解析的Jwt
                 .parseClaimsJws(token).getBody();
         return claims;
     }
}
