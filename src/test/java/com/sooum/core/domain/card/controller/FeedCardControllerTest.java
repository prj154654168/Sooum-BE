package com.sooum.core.domain.card.controller;

import com.sooum.core.domain.card.dto.TagFeedCardDto;
import com.sooum.core.domain.card.service.DetailFeedService;
import com.sooum.core.domain.card.service.FeedService;
import com.sooum.core.domain.card.service.TagFeedService;
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
import org.springframework.hateoas.Link;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.Collections;
import java.util.List;

import static org.mockito.BDDMockito.*;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@WebMvcTest(FeedCardController.class)
@MockBean(JpaMetamodelMappingContext.class)
@MockBean(JwtBlacklistInterceptor.class)
@MockBean(WebMvcConfig.class)
@MockBean(TokenProvider.class)
@MockBean(JobExplorer.class)
@MockBean(JobOperator.class)
@MockBean(JobRepository.class)
@MockBean(CachedTagRepository.class)
@Import(value = { SecurityConfig.class, RedisConfig.class})
class FeedCardControllerTest {
    @Autowired
    MockMvc mockMvc;
    @MockBean
    FeedService feedService;
    @MockBean
    DetailFeedService detailFeedService;
    @MockBean
    TagFeedService tagFeedService;

    @Test
    @DisplayName("태그에 해당하는 글이 존재x")
    @WithMockUser
    void findTagFeeds_NotExist() throws Exception{
        // given
        given(tagFeedService.createTagFeedsInfo(any(), any(), any(), any(), any())).willReturn(Collections.emptyList());

        // when
        ResultActions resultActions = mockMvc.perform(
                MockMvcRequestBuilders
                        .get("/cards/tags/{tagPk}", 1L)
        );

        // then
        resultActions.andExpect(MockMvcResultMatchers.status().isNoContent());
    }

    @Test
    @DisplayName("태그에 해당하는 글이 존재")
    @WithMockUser
    void findTagFeeds_Exist() throws Exception{
        // given
        List<TagFeedCardDto> mockResult = List.of(mock(TagFeedCardDto.class));
        Link mockLink = linkTo(methodOn(FeedCardController.class).getClass()).slash("/").withSelfRel();
        given(tagFeedService.createTagFeedsInfo(any(), any(), any(), any(), any())).willReturn(mockResult);
        given(tagFeedService.createNextTagFeedsUrl(any(), any())).willReturn(mockLink);

        // when
        ResultActions resultActions = mockMvc.perform(
                MockMvcRequestBuilders
                        .get("/cards/tags/{tagPk}", 1L)
        );

        // then
        resultActions.andExpect(MockMvcResultMatchers.status().isOk());
    }
}