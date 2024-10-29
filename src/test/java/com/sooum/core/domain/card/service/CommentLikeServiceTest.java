package com.sooum.core.domain.card.service;

import com.sooum.core.domain.card.entity.CommentCard;
import com.sooum.core.domain.card.entity.CommentLike;
import com.sooum.core.domain.card.repository.CommentLikeRepository;
import com.sooum.core.domain.member.entity.Member;
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
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.mock;

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
    @DisplayName("답글 카드 좋아요 성공")
    void commentLikeSave_Success() throws Exception{
        // given
        Member mockMember = mock(Member.class);
        CommentCard mockCommentCard = mock(CommentCard.class);
        CommentLike mockCommentLike = mock(CommentLike.class);
        given(commentLikeRepository.findCommentLiked(any(), any())).willReturn(Optional.empty());
        given(memberService.findByPk(any())).willReturn(mockMember);
        given(commentCardService.findByPk(any())).willReturn(mockCommentCard);
        given(commentLikeRepository.save(any())).willReturn(mockCommentLike);

        // when
        commentLikeService.createCommentLike(any(), any());

        // then
        verify(commentLikeRepository).save(any());
    }

    @Test
    @DisplayName("이미 존재하는 답글 카드 좋아요인 경우 exception")
    void commentLikeSave_isAlreadyExist() throws Exception{
        // given
        CommentLike mockCommentLike = mock(CommentLike.class);
        given(commentLikeRepository.findCommentLiked(any(), any())).willReturn(Optional.of(mockCommentLike));
        given(mockCommentLike.isDeleted()).willReturn(false);

        // when, then
        Assertions.assertThrows(EntityExistsException.class,
                () -> commentLikeService.createCommentLike(any(), any()));
    }

    @Test
    @DisplayName("답글 카드 좋아요가 soft delete된 상태면 재생성")
    void softDeleteCommentLike_restore() throws Exception{
        // given
        CommentLike mockCommentLike = mock(CommentLike.class);
        given(commentLikeRepository.findCommentLiked(any(), any())).willReturn(Optional.of(mockCommentLike));
        given(mockCommentLike.isDeleted()).willReturn(true);

        // when
        commentLikeService.createCommentLike(any(), any());

        // then
        verify(mockCommentLike).create();
    }

    @Test
    @DisplayName("답글 카드 좋아요 soft delete 성공")
    void commentLikeDelete_Success() throws Exception{
        // given
        CommentLike mockCommentLike = mock(CommentLike.class);
        given(commentLikeRepository.findCommentLiked(any(), any())).willReturn(Optional.of(mockCommentLike));

        // when
        commentLikeService.deleteCommentLike(1L, 1L);

        // then
        verify(mockCommentLike).delete();
    }

    @Test
    @DisplayName("삭제된 답글 카드 좋아요 재 삭제 요청할 경우 exception")
    void commentLikeDelete_isAlreadyDeleted() throws Exception{
        // given
        CommentLike mockCommentLike = mock(CommentLike.class);
        given(commentLikeRepository.findCommentLiked(any(), any())).willReturn(Optional.of(mockCommentLike));
        given(mockCommentLike.isDeleted()).willReturn(true);

        // when, then
        Assertions.assertThrows(EntityExistsException.class,
                () -> commentLikeService.deleteCommentLike(1L, 1L));
    }
}