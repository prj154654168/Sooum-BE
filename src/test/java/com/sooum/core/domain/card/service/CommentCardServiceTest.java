package com.sooum.core.domain.card.service;

import com.sooum.core.domain.card.entity.CommentCard;
import com.sooum.core.domain.card.entity.parenttype.CardType;
import com.sooum.core.domain.card.repository.CommentCardRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CommentCardServiceTest {
    @Mock
    CommentCardRepository commentCardRepository;
    @InjectMocks
    CommentCardService commentCardService;

    @Test
    @DisplayName("카드가 피드인지 답글인지 확인")
    void findCardType_isFeedCard() throws Exception{
        // given
        Long mockId = 1L;
        given(commentCardRepository.existsById(mockId)).willReturn(true);

        // when
        CardType result = commentCardService.findCardType(mockId);

        // then
        Assertions.assertThat(result).isEqualTo(CardType.COMMENT_CARD);
    }

    @Test
    @DisplayName("lastPk가 empty일때 호출 첫페이지")
    void testCommentsReachMaxPageSize() throws Exception{
        //given
        Long memberPk = 1L;
        Optional<Long> lastPk = Optional.empty();
        List<CommentCard> expectedComments = createMockCommentsForMyComments();
        given(commentCardRepository.findCommentCardsFirstPage(anyLong(), any(PageRequest.class))).willReturn(expectedComments);

        List<CommentCard> actualComments = commentCardService.findCommentList(memberPk, lastPk);

        //then
        verify(commentCardRepository, times(1)).findCommentCardsFirstPage(anyLong(), any(PageRequest.class));
        Assertions.assertThat(actualComments.size()).isEqualTo(30);
    }

    @Test
    @DisplayName("lastPk가 존재할 때 호출 다음페이지")
    void testCommentsReachMaxPageSizes() throws Exception{
        //given
        Long lastPk = 100L;
        Long memberPk = 1L;
        List<CommentCard> expectedComments = createMockCommentsForMyComments();
        given(commentCardRepository.findCommentCardsNextPage(anyLong(), anyLong(), any(PageRequest.class))).willReturn(expectedComments);

        //when
        List<CommentCard> actualComments = commentCardService.findCommentList(memberPk, Optional.of(lastPk));

        //then
        Assertions.assertThat(actualComments.size()).isEqualTo(30);
        verify(commentCardRepository, times(1)).findCommentCardsNextPage(anyLong(),anyLong(),any(PageRequest.class));
    }

    private List<CommentCard> createMockCommentsForMyComments() {
        List<CommentCard> commentCards = new ArrayList<>();
        for (int i = 0; i < 30; i++) {
            CommentCard mockComment = mock(CommentCard.class);
            commentCards.add(mockComment);
        }
        return commentCards;
    }

}