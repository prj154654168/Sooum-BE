package com.sooum.global.auth.interceptor;

import com.sooum.api.block.service.BlackListUseCase;
import com.sooum.global.auth.interceptor.exception.JwtBlacklistException;
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
        String accessToken = tokenProvider.getAccessToken(request)
                .orElse(null);

        if(accessToken == null)
            return true;

        if (blackListUseCase.isExist(accessToken)) {
            response.setStatus(SC_UNAUTHORIZED);
            throw new JwtBlacklistException();
        }
        return true;
    }
}