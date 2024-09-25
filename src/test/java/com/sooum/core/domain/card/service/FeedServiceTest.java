package com.sooum.core.domain.card.service;

import com.sooum.core.domain.card.entity.FeedCard;
import com.sooum.core.domain.member.entity.Member;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

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
        Long mockMemberPk = 1L;

        Member mockMember = mock(Member.class);
        FeedCard mockFeedCard = mock(FeedCard.class);

        given(commentCardService.hasChildCard(feedCardPk)).willReturn(true);
        given(feedCardService.findFeedCard(feedCardPk)).willReturn(mockFeedCard);
        given(mockMember.getPk()).willReturn(mockMemberPk);
        given(mockFeedCard.getWriter()).willReturn(mockMember);

        // when
        feedService.deleteFeedCard(feedCardPk,mockMemberPk);

        // then
        then(mockFeedCard).should().changeDeleteStatus();
    }

    @DisplayName("Test deleting a FeedCard without a CommentCard")
    @Test
    void deleteFeedCard_withoutCommentCard() {

        // given
        Long feedCardPk = 1L;
        Long mockMemberPk = 1L;

        Member mockMember = mock(Member.class);
        FeedCard mockFeedCard = mock(FeedCard.class);

        given(commentCardService.hasChildCard(feedCardPk)).willReturn(false);
        given(mockMember.getPk()).willReturn(mockMemberPk);
        given(mockFeedCard.getWriter()).willReturn(mockMember);
        given(feedCardService.findFeedCard(feedCardPk)).willReturn(mockFeedCard);
        willDoNothing().given(popularFeedService).deletePopularCard(feedCardPk);
        willDoNothing().given(feedLikeService).deleteAllFeedLikes(feedCardPk);

        // when
        feedService.deleteFeedCard(feedCardPk, mockMemberPk);

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