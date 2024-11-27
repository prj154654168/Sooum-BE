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
import java.io.PrintWriter;

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
                else throw new InvalidTokenException();
            });

            filterChain.doFilter(request, response);
        } catch (ExpiredJwtException e) {
            if(e.getClaims().getSubject().equals("AccessToken")) {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                PrintWriter writer = response.getWriter();
                writer.flush();
                writer.close();
            }
            else {
                response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                PrintWriter writer = response.getWriter();
                writer.flush();
                writer.close();
            }
        }catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            PrintWriter writer = response.getWriter();
            writer.flush();
            writer.close();
        }
    }

    private void setAuthentication(String token) {
        if (tokenProvider.validateToken(token)) {
            Authentication authentication = tokenProvider.getAuthentication(token);
            SecurityContextHolder.getContext().setAuthentication(authentication);
        }
    }
}
