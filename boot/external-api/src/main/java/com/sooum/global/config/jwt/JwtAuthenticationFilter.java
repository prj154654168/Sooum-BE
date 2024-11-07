package com.sooum.global.config.jwt;

import io.jsonwebtoken.ExpiredJwtException;
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
        try {
            tokenProvider.getToken(request).ifPresent(token -> {
                if (tokenProvider.validateToken(token))
                    setAuthentication(token);
            });

            filterChain.doFilter(request, response);
        } catch (ExpiredJwtException e) {
            if(e.getClaims().getSubject().equals("AccessToken"))
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "엑세스 토큰 만료");
            else
                response.sendError(HttpServletResponse.SC_FORBIDDEN, "리프래쉬 토큰 만료");
        }
    }

    private void setAuthentication(String token) {
        if (tokenProvider.validateToken(token)) {
            Authentication authentication = tokenProvider.getAuthentication(token);
            SecurityContextHolder.getContext().setAuthentication(authentication);
        }
    }
}
