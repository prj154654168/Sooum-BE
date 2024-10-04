package com.sooum.core.domain.tag.service;

import com.sooum.core.domain.member.entity.Member;
import com.sooum.core.domain.member.service.MemberService;
import com.sooum.core.domain.tag.entity.FavoriteTag;
import com.sooum.core.domain.tag.entity.Tag;
import jakarta.persistence.EntityExistsException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.BDDMockito.*;

@ExtendWith(MockitoExtension.class)
class FavoriteTagServiceTest {
    @Mock
    TagService tagService;
    @Mock
    MemberService memberService;
    @Spy @InjectMocks
    FavoriteTagService favoriteTagService;

    @Test
    @DisplayName("이미 좋아요가 존재할 경우 실패")
    void saveFavoriteTag_isAlreadySave() throws Exception{
        // given
        given(tagService.isExistFavoriteTag(any(), any())).willReturn(true);

        // when, then
        Assertions.assertThrows(EntityExistsException.class, () -> favoriteTagService.saveFavoriteTag(any(), any()));
    }

    @Test
    @DisplayName("좋아요 저장 성공")
    void saveFavoriteTag_success() throws Exception{
        // given
        given(tagService.isExistFavoriteTag(any(), any())).willReturn(false);
        given(tagService.findTag(any())).willReturn(mock(Tag.class));
        given(memberService.findByPk(any())).willReturn(mock(Member.class));
        doNothing().when(tagService).saveFavoriteTag(any());

        // when
        favoriteTagService.saveFavoriteTag(any(), any());

        // then
        verify(favoriteTagService, timeout(1)).saveFavoriteTag(any(), any());
    }

    @Test
    @DisplayName("삭제 성공")
    void deleteFavoriteTag_success() throws Exception {
        // given
        given(tagService.findFavoriteTag(any(), any())).willReturn(mock(FavoriteTag.class));
        doNothing().when(tagService).deleteFavoriteTag(any());

        // when
        favoriteTagService.deleteFavoriteTag(any(), any());

        // then
        verify(favoriteTagService, timeout(1)).deleteFavoriteTag(any(), any());
    }


}