package com.sooum.global.auth.interceptor;

import com.sooum.api.member.service.BlackListUseCase;
import com.sooum.global.config.jwt.TokenProvider;
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
    private final BlackListUseCase blackListUseCase;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        String token = tokenProvider.getToken(request)
                .orElse(null);

        if(token == null)
            return true;

        if (isBlackListToken(token)) {
            response.setStatus(SC_UNAUTHORIZED);
            return false;
        }

        return true;
    }

    private boolean isBlackListToken(String token) {
        return (tokenProvider.isAccessToken(token) && blackListUseCase.isAccessTokenExist(token))
                || (!tokenProvider.isAccessToken(token) && blackListUseCase.isRefreshTokenExist(token));
    }
}
