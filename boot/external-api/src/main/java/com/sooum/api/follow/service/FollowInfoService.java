package com.sooum.api.follow.service;

import com.sooum.api.follow.dto.FollowDto;
import com.sooum.api.img.service.ImgService;
import com.sooum.data.block.service.BlockMemberService;
import com.sooum.data.follow.service.FollowService;
import com.sooum.data.member.entity.Member;
import com.sooum.global.util.NextPageLinkGenerator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class FollowInfoService {
    private final FollowService followService;
    private final BlockMemberService blockMemberService;
    private final ImgService imgService;

    public List<FollowDto.FollowerInfo> findFollowersInfo(Long profileOwnerPk, Optional<Long> followerLastId, Long requesterPk) {
        List<Long> blockedMembers = blockMemberService.findAllBlockToPk(requesterPk);
        List<Member> followers = followService.findFollowerWithoutBlockedMembers(followerLastId, profileOwnerPk, blockedMembers);
        List<Long> followedFollowersPk = followService.findFollowedFollowersPk(requesterPk, followers);

        return NextPageLinkGenerator.appendEachProfileLink(followers.stream()
                .map(follower -> FollowDto.FollowerInfo.builder()
                        .id(String.valueOf(follower.getPk()))
                        .nickname(follower.getNickname())
                        .backgroundImgUrl(imgService.findProfileImgUrl(follower.getProfileImgName()))
                        .isFollowing(followedFollowersPk.contains(follower.getPk()))
                        .build()
                ).toList()
        );
    }

    public List<FollowDto.FollowingInfo> findFollowingsInfo(Long profileOwnerPk, Optional<Long> followingLastId, Long requesterPk) {
        List<Long> blockedMembers = blockMemberService.findAllBlockToPk(requesterPk);
        List<Member> followings = followService.findFollowingWithoutBlockedMembers(followingLastId, profileOwnerPk, blockedMembers);
        List<Long> followedFollowingsPk = followService.findFollowedFollowingsPk(requesterPk, followings);

        return NextPageLinkGenerator.appendEachProfileLink(followings.stream()
                .map(follower -> FollowDto.FollowingInfo.builder()
                        .id(String.valueOf(follower.getPk()))
                        .nickname(follower.getNickname())
                        .backgroundImgUrl(imgService.findProfileImgUrl(follower.getProfileImgName()))
                        .isFollowing(followedFollowingsPk.contains(follower.getPk()))
                        .build()
                ).toList()
        );
    }
}
