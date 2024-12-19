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

    @Transactional
    public ProfileDto.MyProfileInfoResponse findMyProfileInfo(Long memberPk) {
        Member profileOwner = memberService.findMember(memberPk);

        Long totalVisitorCnt = memberService.findTotalVisitorCnt(profileOwner);
        Long currentDateVisitorCnt = visitorService.findCurrentDateVisitorCnt(profileOwner);
        Long feedCardCnt = feedCardService.findFeedCardCnt(profileOwner);
        Long followerCnt = followService.findFollowerCnt(profileOwner);
        Long followingCnt = followService.findFollowingCnt(profileOwner);

        return ProfileDto.MyProfileInfoResponse.builder()
                .nickname(profileOwner.getNickname())
                .currentDayVisitors(String.valueOf(currentDateVisitorCnt))
                .totalVisitorCnt(String.valueOf(totalVisitorCnt))
                .profileImg(imgService.findProfileImgUrl(profileOwner.getProfileImgName()))
                .cardCnt(String.valueOf(feedCardCnt))
                .followerCnt(String.valueOf(followerCnt))
                .followingCnt(String.valueOf(followingCnt))
                .build();
    }

    @Transactional
    public ProfileDto.ProfileInfoResponse findProfileInfo(Long profileOwnerPk, Long memberPk) {
        Member profileOwner = memberService.findMember(profileOwnerPk);
        Member visitor = memberService.findMember(memberPk);
        saveVisitor(profileOwner, visitor);

        Long totalVisitorCnt = memberService.findTotalVisitorCnt(profileOwner);
        Long currentDateVisitorCnt = visitorService.findCurrentDateVisitorCnt(profileOwner);
        Long feedCardCnt = feedCardService.findFeedCardCnt(profileOwner);
        Long followerCnt = followService.findFollowerCnt(profileOwner);
        Long followingCnt = followService.findFollowingCnt(profileOwner);
        boolean alreadyFollowing = followService.isAlreadyFollowing(visitor, profileOwner);

        return ProfileDto.ProfileInfoResponse.builder()
                .nickname(profileOwner.getNickname())
                .currentDayVisitors(String.valueOf(currentDateVisitorCnt))
                .totalVisitorCnt(String.valueOf(totalVisitorCnt))
                .profileImg(imgService.findProfileImgUrl(profileOwner.getProfileImgName()))
                .cardCnt(String.valueOf(feedCardCnt))
                .followerCnt(String.valueOf(followerCnt))
                .followingCnt(String.valueOf(followingCnt))
                .isFollowing(alreadyFollowing)
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
