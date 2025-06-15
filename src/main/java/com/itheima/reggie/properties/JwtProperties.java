package com.itheima.reggie.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "reggie.jwt")
@Data
public class JwtProperties {
    /**
     *  管理端签名密钥
     */
    private String adminSecretKey;
    private long adminTtl;
    private String adminTokenName;

    /**
     *  用户端签名密钥
     */
    private String userSecretKey;
    private long userTtl;
    private String userTokenName;

}
