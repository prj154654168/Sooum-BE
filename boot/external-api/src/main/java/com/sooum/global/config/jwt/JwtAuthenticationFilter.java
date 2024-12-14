package com.sooum.global.config.jwt;

import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpMethod;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Map;

@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final TokenProvider tokenProvider;

    private final Map<String, HttpMethod> EXCLUDE_AUTH_PATH = Map.of(
            "/users/key", HttpMethod.GET,
            "/users/sign-up", HttpMethod.POST,
            "/users/login", HttpMethod.POST,
            "/members", HttpMethod.GET,
            "/profiles/nickname/**/available", HttpMethod.GET,
            "/members/suspension", HttpMethod.POST,
            "/app/version/**", HttpMethod.GET
    );
    private static final AntPathMatcher antPathMatcher = new AntPathMatcher();

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        try {

            if (isExcludedPath(request)) {
                filterChain.doFilter(request, response);
                return;
            }

            tokenProvider.getToken(request).ifPresentOrElse(token -> {
                if (tokenProvider.validateToken(token))
                    setAuthentication(token);
                else throw new InvalidTokenException();
            },()-> {throw new InvalidTokenException();});

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
        }catch (InvalidTokenException e) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            PrintWriter writer = response.getWriter();
            writer.flush();
            writer.close();
        }
    }

    public boolean isExcludedPath(HttpServletRequest request) {
        String requestPath = request.getRequestURI();
        HttpMethod requestMethod = HttpMethod.valueOf(request.getMethod());

        return EXCLUDE_AUTH_PATH.entrySet().stream()
                .anyMatch(entry -> antPathMatcher.match(entry.getKey(), requestPath)
                        && entry.getValue().equals(requestMethod));
    }

    private void setAuthentication(String token) {
        if (tokenProvider.validateToken(token)) {
            Authentication authentication = tokenProvider.getAuthentication(token);
            SecurityContextHolder.getContext().setAuthentication(authentication);
        }
    }
}
