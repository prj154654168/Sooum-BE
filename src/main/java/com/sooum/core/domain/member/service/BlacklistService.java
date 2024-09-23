package com.sooum.core.domain.member.service;

import com.sooum.core.domain.member.exception.MemberNotFoundException;
import com.sooum.core.global.config.jwt.TokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class BlacklistService {

    private final RedisTemplate<String, Object> redisTemplate;
    private final TokenProvider tokenProvider;

    public void save(String token, Long timeout) {
        redisTemplate.opsForValue().set(token, tokenProvider.getId(token).orElseThrow(MemberNotFoundException::new), timeout, TimeUnit.SECONDS);
    }

    public Boolean isExist(String token) {
        return redisTemplate.hasKey(token);
    }
}
