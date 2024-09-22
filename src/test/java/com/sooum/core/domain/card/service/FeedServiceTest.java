package com.sooum.core.domain.card.service;

import com.sooum.core.domain.card.entity.CommentCard;
import com.sooum.core.domain.card.entity.FeedCard;
import com.sooum.core.domain.card.entity.font.Font;
import com.sooum.core.domain.card.entity.fontsize.FontSize;
import com.sooum.core.domain.card.entity.imgtype.ImgType;
import com.sooum.core.domain.card.entity.parenttype.CardType;
import com.sooum.core.domain.card.repository.CommentCardRepository;
import com.sooum.core.domain.card.repository.FeedCardRepository;
import com.sooum.core.domain.member.entity.Member;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.locationtech.jts.geom.Point;
import org.mockito.BDDMockito;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.mockito.BDDMockito.*;


@ExtendWith(MockitoExtension.class)
class FeedServiceTest {

    @Mock
    private CommentCardService commentCardService;
    @Mock
    private FeedCardService feedCardService;
    @Mock
    private PopularFeedService popularFeedService;
    @Mock
    private FeedLikeService feedLikeService;

    @InjectMocks
    private FeedService feedService;

    @DisplayName("Test deleting a FeedCard when a CommentCard exists")
    @Test
    void deleteFeedCard_CommentCardExists() {

        // given
        Long feedCardPk = 1L;
        FeedCard mockFeedCard = mock(FeedCard.class);

        given(commentCardService.hasCommentCard(feedCardPk)).willReturn(true);
        given(feedCardService.findFeedCard(feedCardPk)).willReturn(mockFeedCard);

        // when
        feedService.deleteFeedCard(feedCardPk);

        // then
        then(mockFeedCard).should().changeDeleteStatus();
    }

    @DisplayName("Test deleting a FeedCard without a CommentCard")
    @Test
    void deleteFeedCard_withoutCommentCard() {

        // given
        Long feedCardPk = 1L;

        given(commentCardService.hasCommentCard(feedCardPk)).willReturn(false);
        willDoNothing().given(popularFeedService).deletePopularCard(feedCardPk);
        willDoNothing().given(feedLikeService).deleteAllFeedLikes(feedCardPk);

        // when
        feedService.deleteFeedCard(feedCardPk);

        // then
        then(feedCardService).should().deleteFeedCard(feedCardPk);
    }

//    @DisplayName("Delete parent card and current card")
//    @Test
//    void deleteParentCard_deleteCurrentCard() {
//
//        // given
//        Long commentCardPk = 1L;
//        CommentCard commentCard = CommentCard.builder().parentCardPk(1L).parentCardType(CardType.FEED_CARD).;
//        given(commentCardService.findCommentCard(commentCardPk)).willReturn(commentCard);
//        given(commentCardService.hasCommentCard(commentCardPk)).willReturn(false);
//        given(commentCardService.findChildCommentCardList(commentCardPk)).willReturn(List.of(commentCard));
//
//        feedService.deleteCommentCard();
//    }
//
//    @DisplayName("Delete parent card and mark current card as deleted")
//    @Test
//    void deleteParentCard_markCurrentCardAsDeleted() {}
//
//    @DisplayName("Mark parent card as deleted and delete current card")
//    @Test
//    void markParentCardAsDeleted_deleteCurrentCard() {}
//
//    @DisplayName("Mark parent card and current card as deleted")
//    @Test
//    void markParentCardAsDeleted_markCurrentCardAsDeleted() {}

}