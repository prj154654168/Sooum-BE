package com.sooum.core.domain.card.service;

import com.sooum.core.domain.card.entity.FeedCard;
import com.sooum.core.domain.card.entity.FeedLike;
import com.sooum.core.domain.card.repository.FeedLikeRepository;
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
    void feedLikeSave_Success() throws Exception{
        // given
        Member mockMember = mock(Member.class);
        FeedCard mockFeedCard = mock(FeedCard.class);
        FeedLike mockFeedLike = mock(FeedLike.class);
        given(feedLikeRepository.findFeedLiked(any(), any())).willReturn(Optional.empty());
        given(memberService.findByPk(any())).willReturn(mockMember);
        given(feedCardService.findByPk(any())).willReturn(mockFeedCard);
        given(feedLikeRepository.save(any())).willReturn(mockFeedLike);

        // when
        feedLikeService.createFeedLike(any(), any());

        // then
        verify(feedLikeRepository).save(any());
    }

    @Test
    @DisplayName("이미 존재하는 피드 카드 좋아요인 경우 exception")
    void feedLikeSave_isAlreadyExist() throws Exception{
        // given
        FeedLike mockFeedLike = mock(FeedLike.class);
        given(feedLikeRepository.findFeedLiked(any(), any())).willReturn(Optional.of(mockFeedLike));
        given(mockFeedLike.isDeleted()).willReturn(false);

        // when, then
        Assertions.assertThrows(EntityExistsException.class,
                () -> feedLikeService.createFeedLike(any(), any()));
    }

    @Test
    @DisplayName("피드 카드 좋아요가 soft delete된 상태면 재생성")
    void softDeleteFeedLike_restore() throws Exception{
        // given
        FeedLike mockFeedLike = mock(FeedLike.class);
        given(feedLikeRepository.findFeedLiked(any(), any())).willReturn(Optional.of(mockFeedLike));
        given(mockFeedLike.isDeleted()).willReturn(true);

        // when
        feedLikeService.createFeedLike(any(), any());

        // then
        verify(mockFeedLike).create();
    }

    @Test
    @DisplayName("피드 카드 좋아요 soft delete 성공")
    void feedLikeDelete_Success() throws Exception{
        // given
        FeedLike mockFeedLike = mock(FeedLike.class);
        given(feedLikeRepository.findFeedLiked(any(), any())).willReturn(Optional.of(mockFeedLike));

        // when
        feedLikeService.deleteFeedLike(1L, 1L);

        // then
        verify(mockFeedLike).delete();
    }

    @Test
    @DisplayName("삭제된 피드 카드 좋아요 재 삭제 요청할 경우 exception")
    void feedLikeDelete_isAlreadyDeleted() throws Exception{
        // given
        FeedLike mockFeedLike = mock(FeedLike.class);
        given(feedLikeRepository.findFeedLiked(any(), any())).willReturn(Optional.of(mockFeedLike));
        given(mockFeedLike.isDeleted()).willReturn(true);

        // when, then
        Assertions.assertThrows(EntityExistsException.class,
                () -> feedLikeService.deleteFeedLike(1L, 1L));
    }
}