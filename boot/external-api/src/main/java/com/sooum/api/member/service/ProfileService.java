package com.sooum.api.member.service;

import com.sooum.api.img.service.ImgService;
import com.sooum.api.member.dto.ProfileDto;
import com.sooum.data.card.service.FeedCardService;
import com.sooum.data.follow.service.FollowService;
import com.sooum.data.img.entity.ProfileImg;
import com.sooum.data.img.service.ProfileImgService;
import com.sooum.data.member.entity.Member;
import com.sooum.data.member.service.MemberService;
import com.sooum.data.visitor.service.VisitorService;
import com.sooum.global.exceptionmessage.ExceptionMessage;
import com.sooum.global.regex.BadWordFiltering;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProfileService {
    private final VisitorService visitorService;
    private final MemberService memberService;
    private final FeedCardService feedCardService;
    private final FollowService followService;
    private final ImgService imgService;
    private final BadWordFiltering badWordFiltering;
    private final ProfileImgService profileImgService;

    private static final List<String> FORBIDDEN_NICKNAME = List.of("숨 운영자", "숨 운영진", "숨 관리자", "숨 관리진", "운영자", "운영진", "관리자", "관리진");

    public ProfileDto.NicknameAvailableResponse verifyNicknameAvailable(String nickname) {
        return ProfileDto.NicknameAvailableResponse.builder()
                .isAvailable(isAvailableNickname(nickname))
                .build();
    }

    private boolean isAvailableNickname(String nickname) {
        return !nickname.isBlank() && !badWordFiltering.isBadWord(nickname) && !FORBIDDEN_NICKNAME.contains(nickname);
    }

    @Transactional
    public void updateProfile(ProfileDto.ProfileUpdate profileUpdate, Long memberPk) {
        Member member = memberService.findMember(memberPk);

        updateProfileImg(profileUpdate.getProfileImg(), member);
        member.updateNickname(profileUpdate.getNickname());
    }

    private void updateProfileImg(String profileImgName, Member profileOwner) {
        if (profileImgName == null || profileImgName.isBlank()) {
            return;
        }

        if(!imgService.isProfileImgSaved(profileImgName)) {
            throw new EntityNotFoundException(ExceptionMessage.IMAGE_NOT_FOUND.getMessage());
        }
        if (imgService.isModeratingProfileImg(profileImgName)) {
            throw new EntityNotFoundException(ExceptionMessage.IMAGE_REJECTED_BY_MODERATION.getMessage());
        }

        ProfileImg profileImg = profileImgService.findProfileImg(profileImgName);
        profileImg.updateProfileOwner(profileOwner);

        profileOwner.updateProfileImgName(profileImgName);
    }
}
