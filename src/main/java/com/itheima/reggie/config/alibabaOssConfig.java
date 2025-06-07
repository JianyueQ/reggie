package com.itheima.reggie.config;

import com.itheima.reggie.properties.AlibabaOssProperties;
import com.itheima.reggie.utils.AlibabaOssUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@Slf4j
public class alibabaOssConfig {

    @Bean
    @ConditionalOnMissingBean
    public AlibabaOssUtils aliOssUtils(AlibabaOssProperties aliOssProperties){
        log.info("开始创建阿里云工具类对象.....");
        return new AlibabaOssUtils(
                aliOssProperties.getEndpoint(),
                aliOssProperties.getAccessKeyId(),
                aliOssProperties.getAccessKeySecret(),
                aliOssProperties.getBucketName());
    }



}
