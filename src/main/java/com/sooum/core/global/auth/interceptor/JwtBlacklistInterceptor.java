package com.sooum.core.global.auth.interceptor;

import com.sooum.core.domain.member.repository.BlacklistRepository;
import com.sooum.core.global.auth.interceptor.exception.JwtBlacklistException;
import com.sooum.core.global.config.jwt.TokenProvider;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import static jakarta.servlet.http.HttpServletResponse.SC_UNAUTHORIZED;

@Component
@RequiredArgsConstructor
public class JwtBlacklistInterceptor implements HandlerInterceptor {

    private final BlacklistRepository blacklistRepository;
    private final TokenProvider tokenProvider;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        String accessToken = tokenProvider.getAccessToken(request)
                .orElse(null);

        if (blacklistRepository.existsByAccessToken(accessToken)) {
            response.setStatus(SC_UNAUTHORIZED);
            throw new JwtBlacklistException();
        }
        return true;
    }
}
