package com.sooum.core.global.auth.interceptor;

import com.sooum.core.domain.member.service.BlacklistService;
import com.sooum.core.global.auth.interceptor.exception.JwtBlacklistException;
import com.sooum.core.global.config.jwt.InvalidTokenException;
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

    private final TokenProvider tokenProvider;
    private final BlacklistService blacklistService;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        String accessToken = tokenProvider.getAccessToken(request)
                .orElseThrow(InvalidTokenException::new);

        if (blacklistService.isExist(accessToken)) {
            response.setStatus(SC_UNAUTHORIZED);
            throw new JwtBlacklistException();
        }
        return true;
    }
}
