package com.itheima.reggie.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "reggie.jwt")
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


    public String getUserTokenName() {
        return userTokenName;
    }

    public void setUserTokenName(String userTokenName) {
        this.userTokenName = userTokenName;
    }

    public long getUserTtl() {
        return userTtl;
    }

    public void setUserTtl(long userTtl) {
        this.userTtl = userTtl;
    }

    public String getUserSecretKey() {
        return userSecretKey;
    }

    public void setUserSecretKey(String userSecretKey) {
        this.userSecretKey = userSecretKey;
    }

    public String getAdminTokenName() {
        return adminTokenName;
    }

    public void setAdminTokenName(String adminTokenName) {
        this.adminTokenName = adminTokenName;
    }

    public long getAdminTtl() {
        return adminTtl;
    }

    public void setAdminTtl(long adminTtl) {
        this.adminTtl = adminTtl;
    }

    public String getAdminSecretKey() {
        return adminSecretKey;
    }

    public void setAdminSecretKey(String adminSecretKey) {
        this.adminSecretKey = adminSecretKey;
    }

    @Override
    public String toString() {
        return "JwtProperties{" +
                "adminSecretKey='" + adminSecretKey + '\'' +
                ", adminTtl=" + adminTtl +
                ", adminTokenName='" + adminTokenName + '\'' +
                ", userSecretKey='" + userSecretKey + '\'' +
                ", userTtl=" + userTtl +
                ", userTokenName='" + userTokenName + '\'' +
                '}';
    }




}
