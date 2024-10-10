package com.sooum.core.domain.card.service;

import com.sooum.core.domain.card.entity.CommentCard;
import com.sooum.core.domain.card.entity.CommentLike;
import com.sooum.core.domain.card.entity.FeedCard;
import com.sooum.core.domain.card.entity.font.Font;
import com.sooum.core.domain.card.entity.fontsize.FontSize;
import com.sooum.core.domain.card.entity.imgtype.ImgType;
import com.sooum.core.domain.card.entity.parenttype.CardType;
import com.sooum.core.domain.member.entity.Member;
import com.sooum.core.domain.member.entity.devicetype.DeviceType;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.ArrayList;
import java.util.List;

@ExtendWith(MockitoExtension.class)
public class FeedStaticServiceTest {
    private static final int MEMBER_SIZE = 3;
    private static final int FEED_SIZE = 12;
    private static final int COMMENT_SIZE = 36;

    @Test
    @DisplayName("내가 쓴 댓글이 존재")
    void isWrittenCommentCard_IsExist() throws Exception{
        // given
        List<Member> members = createMembers();
        List<FeedCard> feedCards = createFeedCard(members);
        List<CommentCard> commentCards = createCommentCards(feedCards, members);

        // when
        boolean isWrittenCommentCard = FeedService.isWrittenCommentCard(commentCards, 1L);

        // then
        Assertions.assertThat(isWrittenCommentCard).isTrue();
    }

    @Test
    @DisplayName("내가 쓴 댓글이 존재x")
    void isWrittenCommentCard_IsNotExist() throws Exception{
        // given
        List<Member> members = createMembers();
        List<FeedCard> feedCards = createFeedCard(members);
        List<CommentCard> commentCards = createCommentCards(feedCards, members);

        // when
        boolean isWrittenCommentCard = FeedService.isWrittenCommentCard(commentCards, 4L);

        // then
        Assertions.assertThat(isWrittenCommentCard).isFalse();
    }

    @Test
    @DisplayName("좋아요한 댓글이 존재")
    void isLiked_exist() throws Exception{
        // given
        List<Member> members = createMembers();
        List<FeedCard> feedCards = createFeedCard(members);
        List<CommentCard> commentCards = createCommentCards(feedCards, members);
        List<CommentLike> commentLikes = createCommentLike(commentCards, members);

        // when
        boolean isLiked = FeedService.isLiked(commentCards.get(0), commentLikes, members.get(0).getPk());

        // then
        Assertions.assertThat(isLiked).isTrue();
    }

    @Test
    @DisplayName("좋아요한 댓글이 존재x")
    void isLiked_notExist() throws Exception{
        // given
        List<Member> members = createMembers();
        List<FeedCard> feedCards = createFeedCard(members);
        List<CommentCard> commentCards = createCommentCards(feedCards, members);
        List<CommentLike> commentLikes = createCommentLike(commentCards, members);
        CommentCard outerCommentCard = createCommentCard(feedCards, createOuterMember());

        // when
        boolean isLiked = FeedService.isLiked(outerCommentCard, commentLikes, members.get(0).getPk());

        // then
        Assertions.assertThat(isLiked).isFalse();
    }

    @Test
    @DisplayName("좋아요 개수 세기")
    void countLikes() throws Exception{
        // given
        List<Member> members = createMembers();
        List<FeedCard> feedCards = createFeedCard(members);
        List<CommentCard> commentCards = createCommentCards(feedCards, members);
        List<CommentLike> commentLikes = createCommentLike(commentCards, members);

        // when
        int countResult = FeedService.countLikes(commentCards.get(0), commentLikes);

        // then
        Assertions.assertThat(countResult).isEqualTo(1);
    }

    private List<CommentLike> createCommentLike(List<CommentCard> commentCards, List<Member> members) {
        List<CommentLike> commentLikes = new ArrayList<>();
        for (int i = 0; i < COMMENT_SIZE; i++) {
            CommentLike commentLike = CommentLike.builder()
                    .targetCard(commentCards.get(i % COMMENT_SIZE))
                    .likedMember(members.get(i % MEMBER_SIZE))
                    .build();
            commentLikes.add(commentLike);
        }
        return commentLikes;
    }

    private CommentCard createCommentCard(List<FeedCard> feedCards, Member member) {
        CommentCard commentCard = CommentCard.builder()
                .content("카드 내용 ")
                .fontSize(FontSize.BIG)
                .font(Font.PRETENDARD)
                .location(null)
                .imgType(ImgType.DEFAULT)
                .imgName(1 + ".jpg")
                .writer(member)
                .masterCard(feedCards.get(feedCards.size() % FEED_SIZE).getPk())
                .parentCardType(CardType.COMMENT_CARD)
                .parentCardPk(feedCards.get(feedCards.size() % FEED_SIZE).getPk())
                .build();
        ReflectionTestUtils.setField(commentCard, "pk", (long) COMMENT_SIZE + 1);
        return commentCard;
    }

    private List<CommentCard> createCommentCards(List<FeedCard> feedCards, List<Member> members) {
        ArrayList<CommentCard> commentCards = new ArrayList<>();
        for (int i = 0; i < COMMENT_SIZE; i++) {
            CommentCard commentCard = CommentCard.builder()
                    .content("카드 내용 " + i)
                    .fontSize(FontSize.BIG)
                    .font(Font.PRETENDARD)
                    .location(null)
                    .imgType(ImgType.DEFAULT)
                    .imgName(i + ".jpg")
                    .writer(members.get(i % MEMBER_SIZE))
                    .masterCard(feedCards.get(i % FEED_SIZE).getPk())
                    .parentCardType(i % 2 == 0 ? CardType.FEED_CARD : CardType.COMMENT_CARD)
                    .parentCardPk(feedCards.get(i % FEED_SIZE).getPk())
                    .build();
            ReflectionTestUtils.setField(commentCard, "pk", (long) i);
            commentCards.add(commentCard);
        }
        return commentCards;
    }

    private List<FeedCard> createFeedCard(List<Member> members) {
        ArrayList<FeedCard> feedCards = new ArrayList<>();
        for (int i = 0; i < FEED_SIZE; i++) {
            FeedCard feedCard = FeedCard.builder()
                    .content("카드 내용 ")
                    .fontSize(FontSize.BIG)
                    .font(Font.PRETENDARD)
                    .location(null)
                    .imgType(ImgType.DEFAULT)
                    .imgName(1 + ".jpg")
                    .isPublic(true)
                    .isStory(false)
                    .writer(members.get(i % MEMBER_SIZE))
                    .build();
            ReflectionTestUtils.setField(feedCard, "pk", (long) i);
            feedCards.add(feedCard);
        }
        return feedCards;
    }

    private List<Member> createMembers() {
        List<Member> members = new ArrayList<>();
        for (int i = 0; i < MEMBER_SIZE; i++) {
            Member member = Member.builder()
                    .deviceId("dev")
                    .deviceType(DeviceType.IOS)
                    .firebaseToken("firbaseDummy")
                    .nickname("nickname")
                    .isAllowNotify(true)
                    .build();
            ReflectionTestUtils.setField(member, "pk", (long) i);
            members.add(member);
        }
        return members;
    }

    private Member createOuterMember() {
        Member member = Member.builder()
                .deviceId("dev")
                .deviceType(DeviceType.IOS)
                .firebaseToken("firbaseDummy")
                .nickname("nickname")
                .isAllowNotify(true)
                .build();
        ReflectionTestUtils.setField(member, "pk", (long) MEMBER_SIZE + 1);
        return member;
    }
}
