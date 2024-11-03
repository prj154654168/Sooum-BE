package com.sooum.api.block.service;

import com.sooum.data.member.entity.Blacklist;
import com.sooum.data.member.service.BlacklistService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class BlackListUseCase {
    private final RedisTemplate<String, Object> redisTemplate;
    private final BlacklistService blacklistService;
    private static final String PREFIX = "blacklist:";

    public void save(String token, LocalDateTime expiredAt) {
        redisTemplate.opsForValue()
                .set(
                        PREFIX + token,
                        "",
                        Duration.between(LocalDateTime.now(), expiredAt)
                );

        Blacklist blacklist = Blacklist.builder()
                .token(token)
                .expiredAt(expiredAt)
                .build();

        blacklistService.save(blacklist);
    }

    public Boolean isExist(String token) {
        return redisTemplate.hasKey(PREFIX + token);
    }
}
