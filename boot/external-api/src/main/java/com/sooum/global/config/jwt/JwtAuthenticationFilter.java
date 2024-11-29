package com.sooum.global.config.jwt;

import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpMethod;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Map;

import static org.springframework.http.HttpMethod.GET;
import static org.springframework.http.HttpMethod.POST;

@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final TokenProvider tokenProvider;
    private final Map<String, HttpMethod> NO_AUTH_REQUEST = Map.of(
            "/users/key", GET,       // RSA 공개 키 요청
            "/users/login", POST,    // 로그인 요청
            "/users/sign-up", POST,   // 회원가입 요청
            "/members", GET          // 유저 정지 이력 조회 API
    );

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws IOException {
        try {
            for (Map.Entry<String, HttpMethod> noAuth : NO_AUTH_REQUEST.entrySet()) {
                if(isNoAuthRequest(request, noAuth)) {
                    filterChain.doFilter(request, response);
                    return;
                }
            }

            tokenProvider.getToken(request).ifPresentOrElse(token -> {
                if (tokenProvider.validateToken(token))
                    setAuthentication(token);
                else throw new InvalidTokenException();
            }, () -> {
                throw new EmptyTokenException();    // 토큰이 필요한 요청에 토큰이 존재하지 않을 경우 throw
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
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            PrintWriter writer = response.getWriter();
            writer.flush();
            writer.close();
        }
    }

    private static boolean isNoAuthRequest(HttpServletRequest request, Map.Entry<String, HttpMethod> noAuthRequest) {
        return noAuthRequest.getKey().equals(request.getRequestURI())
                && noAuthRequest.getValue().matches(request.getMethod());
    }

    private void setAuthentication(String token) {
        if (tokenProvider.validateToken(token)) {
            Authentication authentication = tokenProvider.getAuthentication(token);
            SecurityContextHolder.getContext().setAuthentication(authentication);
        }
    }
}
