package com.sooum.api.member.service;

import com.sooum.data.member.entity.Blacklist;
import com.sooum.data.member.service.BlacklistService;
import com.sooum.global.config.jwt.RedisTokenPathPrefix;
import com.sooum.global.config.jwt.TokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class BlackListUseCase {
    private final StringRedisTemplate stringRedisTemplate;
    private final BlacklistService blacklistService;
    private final TokenProvider tokenProvider;

    public void save(String token, LocalDateTime expiredAt) {

        if (tokenProvider.isAccessToken(token))
            stringRedisTemplate.opsForValue()
                    .set(
                            RedisTokenPathPrefix.ACCESS_TOKEN.getPrefix() + token,
                            "",
                            Duration.between(LocalDateTime.now(), expiredAt)
                    );
        else
            stringRedisTemplate.opsForValue()
                    .set(
                            RedisTokenPathPrefix.REFRESH_TOKEN.getPrefix() + token,
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
        return stringRedisTemplate.hasKey(RedisTokenPathPrefix.ACCESS_TOKEN.getPrefix() + token);
    }

    public Boolean isRefreshTokenExist(String token) {
        return stringRedisTemplate.hasKey(RedisTokenPathPrefix.REFRESH_TOKEN.getPrefix() + token);
    }

    public void saveBlackListRefreshToken(String refreshToken) {
        LocalDateTime refreshTokenExpiredAt = tokenProvider.getExpirationAllowExpired(refreshToken);

        if (refreshTokenExpiredAt.isAfter(LocalDateTime.now())) {
            stringRedisTemplate.opsForValue().set(
                    RedisTokenPathPrefix.REFRESH_TOKEN.getPrefix() + refreshToken, "",
                    Duration.between(LocalDateTime.now(), refreshTokenExpiredAt)
            );

            Blacklist blacklist = Blacklist.builder()
                    .token(refreshToken)
                    .expiredAt(refreshTokenExpiredAt)
                    .build();
            blacklistService.save(blacklist);
        }
    }
}
