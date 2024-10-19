package com.sooum.core.domain.member.service;

import com.sooum.core.domain.card.service.FeedCardService;
import com.sooum.core.domain.follow.service.FollowService;
import com.sooum.core.domain.img.service.ImgService;
import com.sooum.core.domain.member.dto.MemberDto;
import com.sooum.core.domain.member.dto.ProfileDto;
import com.sooum.core.domain.member.entity.Member;
import com.sooum.core.domain.visitor.service.VisitorService;
import com.sooum.core.global.exceptionmessage.ExceptionMessage;
import com.sooum.core.global.regex.BadWordFiltering;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ProfileService {
    private final VisitorService visitorService;
    private final MemberService memberService;
    private final FeedCardService feedCardService;
    private final FollowService followService;
    private final ImgService imgService;
    private final BadWordFiltering badWordFiltering;

    @Transactional
    public ProfileDto.ProfileInfoResponse findProfileInfo(Long profileOwnerPk, Long memberPk) {
        Member profileOwner = memberService.findByPk(profileOwnerPk);
        Member visitor = memberService.findByPk(memberPk);
        saveVisitor(profileOwner, visitor);

        Long totalVisitorCnt = memberService.findTotalVisitorCnt(profileOwner);
        Long currentDateVisitorCnt = visitorService.findCurrentDateVisitorCnt(profileOwner);
        Long feedCardCnt = feedCardService.findFeedCardCnt(profileOwner);
        Long followerCnt = followService.findFollowerCnt(profileOwner);
        Long followingCnt = followService.findFollowingCnt(profileOwner);

        return ProfileDto.ProfileInfoResponse.builder()
                .nickname(profileOwner.getNickname())
                .currentDayVisitors(String.valueOf(currentDateVisitorCnt))
                .totalVisitorCnt(String.valueOf(totalVisitorCnt))
                .profileImg(imgService.findProfileImgUrl(profileOwner.getProfileImgName()))
                .cardCnt(String.valueOf(feedCardCnt))
                .followerCnt(String.valueOf(followerCnt))
                .followingCnt(String.valueOf(followingCnt))
                .build();
    }

    private void saveVisitor(Member profileOwner, Member visitor) {
        boolean isNewVisitor = visitorService.saveVisitor(profileOwner, visitor);
        if (isNewVisitor) {
            memberService.incrementTotalVisitorCnt(profileOwner);
        }
    }

    public ProfileDto.NicknameAvailable verifyNicknameAvailable(String nickname) {
        return ProfileDto.NicknameAvailable.builder()
                .isAvailable(badWordFiltering.checkBadWord(nickname))
                .build();
    }

    @Transactional
    public void updateProfile(ProfileDto.ProfileUpdate profileUpdate, Long memberPk) {
        if (imgService.isModeratingImg(profileUpdate.getProfileImg())) {
            throw new EntityNotFoundException(ExceptionMessage.IMAGE_REJECTED_BY_MODERATION.getMessage());
        }

        if(!imgService.verifyImgSaved(profileUpdate.getProfileImg())) {
            throw new EntityNotFoundException(ExceptionMessage.IMAGE_NOT_FOUND.getMessage());
        }

        Member member = memberService.findByPk(memberPk);
        member.updateProfile(profileUpdate.getNickname(), profileUpdate.getProfileImg());
    }
}
