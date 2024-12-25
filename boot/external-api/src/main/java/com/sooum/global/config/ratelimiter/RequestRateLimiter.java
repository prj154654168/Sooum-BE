package com.sooum.global.config.ratelimiter;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Objects;

@Component
@RequiredArgsConstructor
public class RequestRateLimiter {
    private final static String REDIS_KEY_PREFIX = "request_rate_limiter:";
    private final RedisTemplate<String, LocalDateTime> stringLocalDateTimeRedisTemplate;

    public boolean isRequestAllowed(String memberPk, String url) {
        String redisKey = REDIS_KEY_PREFIX + url + ":" + memberPk;
        if (!Objects.isNull(stringLocalDateTimeRedisTemplate.opsForValue().get(redisKey))) {
            return false;
        }
        saveRequest(redisKey);
        return true;
    }

    private void saveRequest(String key) {
        stringLocalDateTimeRedisTemplate.opsForValue().set(key,
                LocalDateTime.now(),
                Duration.between(LocalDateTime.now(),LocalDateTime.now().plusSeconds(1))
        );
    }
}
