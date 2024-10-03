package com.sooum.core.global.config.redis;

import io.redisearch.Client;
import io.lettuce.core.RedisURI;
import io.lettuce.core.api.StatefulRedisConnection;
import io.lettuce.core.RedisClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RedisSearchConfig {

    @Bean
    public Client redisearchClient() {
        // Lettuce RedisClient 생성
        RedisClient redisClient = RedisClient.create(RedisURI.Builder.redis("localhost", 6379).build());

        // Redis 연결 생성
        StatefulRedisConnection<String, String> connection = redisClient.connect();

        // Redisearch 클라이언트 생성
        Client redisearchClient = new io.redisearch.client.Client("idx:tags", connection.sync());

        return redisearchClient;
    }
}
