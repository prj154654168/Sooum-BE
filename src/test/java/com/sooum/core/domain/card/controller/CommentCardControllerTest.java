package com.sooum.core.domain.card.controller;

import com.sooum.core.domain.card.dto.CommentDto;
import com.sooum.core.domain.card.entity.parenttype.CardType;
import com.sooum.core.domain.card.service.CommentCardService;
import com.sooum.core.domain.card.service.CommentInfoService;
import com.sooum.core.domain.card.service.CommentLikeService;
import com.sooum.core.domain.card.service.FeedService;
import com.sooum.core.global.auth.interceptor.JwtBlacklistInterceptor;
import com.sooum.core.global.config.jwt.TokenProvider;
import com.sooum.core.global.config.mvc.WebMvcConfig;
import com.sooum.core.global.config.security.SecurityConfig;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
import org.springframework.http.HttpStatus;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.util.Collections;
import java.util.List;

import static org.mockito.BDDMockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(CommentCardController.class)
@MockBean(JpaMetamodelMappingContext.class)
@MockBean(JwtBlacklistInterceptor.class)
@MockBean(WebMvcConfig.class)
@MockBean(TokenProvider.class)
@Import(SecurityConfig.class)
class CommentCardControllerTest {
    @Autowired
    MockMvc mockMvc;
    @MockBean
    CommentLikeService commentLikeService;
    @MockBean
    FeedService feedService;
    @MockBean
    CommentCardService commentCardService;
    @MockBean
    CommentInfoService commentInfoService;
    private static final String ACCESS_TOKEN = "Bearer eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJpYXQiOjE3MjY5Mjk0MzMsImV4cCI6MTAxNzI2OTI5NDMzLCJzdWIiOiJBY2Nlc3NUb2tlbiIsImlkIjo2MjUwNDc5NzMyNTA1MTUxNTMsInJvbGUiOiJVU0VSIn0.aL4Tr3FaSwvu9hOQISAvGJfCHBGCV9jRo_BfTQkBssU";
    private static final String TOKEN_HEADER = "Authorization";

    @Test
    @DisplayName("댓글 카드 좋아요 성공")
    @WithMockUser
    void createCommentLikeSuccess() throws Exception{
        // given
        doNothing().when(commentLikeService).createCommentLike(any(), any());

        // when
        ResultActions resultActions = mockMvc.perform(
                post("/comments/{cardPk}/like", 1L)
                        .header(TOKEN_HEADER, ACCESS_TOKEN)
        );

        // then
        resultActions.andExpect(status().isCreated());
        resultActions.andExpect(jsonPath("$.httpCode").value(HttpStatus.CREATED.value()));
    }

    @Test
    @DisplayName("댓글 카드 좋아요 삭제")
    @WithMockUser
    void deleteCommentLikeSuccess() throws Exception{
        // given
        doNothing().when(commentLikeService).deleteCommentLike(any(), any());

        // when
        ResultActions resultActions = mockMvc.perform(
                delete("/comments/{cardPk}/like", 1)
                        .header(TOKEN_HEADER, ACCESS_TOKEN)
        );

        // then
        resultActions.andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("댓글이 없어서 조회가 안되는 경우")
    @WithMockUser
    void createCommentCardsInfo_notExistComment() throws Exception{
        // given
        given(commentInfoService.createCommentsInfo(any(), any(), any(), any(), any(), any())).willReturn(Collections.emptyList());

        // when
        ResultActions resultActions = mockMvc.perform(
                get("/comments/current/{cardPk}", 1L)
                        .param("parentCardType", CardType.FEED_CARD.toString())
                        .header(TOKEN_HEADER, ACCESS_TOKEN)
        );

        // then
        resultActions.andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("댓글 조회가 되는 경우")
    @WithMockUser
    void createCommentCardsInfo_existComment() throws Exception{
        // given
        List<CommentDto.CommentCardsInfo> mockResult = List.of(mock(CommentDto.CommentCardsInfo.class));
        given(commentInfoService.createCommentsInfo(any(), any(), any(), any(), any(), any())).willReturn(mockResult);

        // when
        ResultActions resultActions = mockMvc.perform(
                get("/comments/current/{cardPk}", 1L)
                        .param("parentCardType", CardType.FEED_CARD.toString())
                        .header(TOKEN_HEADER, ACCESS_TOKEN)
        );

        // then
        resultActions.andExpect(status().isOk());
    }
}