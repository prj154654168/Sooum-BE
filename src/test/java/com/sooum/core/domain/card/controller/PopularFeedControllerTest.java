package com.sooum.core.domain.card.controller;

import com.sooum.core.domain.card.dto.PopularCardRetrieve;
import com.sooum.core.domain.card.service.PopularFeedService;
import com.sooum.core.domain.tag.repository.CachedTagRepository;
import com.sooum.core.global.auth.interceptor.JwtBlacklistInterceptor;
import com.sooum.core.global.config.jwt.TokenProvider;
import com.sooum.core.global.config.mvc.WebMvcConfig;
import com.sooum.core.global.config.redis.RedisConfig;
import com.sooum.core.global.config.security.SecurityConfig;
import org.junit.jupiter.api.Test;
import org.springframework.batch.core.explore.JobExplorer;
import org.springframework.batch.core.launch.JobOperator;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.Collections;
import java.util.List;

import static org.mockito.BDDMockito.*;

@WebMvcTest(PopularFeedController.class)
@MockBean(JpaMetamodelMappingContext.class)
@MockBean(JwtBlacklistInterceptor.class)
@MockBean(WebMvcConfig.class)
@MockBean(TokenProvider.class)
@MockBean(JobExplorer.class)
@MockBean(JobOperator.class)
@MockBean(JobRepository.class)
@MockBean(CachedTagRepository.class)
@Import(value = { SecurityConfig.class, RedisConfig.class})
class PopularFeedControllerTest {
    @Autowired
    MockMvc mockMvc;
    @MockBean
    PopularFeedService popularFeedService;
    private static final String ACCESS_TOKEN = "Bearer eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJpYXQiOjE3MjY5Mjk0MzMsImV4cCI6MTAxNzI2OTI5NDMzLCJzdWIiOiJBY2Nlc3NUb2tlbiIsImlkIjo2MjUwNDc5NzMyNTA1MTUxNTMsInJvbGUiOiJVU0VSIn0.aL4Tr3FaSwvu9hOQISAvGJfCHBGCV9jRo_BfTQkBssU";

    @Test
    @WithMockUser
    void findHomePopularFeeds() throws Exception{
        /// given
        List<PopularCardRetrieve> mockResult = List.of(mock(PopularCardRetrieve.class));
        given(popularFeedService.findHomePopularFeeds(any(), any(), any())).willReturn(mockResult);

        // when
        ResultActions resultActions = mockMvc.perform(
                MockMvcRequestBuilders
                        .get("/cards/home/popular")
                        .header("Authorization", ACCESS_TOKEN)
        );

        // then
        resultActions.andExpect(MockMvcResultMatchers.status().isOk());
        resultActions.andDo(MockMvcResultHandlers.print());
    }

    @Test
    @WithMockUser
    void findEmptyFeed() throws Exception{
        /// given
        given(popularFeedService.findHomePopularFeeds(any(), any(), any())).willReturn(Collections.emptyList());

        // when
        ResultActions resultActions = mockMvc.perform(
                MockMvcRequestBuilders
                        .get("/cards/home/popular")
                        .header("Authorization", ACCESS_TOKEN)
        );

        // then
        resultActions.andExpect(MockMvcResultMatchers.status().isNoContent());
        resultActions.andDo(MockMvcResultHandlers.print());
    }
}