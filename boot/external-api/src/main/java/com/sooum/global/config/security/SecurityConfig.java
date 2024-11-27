package com.sooum.global.config.security;

import com.sooum.global.config.jwt.JwtAuthenticationFilter;
import com.sooum.global.config.jwt.TokenProvider;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final TokenProvider tokenProvider;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        // 모든 요청 HTTPS 강제
//        http.requiresChannel(channelRequestMatcherRegistry ->
//                channelRequestMatcherRegistry.anyRequest().requiresSecure());

        http.csrf(AbstractHttpConfigurer::disable)
                .httpBasic(AbstractHttpConfigurer::disable)
                .formLogin(AbstractHttpConfigurer::disable)
                .logout(AbstractHttpConfigurer::disable);

        http.authorizeHttpRequests(request -> request
                // No Auth
                .requestMatchers(HttpMethod.GET, "/users/key").permitAll()
                .requestMatchers(HttpMethod.POST, "/users/sign-up").permitAll()  // 회원가입
                .requestMatchers(HttpMethod.POST, "/users/login").permitAll()  // 로그인
                .requestMatchers(HttpMethod.GET, "/members").permitAll()
                .requestMatchers(HttpMethod.POST, "/cards").hasRole("USER")
                .requestMatchers(HttpMethod.POST, "/cards/{cardPk}").hasRole("USER")
                .requestMatchers(HttpMethod.POST, "/users/token").hasRole("USER") // 토큰 재발급
                .requestMatchers(HttpMethod.GET, "/profiles/nickname/{nickname}/available").permitAll()
                .requestMatchers(HttpMethod.POST, "/settings/transfer").permitAll()
                // Authenticated
                .anyRequest().authenticated()
        );

        // Session 해제
        http.sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        // Jwt 커스텀 필터 등록
        http.addFilterBefore(jwtAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class);

        // Token Exception Handling
        http.exceptionHandling(except -> except
                .authenticationEntryPoint((request, response, authException) -> response.sendError(response.getStatus(), "토큰 오류"))
        );

        return http.build();
    }

    @Bean
    public JwtAuthenticationFilter jwtAuthenticationFilter() {
        return new JwtAuthenticationFilter(tokenProvider);
    }
    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
