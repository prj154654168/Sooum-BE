package com.sooum.core.global.auth.interceptor;

import com.sooum.core.domain.member.repository.BlacklistRepository;
import com.sooum.core.global.auth.interceptor.exception.JwtBlacklistException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import static jakarta.servlet.http.HttpServletResponse.SC_UNAUTHORIZED;

@Component
@RequiredArgsConstructor
public class JwtBlacklistInterceptor implements HandlerInterceptor {

    private final BlacklistRepository blacklistRepository;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Long memberId = Long.valueOf(authentication.getName());

        if (blacklistRepository.existsByMember_Pk(memberId)) {
            response.setStatus(SC_UNAUTHORIZED);
            throw new JwtBlacklistException();
        }
        return true;
    }
}
