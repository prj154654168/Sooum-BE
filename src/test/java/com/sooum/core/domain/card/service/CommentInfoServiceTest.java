package com.sooum.core.domain.card.service;

import com.sooum.core.domain.block.service.BlockMemberService;
import com.sooum.core.domain.card.entity.CommentCard;
import com.sooum.core.domain.card.entity.parenttype.CardType;
import com.sooum.core.domain.img.service.ImgService;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.mockito.BDDMockito.*;

@ExtendWith(MockitoExtension.class)
class CommentInfoServiceTest {
    @Mock
    BlockMemberService blockMemberService;
    @Mock
    ImgService imgService;
    @Mock
    CommentLikeService commentLikeService;
    @Mock
    CommentCardService commentCardService;
    @InjectMocks @Spy
    CommentInfoService commentInfoService;
    private static final int MAX_PAGE_SIZE = 100;

    @Test
    @DisplayName("한번에 댓글 50개 채우는 지 확인")
    void testCommentsReach50_ByOneTime() throws Exception{
        // given
        List<CommentCard> mockComments = createMockComments(MAX_PAGE_SIZE);
        List<CommentCard> filterMockComments = createMockComments(83);
        given(commentCardService.findCommentsByLastPk(any(), any(), any())).willReturn(mockComments);
        given(blockMemberService.filterBlockedMembers(eq(mockComments), any())).willReturn(filterMockComments);

        // when
        List<CommentCard> comments = commentInfoService.findDefaultPageSizeComments(Optional.of(1L), CardType.FEED_CARD, 1L, 1L);

        // then
        Assertions.assertThat(comments.size()).isEqualTo(50);
        verify(commentCardService, times(1)).findCommentsByLastPk(any(),any(),any());
    }

    @Test
    @DisplayName("두번에 댓글 50개 채우는 지 확인")
    void testCommentsReach50_ByTwoTime() throws Exception{
        // given
        List<CommentCard> mockComments = createMockComments(MAX_PAGE_SIZE);
        List<CommentCard> filterMockComments = createMockComments(40);
        given(commentCardService.findCommentsByLastPk(any(), any(), any())).willReturn(mockComments);
        given(blockMemberService.filterBlockedMembers(eq(mockComments), any())).willReturn(filterMockComments);

        // when
        List<CommentCard> comments = commentInfoService.findDefaultPageSizeComments(Optional.of(1L), CardType.FEED_CARD, 1L, 1L);

        // then
        Assertions.assertThat(comments.size()).isEqualTo(50);
        verify(commentCardService, times(2)).findCommentsByLastPk(any(),any(),any());
    }

    private List<CommentCard> createMockComments(int size) {
        CommentCard mockComment = mock(CommentCard.class);
        List<CommentCard> commentCards = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            commentCards.add(mockComment);
        }
        return commentCards;
    }
}