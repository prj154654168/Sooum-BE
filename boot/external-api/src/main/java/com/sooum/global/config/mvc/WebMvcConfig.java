package com.sooum.global.config.mvc;


import com.sooum.global.auth.interceptor.JwtBlacklistInterceptor;
import com.sooum.global.auth.resolver.CurrentUserArgumentResolver;
import com.sooum.global.config.jwt.TokenProvider;
import com.sooum.global.config.ratelimiter.RateLimitInterceptor;
import com.sooum.global.config.security.path.ExcludeAuthPathProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

@Configuration
@RequiredArgsConstructor
public class WebMvcConfig implements WebMvcConfigurer {

    private final TokenProvider tokenProvider;
    private final JwtBlacklistInterceptor jwtBlacklistInterceptor;
    private final RateLimitInterceptor rateLimitInterceptor;
    private final ExcludeAuthPathProperties excludeAuthPathProperties;

    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
        resolvers.add(new CurrentUserArgumentResolver(tokenProvider));
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(jwtBlacklistInterceptor)
                .excludePathPatterns(excludeAuthPathProperties.getExcludeAuthPaths());

        registry.addInterceptor(rateLimitInterceptor)
                .excludePathPatterns(excludeAuthPathProperties.getExcludeAuthPaths());
    }
}