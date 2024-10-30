package com.sooum.core.domain.member.service;

import com.sooum.core.domain.img.service.ImgService;
import com.sooum.core.domain.member.dto.ProfileDto;
import com.sooum.core.domain.member.entity.Member;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class ProfileServiceTest {
    @Mock
    MemberService memberService;
    @Mock
    ImgService imgService;
    @InjectMocks
    ProfileService profileService;

    @Test
    @DisplayName("사진이 s3에 저장되지 않았거나 부적절한 카드일 경우 exception")
    void updateProfile_isNotValidImgCondition() throws Exception{
        // given
        given(imgService.isModeratingImg(any())).willReturn(true).willReturn(false);
        given(imgService.verifyImgSaved(any())).willReturn(false);

        // when, then
        // 부적절한 카드
        Assertions.assertThrows(EntityNotFoundException.class, () -> profileService.updateProfile(ProfileDto.ProfileUpdate.builder().build(), 1L));
        // 저장이 안된 카드
        Assertions.assertThrows(EntityNotFoundException.class, () -> profileService.updateProfile(ProfileDto.ProfileUpdate.builder().build(), 1L));
    }

    @Test
    @DisplayName("프로필 업데이트 성공")
    void updateProfile_success() throws Exception{
        // given
        Member mockMember = mock(Member.class);
        given(imgService.isModeratingImg(any())).willReturn(false);
        given(imgService.verifyImgSaved(any())).willReturn(true);
        given(memberService.findByPk(any())).willReturn(mockMember);

        // when
        profileService.updateProfile(ProfileDto.ProfileUpdate.builder().build(), any());

        // then
        verify(mockMember).updateProfile(any(), any());
    }
}