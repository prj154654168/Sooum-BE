package com.sooum.core.domain.member.service;

import com.sooum.core.domain.member.entity.Blacklist;
import com.sooum.core.domain.member.exception.MemberNotFoundException;
import com.sooum.core.domain.member.repository.BlacklistRepository;
import com.sooum.core.global.config.jwt.TokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BlacklistService {

    private final RedisTemplate<String, Object> redisTemplate;
    private final TokenProvider tokenProvider;
    private final BlacklistRepository blacklistRepository;

    public void save(String token, LocalDateTime expiredAt) {
        redisTemplate.opsForValue()
                .set(
                        token,
                        tokenProvider.getId(token).orElseThrow(MemberNotFoundException::new),
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

    public void fetch() {
        List<Blacklist> legacy = blacklistRepository.findAll().stream()
                .filter(blacklist -> blacklist.getExpiredAt().isBefore(LocalDateTime.now()))
                .toList();

        blacklistRepository.deleteAllInBatch(legacy);
    }
}
