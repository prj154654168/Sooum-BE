package com.sooum.core.domain.card.repository;

import com.sooum.core.domain.card.entity.CommentCard;
import com.sooum.core.domain.card.entity.CommentLike;
import com.sooum.core.domain.card.entity.FeedCard;
import com.sooum.core.domain.card.entity.FeedLike;
import com.sooum.core.domain.card.entity.font.Font;
import com.sooum.core.domain.card.entity.fontsize.FontSize;
import com.sooum.core.domain.card.entity.imgtype.ImgType;
import com.sooum.core.domain.card.entity.parenttype.CardType;
import com.sooum.core.domain.card.entity.parenttype.ParentType;
import com.sooum.core.domain.member.entity.Member;
import com.sooum.core.domain.member.entity.devicetype.DeviceType;
import com.sooum.core.domain.member.repository.MemberRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.transaction.annotation.Transactional;

@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@DataJpaTest
class CardLikeRepositoryTest {
    @Autowired
    MemberRepository memberRepository;
    @Autowired
    FeedLikeRepository feedLikeRepository;
    @Autowired
    FeedCardRepository feedCardRepository;
    @Autowired
    CommentCardRepository commentCardRepository;
    @Autowired
    CommentLikeRepository commentLikeRepository;

    @Test
    @Transactional
    @DisplayName("해당 피드 카드에 좋아요 존재")
    void isExistFeedLike() throws Exception{
        // given
        Member requester = createMember("requester");
        FeedCard feedCard = createFeedCard(createMember("writer"));
        createFeedLike(feedCard, requester);

        // when
        boolean isExistFeedLike = feedLikeRepository.existsByTargetCardPkAndLikedMemberPk(feedCard.getPk(), requester.getPk());

        // then
        Assertions.assertThat(isExistFeedLike).isTrue();
    }

    @Test
    @Transactional
    @DisplayName("해당 피드 카드에 좋아요 존재x")
    void isNotExistFeedLike() throws Exception{
        // given
        Member requester = createMember("requester");
        Member anotherUser = createMember("anotherUser");
        FeedCard feedCard = createFeedCard(createMember("writer"));
        createFeedLike(feedCard, requester);

        // when
        boolean isExistFeedLike = feedLikeRepository.existsByTargetCardPkAndLikedMemberPk(feedCard.getPk(), anotherUser.getPk());

        // then
        Assertions.assertThat(isExistFeedLike).isFalse();
    }

    @Test
    @Transactional
    @DisplayName("해당 댓글 카드에 좋아요 존재")
    void isExistCommentLike() throws Exception{
        // given
        Member requester = createMember("requester");
        Member writer = createMember("writer");
        FeedCard feedCard = createFeedCard(writer);
        CommentCard commentCard = createCommentCard(feedCard, writer);
        createCommentLike(commentCard, requester);

        // when
        boolean isExistFeedLike = commentLikeRepository.existsByTargetCardPkAndLikedMemberPk(commentCard.getPk(), requester.getPk());

        // then
        Assertions.assertThat(isExistFeedLike).isTrue();
    }

    @Test
    @Transactional
    @DisplayName("해당 댓 카드에 좋아요 존재x")
    void isNotExistCommentLike() throws Exception{
        // given
        Member requester = createMember("requester");
        Member writer = createMember("writer");
        Member anotherMember = createMember("anotherMember");
        FeedCard feedCard = createFeedCard(writer);
        CommentCard commentCard = createCommentCard(feedCard, writer);
        createCommentLike(commentCard, requester);

        // when
        boolean isExistFeedLike = commentLikeRepository.existsByTargetCardPkAndLikedMemberPk(commentCard.getPk(), anotherMember.getPk());

        // then
        Assertions.assertThat(isExistFeedLike).isFalse();
    }

    private void createFeedLike(FeedCard feedCard, Member member) {
        FeedLike feedLike = FeedLike.builder()
                .targetCard(feedCard)
                .likedMember(member)
                .build();
        feedLikeRepository.save(feedLike);
    }

    private void createCommentLike(CommentCard commentCard, Member member) {
        CommentLike commentLike = CommentLike.builder()
                .targetCard(commentCard)
                .likedMember(member)
                .build();
        commentLikeRepository.save(commentLike);
    }

    private FeedCard createFeedCard(Member member) {
            FeedCard feedCard = FeedCard.builder()
                    .content("카드 내용 ")
                    .fontSize(FontSize.BIG)
                    .font(Font.DEFAULT)
                    .location(null)
                    .imgType(ImgType.DEFAULT)
                    .imgName("1.jpg")
                    .isPublic(true)
                    .isStory(false)
                    .writer(member)
                    .build();
        return feedCardRepository.save(feedCard);
    }

    private CommentCard createCommentCard(FeedCard feedCard, Member writer) {
        CommentCard commentCard = CommentCard.builder()
                .content("카드 내용 ")
                .fontSize(FontSize.BIG)
                .font(Font.DEFAULT)
                .location(null)
                .imgType(ImgType.DEFAULT)
                .imgName("1.jpg")
                .isPublic(true)
                .isStory(false)
                .writer(writer)
                .parentCardType(CardType.FEED_CARD)
                .parentCardPk(feedCard.getPk())
                .masterCard(feedCard)
                .build();
        return commentCardRepository.save(commentCard);
    }

    private Member createMember(String nickname) {
        Member member = Member.builder()
                .deviceId(nickname)
                .deviceType(DeviceType.IOS)
                .firebaseToken("firbaseDummy")
                .nickname(nickname)
                .isAllowNotify(true)
                .build();
        return memberRepository.save(member);
    }
}