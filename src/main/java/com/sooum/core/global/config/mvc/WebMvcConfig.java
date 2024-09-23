package com.sooum.core.global.config.mvc;


import com.sooum.core.global.auth.interceptor.JwtBlacklistInterceptor;
import com.sooum.core.global.auth.resolver.CurrentUserArgumentResolver;
import com.sooum.core.global.config.jwt.TokenProvider;
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

    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
        resolvers.add(new CurrentUserArgumentResolver(tokenProvider));
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(jwtBlacklistInterceptor)
                .excludePathPatterns("/users/sign-up", "/users/login", "/users/key");
    }
}