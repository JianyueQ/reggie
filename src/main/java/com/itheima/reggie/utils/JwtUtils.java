package com.itheima.reggie.utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.Map;

public class JwtUtils {


    public static String createJWT(String secretKey, long ttlMillis, Map<String, Object> claims) {

        //指定算法签名
        SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.HS256;
        //创建过期时间
        long exp = System.currentTimeMillis() + ttlMillis;
        Date expDate = new Date(exp);

        JwtBuilder builder = Jwts.builder()
                .setExpiration(expDate)
                .signWith(signatureAlgorithm, secretKey.getBytes(StandardCharsets.UTF_8))
                .setClaims(claims);

        return builder.compact();
    }

    public static Claims parseJWT(String secretKey, String token) {

        return Jwts.parser()
                .setSigningKey(secretKey.getBytes(StandardCharsets.UTF_8))
                .parseClaimsJws(token).getBody();
    }


}
