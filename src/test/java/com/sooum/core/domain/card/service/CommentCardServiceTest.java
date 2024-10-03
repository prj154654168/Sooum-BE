package com.sooum.core.domain.card.service;

import com.sooum.core.domain.card.entity.parenttype.CardType;
import com.sooum.core.domain.card.repository.CommentCardRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.BDDMockito.given;

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

}