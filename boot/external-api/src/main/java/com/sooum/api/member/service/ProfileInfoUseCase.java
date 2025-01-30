package com.sooum.api.member.service;

import com.sooum.api.img.service.ImgService;
import com.sooum.api.member.dto.ProfileDto;
import com.sooum.data.card.service.FeedCardService;
import com.sooum.data.follow.service.FollowService;
import com.sooum.data.member.entity.Member;
import com.sooum.data.member.service.MemberService;
import com.sooum.data.visitor.service.VisitorService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ProfileInfoUseCase {
    private final MemberService memberService;
    private final VisitorService visitorService;
    private final FeedCardService feedCardService;
    private final FollowService followService;
    private final ImgService imgService;

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
                .currentDayVisitors(currentDateVisitorCnt)
                .totalVisitorCnt(totalVisitorCnt)
                .profileImg(imgService.findProfileImgUrl(profileOwner.getProfileImgName()))
                .cardCnt(feedCardCnt)
                .followerCnt(followerCnt)
                .followingCnt(followingCnt)
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
                .currentDayVisitors(currentDateVisitorCnt)
                .totalVisitorCnt(totalVisitorCnt)
                .profileImg(imgService.findProfileImgUrl(profileOwner.getProfileImgName()))
                .cardCnt(feedCardCnt)
                .followerCnt(followerCnt)
                .followingCnt(followingCnt)
                .isFollowing(alreadyFollowing)
                .build();
    }

    private void saveVisitor(Member profileOwner, Member visitor) {
        boolean isNewVisitor = visitorService.saveVisitor(profileOwner, visitor);
        if (isNewVisitor) {
            memberService.incrementTotalVisitorCnt(profileOwner);
        }
    }
}
