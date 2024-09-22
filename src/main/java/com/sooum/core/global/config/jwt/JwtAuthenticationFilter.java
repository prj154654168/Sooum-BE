package com.sooum.core.global.config.jwt;

import com.sooum.core.domain.member.entity.Role;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Slf4j
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final TokenProvider tokenProvider;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        tokenProvider.getRefreshToken(request).ifPresentOrElse(this::setAuthentication,
                () -> tokenProvider.getAccessToken(request).ifPresent(accessToken -> {
                    if (!isBanned(accessToken)) {
                        setAuthentication(accessToken);
                    }
                }));

        filterChain.doFilter(request, response);
    }

    private void setAuthentication(String token) {
        if (tokenProvider.validateToken(token)) {
            Authentication authentication = tokenProvider.getAuthentication(token);
            SecurityContextHolder.getContext().setAuthentication(authentication);
        }
    }

    private boolean isBanned(String token) {
        Role tokenRole = tokenProvider.getRole(token)
                .orElse(Role.BANNED);   // 토큰에 role이 비워져 있다면 비정상적 요청이므로 BAN 상태로 처리하여 접근 제어

        return tokenRole == Role.BANNED;
    }
}
