package com.sooum.core.domain.member.service;

import com.sooum.core.domain.member.entity.Blacklist;
import com.sooum.core.domain.member.repository.BlacklistRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class BlacklistService {

    private final RedisTemplate<String, Object> redisTemplate;
    private final BlacklistRepository blacklistRepository;

    public void save(String token, LocalDateTime expiredAt) {
        redisTemplate.opsForValue()
                .set(
                        token,
                        "",
                        Duration.between(LocalDateTime.now(), expiredAt)
                );

        Blacklist blacklist = Blacklist.builder()
                .token(token)
                .expiredAt(expiredAt)
                .build();

        blacklistRepository.save(blacklist);
    }

    public Boolean isExist(String token) {
        return redisTemplate.hasKey(token);
    }
}
