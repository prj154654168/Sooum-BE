package com.sooum.core.domain.member.service;

import com.sooum.core.domain.member.entity.Blacklist;
import com.sooum.core.domain.member.exception.MemberNotFoundException;
import com.sooum.core.domain.member.repository.BlacklistRepository;
import com.sooum.core.global.config.jwt.TokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.Instant;
import java.util.Set;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class BlacklistService {

    private final RedisTemplate<String, Object> redisTemplate;
    private final TokenProvider tokenProvider;
    private final BlacklistRepository blacklistRepository;

    public void save(String token, Duration timeout) {
        redisTemplate.opsForValue().set(token, tokenProvider.getId(token).orElseThrow(MemberNotFoundException::new));
        redisTemplate.expire(token, timeout);
    }

    public Boolean isExist(String token) {
        return redisTemplate.hasKey(token);
    }

    @Transactional
    public void backup() {
        Set<String> tokens = redisTemplate.keys("*");

        if(tokens == null || tokens.isEmpty())
            return;

        for (String token : tokens) {
            Long ttl = redisTemplate.getExpire(token, TimeUnit.SECONDS);

            if(ttl != null) {
                Blacklist blacklist = Blacklist.builder()
                        .token(token)
                        .expiredAt(Instant.now().plusSeconds(ttl))
                        .build();

                blacklistRepository.save(blacklist);
            }
        }
    }
}
