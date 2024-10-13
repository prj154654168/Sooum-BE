package com.sooum.core.domain.member.service;

import com.sooum.core.domain.card.service.FeedCardService;
import com.sooum.core.domain.follow.service.FollowService;
import com.sooum.core.domain.img.service.ImgService;
import com.sooum.core.domain.member.dto.ProfileDto;
import com.sooum.core.domain.member.entity.Member;
import com.sooum.core.domain.visitor.service.VisitorService;
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
}
