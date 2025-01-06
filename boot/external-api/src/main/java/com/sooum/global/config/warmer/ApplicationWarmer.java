package com.sooum.global.config.warmer;

import com.sooum.data.member.entity.Role;
import com.sooum.global.config.jwt.TokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@RequiredArgsConstructor
@Component
public class ApplicationWarmer implements ApplicationListener<ApplicationReadyEvent> {
    private final TokenProvider tokenProvider;

    @Override
    public void onApplicationEvent(ApplicationReadyEvent event) {
        String accessToken = tokenProvider.createAccessToken(1L, Role.USER);
        RestClient restClient = RestClient.builder()
                .defaultHeader("Authorization","Bearer "+accessToken)
                .baseUrl("http://localhost:8080")
                .build();
        restClient.get().uri("/cards/home/latest").retrieve().body(String.class);
    }
}
