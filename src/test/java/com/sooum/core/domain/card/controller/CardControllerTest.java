package com.sooum.core.domain.card.controller;

import com.sooum.core.domain.card.service.CardService;
import com.sooum.core.domain.card.service.FeedLikeService;
import com.sooum.core.domain.card.service.FeedService;
import com.sooum.core.domain.tag.repository.CachedTagRepository;
import com.sooum.core.global.auth.interceptor.JwtBlacklistInterceptor;
import com.sooum.core.global.config.jwt.TokenProvider;
import com.sooum.core.global.config.mvc.WebMvcConfig;
import com.sooum.core.global.config.redis.RedisConfig;
import com.sooum.core.global.config.security.SecurityConfig;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.batch.core.explore.JobExplorer;
import org.springframework.batch.core.launch.JobOperator;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import static org.mockito.BDDMockito.any;
import static org.mockito.BDDMockito.doNothing;

@WebMvcTest(CardController.class)
@MockBean(JpaMetamodelMappingContext.class)
@MockBean(JwtBlacklistInterceptor.class)
@MockBean(WebMvcConfig.class)
@MockBean(TokenProvider.class)
@MockBean(JobExplorer.class)
@MockBean(JobOperator.class)
@MockBean(JobRepository.class)
@MockBean(CachedTagRepository.class)
@Import(value = { SecurityConfig.class, RedisConfig.class})
class CardControllerTest {
    @Autowired
    MockMvc mockMvc;
    @MockBean
    CardService cardService;
    @MockBean
    FeedService feedService;
    @MockBean
    FeedLikeService feedLikeService;

    private static final String ACCESS_TOKEN = "Bearer eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJpYXQiOjE3MjY5Mjk0MzMsImV4cCI6MTAxNzI2OTI5NDMzLCJzdWIiOiJBY2Nlc3NUb2tlbiIsImlkIjo2MjUwNDc5NzMyNTA1MTUxNTMsInJvbGUiOiJVU0VSIn0.aL4Tr3FaSwvu9hOQISAvGJfCHBGCV9jRo_BfTQkBssU";
    private static final String TOKEN_HEADER = "Authorization";

    @Test
    @DisplayName("피드 카드 좋아요 성공")
    @WithMockUser
    void createFeedLikeSuccess() throws Exception{
        // given
        doNothing().when(feedLikeService).createFeedLike(any(), any());

        // when
        ResultActions resultActions = mockMvc.perform(
                MockMvcRequestBuilders
                        .post("/cards/{cardPk}/like", 1L)
                        .header(TOKEN_HEADER, ACCESS_TOKEN)
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(SecurityMockMvcRequestPostProcessors.csrf())
        );

        // then
        resultActions.andDo(MockMvcResultHandlers.print());
        resultActions.andExpect(MockMvcResultMatchers.status().isCreated());
        resultActions.andExpect(MockMvcResultMatchers.jsonPath("$.httpCode").value(HttpStatus.CREATED.value()));
    }

    @Test
    @DisplayName("피드 카드 좋아요 삭제")
    @WithMockUser
    void deleteFeedLikeSuccess() throws Exception{
        // given
        doNothing().when(feedLikeService).deleteFeedLike(any(), any());

        // when
        ResultActions resultActions = mockMvc.perform(
                MockMvcRequestBuilders
                        .delete("/cards/1/like")
                        .header(TOKEN_HEADER, ACCESS_TOKEN)
                        .with(SecurityMockMvcRequestPostProcessors.csrf())
        );

        // then
        resultActions.andDo(MockMvcResultHandlers.print());
        resultActions.andExpect(MockMvcResultMatchers.status().isNoContent());
    }
}