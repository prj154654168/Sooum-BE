package com.sooum.core.domain.card.service;

import com.sooum.core.domain.card.entity.CommentCard;
import com.sooum.core.domain.card.entity.FeedCard;
import com.sooum.core.domain.card.entity.FeedLike;
import com.sooum.core.domain.card.entity.font.Font;
import com.sooum.core.domain.card.entity.fontsize.FontSize;
import com.sooum.core.domain.card.entity.imgtype.ImgType;
import com.sooum.core.domain.card.entity.parenttype.ParentType;
import com.sooum.core.domain.card.repository.FeedLikeRepository;
import com.sooum.core.domain.member.entity.Member;
import com.sooum.core.domain.member.entity.devicetype.DeviceType;
import com.sooum.core.domain.member.service.MemberService;
import jakarta.persistence.EntityExistsException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.mockito.BDDMockito.*;

@ExtendWith(MockitoExtension.class)
class FeedLikeServiceTest {
    @Mock
    MemberService memberService;
    @Mock
    FeedCardService feedCardService;
    @Mock
    FeedLikeRepository feedLikeRepository;
    @InjectMocks
    FeedLikeService feedLikeService;

    @Test
    @DisplayName("피드 카드 좋아요 성공")
    void feedLikeSaveSuccess() throws Exception{
        // given
        Member member = createMember();
        FeedCard feedCard = createFeedCard(member);
        FeedLike feedLike = createFeedLike(feedCard, member);
        given(feedLikeRepository.existsByTargetCardPkAndLikedMemberPk(any(), any())).willReturn(false);
        given(memberService.findByPk(any())).willReturn(member);
        given(feedCardService.findByPk(any())).willReturn(feedCard);
        given(feedLikeRepository.save(any())).willReturn(feedLike);

        // when
        feedLikeService.createFeedLike(1L, 1L);

        // then
        verify(feedLikeRepository).save(any());
    }

    @Test
    @DisplayName("피드 카드 좋아요 실패")
    void feedLikeSaveFail() throws Exception{
        // given
        given(feedLikeRepository.existsByTargetCardPkAndLikedMemberPk(any(), any())).willReturn(true);

        // when, then
        Assertions.assertThrows(EntityExistsException.class,
                () -> feedLikeService.createFeedLike(1L, 1L));
    }

    @Test
    @DisplayName("피드 카드 좋아요 삭제 성공")
    void feedLikeDeleteSuccess() throws Exception{
        // given
        Member member = createMember();
        FeedCard feedCard = createFeedCard(member);
        FeedLike feedLike = createFeedLike(feedCard, member);
        given(feedLikeRepository.findFeedLiked(any(), any())).willReturn(Optional.of(feedLike));
        doNothing().when(feedLikeRepository).delete(any());

        // when
        feedLikeService.deleteFeedLike(1L, 1L);

        // then
        verify(feedLikeRepository).delete(any());
    }

    private FeedLike createFeedLike(FeedCard feedCard, Member member) {
        return FeedLike.builder()
                .targetCard(feedCard)
                .likedMember(member)
                .build();
    }

    private FeedCard createFeedCard(Member member) {
        return FeedCard.builder()
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
    }

    private CommentCard createCommentCard(FeedCard feedCard, Member writer) {
        return CommentCard.builder()
                .content("카드 내용 ")
                .fontSize(FontSize.BIG)
                .font(Font.DEFAULT)
                .location(null)
                .imgType(ImgType.DEFAULT)
                .imgName("1.jpg")
                .isPublic(true)
                .isStory(false)
                .writer(writer)
                .parentCardType(ParentType.FEED_CARD)
                .parentCardPk(feedCard.getPk())
                .masterCard(feedCard)
                .build();
    }

    private Member createMember() {
        return Member.builder()
                .deviceId("device")
                .deviceType(DeviceType.IOS)
                .firebaseToken("firbaseDummy")
                .nickname("nickname")
                .isAllowNotify(true)
                .build();
    }
}