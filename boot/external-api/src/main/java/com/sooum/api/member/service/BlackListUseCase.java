package com.sooum.api.member.service;

import com.sooum.data.member.entity.Blacklist;
import com.sooum.data.member.service.BlacklistService;
import com.sooum.global.config.jwt.TokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class BlackListUseCase {
    private final RedisTemplate<String, String> redisStringTemplate;
    private final BlacklistService blacklistService;
    private static final String ACCESS_PREFIX = "blacklist:access:";
    private static final String REFRESH_PREFIX = "blacklist:refresh:";
    private final TokenProvider tokenProvider;

    public void save(String token, LocalDateTime expiredAt) {

        if (tokenProvider.isAccessToken(token))
            redisStringTemplate.opsForValue()
                    .set(
                            ACCESS_PREFIX + token,
                            "",
                            Duration.between(LocalDateTime.now(), expiredAt)
                    );
        else
            redisStringTemplate.opsForValue()
                    .set(
                            REFRESH_PREFIX + token,
                            "",
                            Duration.between(LocalDateTime.now(), expiredAt)
                    );

        Blacklist blacklist = Blacklist.builder()
                .token(token)
                .expiredAt(expiredAt)
                .build();

        blacklistService.save(blacklist);
    }

    public Boolean isAccessTokenExist(String token) {
        return redisStringTemplate.hasKey(ACCESS_PREFIX + token);
    }

    public Boolean isRefreshTokenExist(String token) {
        return redisStringTemplate.hasKey(REFRESH_PREFIX + token);
    }
}
