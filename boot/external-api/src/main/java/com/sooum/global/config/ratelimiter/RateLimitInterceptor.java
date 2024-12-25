package com.sooum.global.config.ratelimiter;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.apache.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;


@Setter
@Component
@RequiredArgsConstructor
public class RateLimitInterceptor implements HandlerInterceptor {
    private final RequestRateLimiter requestRateLimiter;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        String memberPk = SecurityContextHolder.getContext().getAuthentication().getName();
        if (!requestRateLimiter.isRequestAllowed(memberPk, request.getRequestURI())) {
            response.setStatus(HttpStatus.SC_TOO_MANY_REQUESTS);
            return false;
        }
        return true;
    }
}
