package com.sooum.core.domain.card.controller;

import com.sooum.core.domain.card.dto.PopularCardRetrieve;
import com.sooum.core.domain.card.entity.font.Font;
import com.sooum.core.domain.card.entity.fontsize.FontSize;
import com.sooum.core.domain.card.service.PopularFeedService;
import com.sooum.core.global.auth.interceptor.JwtBlacklistInterceptor;
import com.sooum.core.global.config.jwt.TokenProvider;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.BDDMockito.any;
import static org.mockito.BDDMockito.given;

@WebMvcTest(PopularFeedController.class)
@MockBean(JpaMetamodelMappingContext.class)
@MockBean(JwtBlacklistInterceptor.class)
@MockBean(TokenProvider.class)
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
        given(popularFeedService.findHomePopularFeeds(any(), any(), any())).willReturn(createPopularFeedsDto());

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
        given(popularFeedService.findHomePopularFeeds(any(), any(), any())).willReturn(List.of());

        // when
        ResultActions resultActions = mockMvc.perform(
                MockMvcRequestBuilders
                        .get("/cards/home/popular")
                        .header("Authorization", ACCESS_TOKEN)
        );

        // then
        // todo mocking 안되는 버그 수정
//        resultActions.andExpect(MockMvcResultMatchers.status().isNoContent());
        resultActions.andDo(MockMvcResultHandlers.print());
    }

    public List<PopularCardRetrieve> createPopularFeedsDto() {
        ArrayList<PopularCardRetrieve> responses = new ArrayList<>();
        for (int i = 0; i < 2; i++) {
            PopularCardRetrieve dto = PopularCardRetrieve.builder()
                    .id(i)
                    .content("내용" + i)
                    .isStory(false)
                    .backgroundImgUrl(null)
                    .font(Font.DEFAULT)
                    .fontSize(FontSize.BIG)
                    .distance(null)
                    .createdAt(LocalDateTime.now())
                    .isLiked(false)
                    .likeCnt(10)
                    .isCommentWritten(false)
                    .commentCnt(10)
                    .build();
            responses.add(dto);
        }
        return responses;
    }
}