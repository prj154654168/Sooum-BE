package com.sooum.core.domain.card.controller;

import com.sooum.core.domain.card.dto.PopularCardDto;
import com.sooum.core.domain.card.dto.popularitytype.PopularityType;
import com.sooum.core.domain.card.entity.font.Font;
import com.sooum.core.domain.card.entity.fontsize.FontSize;
import com.sooum.core.domain.card.service.PopularFeedService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.BDDMockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.web.context.WebApplicationContext;

import javax.swing.text.html.Option;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.*;

@SpringBootTest
@AutoConfigureMockMvc
class PopularFeedControllerTest {
    @Autowired
    MockMvc mockMvc;
    @Autowired
    private WebApplicationContext context;
    @MockBean
    PopularFeedService popularFeedService;

    @BeforeEach
    public void init(){
        this.mockMvc = MockMvcBuilders.webAppContextSetup(context).build();
    }

    @Test
    @WithMockUser
    void findHomePopularFeeds() throws Exception{
        /// given
        given(popularFeedService.findHomePopularFeeds(any(), any(), any())).willReturn(createPopularFeedsDto());

        // when
        ResultActions resultActions = mockMvc.perform(
                MockMvcRequestBuilders.get("/cards/home/popular")
        );

        // then
//        resultActions.andExpect(MockMvcResultMatchers.status().isOk());
        // todo security 포함한 테스트로 변경
        resultActions.andDo(MockMvcResultHandlers.print());
    }

    public List<PopularCardDto.PopularCardRetrieve> createPopularFeedsDto() {
        ArrayList<PopularCardDto.PopularCardRetrieve> responses = new ArrayList<>();
        for (int i = 0; i < 2; i++) {
            PopularCardDto.PopularCardRetrieve dto = PopularCardDto.PopularCardRetrieve.builder()
                    .id(i)
                    .contents("내용" + i)
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
                    .popularityType(PopularityType.LIKE)
                    .build();
            responses.add(dto);
        }
        return responses;
    }


}