package com.sooum.core.domain.card.service;

import com.sooum.core.domain.card.entity.CommentCard;
import com.sooum.core.domain.card.entity.CommentLike;
import com.sooum.core.domain.card.entity.FeedCard;
import com.sooum.core.domain.card.entity.font.Font;
import com.sooum.core.domain.card.entity.fontsize.FontSize;
import com.sooum.core.domain.card.entity.imgtype.ImgType;
import com.sooum.core.domain.card.entity.parenttype.ParentType;
import com.sooum.core.domain.card.repository.CommentLikeRepository;
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

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class CommentLikeServiceTest {
    @Mock
    MemberService memberService;
    @Mock
    CommentCardService commentCardService;
    @Mock
    CommentLikeRepository commentLikeRepository;
    @InjectMocks
    CommentLikeService commentLikeService;

    @Test
    @DisplayName("댓글 카드 좋아요 성공")
    void commentLikeSaveSuccess() throws Exception{
        // given
        Member member = createMember();
        FeedCard feedCard = createFeedCard(member);
        CommentCard commentCard = createCommentCard(feedCard, member);
        CommentLike commentLike = createCommentLike(commentCard, member);
        given(commentLikeRepository.existsByTargetCardPkAndLikedMemberPk(any(), any())).willReturn(false);
        given(memberService.findByPk(any())).willReturn(member);
        given(commentCardService.findByPk(any())).willReturn(commentCard);
        given(commentLikeRepository.save(any())).willReturn(commentLike);

        // when
        commentLikeService.createCommentLike(1L, 1L);

        // then
        verify(commentLikeRepository).save(any());
    }

    @Test
    @DisplayName("댓글 카드 좋아요 실패")
    void commentLikeSaveFail() throws Exception{
        // given
        given(commentLikeRepository.existsByTargetCardPkAndLikedMemberPk(any(), any())).willReturn(true);

        // when, then
        Assertions.assertThrows(EntityExistsException.class,
                () -> commentLikeService.createCommentLike(1L, 1L));
    }

    @Test
    @DisplayName("댓글 카드 좋아요 삭제 성공")
    void commentLikeDeleteSuccess() throws Exception{
        // given
        Member member = createMember();
        FeedCard feedCard = createFeedCard(member);
        CommentCard commentCard = createCommentCard(feedCard, member);
        CommentLike commentLike = createCommentLike(commentCard, member);
        given(commentLikeRepository.findCommentLiked(any(), any())).willReturn(Optional.of(commentLike));
        doNothing().when(commentLikeRepository).delete(any());

        // when
        commentLikeService.deleteCommentLike(1L, 1L);

        // then
        verify(commentLikeRepository).delete(any());
    }

    private CommentLike createCommentLike(CommentCard commentCard, Member member) {
        return CommentLike.builder()
                .targetCard(commentCard)
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