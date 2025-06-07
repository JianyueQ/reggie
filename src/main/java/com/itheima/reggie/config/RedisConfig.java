package com.itheima.reggie.config;


import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
@Slf4j
public class RedisConfig {

    @Bean
    public RedisTemplate redisTemplate(RedisConnectionFactory redisConnectionFactory,
                                       ObjectMapper objectMapper) {
        log.info("开始进行Redis配置...");
        // 创建RedisTemplate对象
        RedisTemplate template = new RedisTemplate<>();
        // 设置连接工厂
        template.setConnectionFactory(redisConnectionFactory);
        //设置redis key的序列化方式
        template.setKeySerializer(new StringRedisSerializer());

        template.setHashKeySerializer(new StringRedisSerializer());

        // 使用自定义的 ObjectMapper 支持 LocalDateTime
        GenericJackson2JsonRedisSerializer jsonSerializer =
                new GenericJackson2JsonRedisSerializer(objectMapper);

        template.setValueSerializer(jsonSerializer);
        template.setHashValueSerializer(jsonSerializer);

        return template;

    }


}
