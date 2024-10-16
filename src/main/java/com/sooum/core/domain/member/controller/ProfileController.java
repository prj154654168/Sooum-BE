package com.sooum.core.domain.member.controller;

import com.sooum.core.domain.follow.dto.FollowDto;
import com.sooum.core.domain.follow.service.FollowInfoService;
import com.sooum.core.domain.member.dto.ProfileDto;
import com.sooum.core.domain.member.service.ProfileService;
import com.sooum.core.global.auth.annotation.CurrentUser;
import com.sooum.core.global.responseform.ResponseCollectionModel;
import com.sooum.core.global.responseform.ResponseEntityModel;
import com.sooum.core.global.responseform.ResponseStatus;
import com.sooum.core.global.util.NextPageLinkGenerator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@Slf4j
@RestController
@RequestMapping("/profiles")
@RequiredArgsConstructor
public class ProfileController {
    private final ProfileService profileService;
    private final FollowInfoService followInfoService;

    @GetMapping("/{memberPk}")
    ResponseEntity<ResponseEntityModel<ProfileDto.ProfileInfoResponse>>
    findProfileInfo(@PathVariable("memberPk") Long profileOwnerPk,
                    @CurrentUser Long memberPk) {

        return ResponseEntity.ok(ResponseEntityModel.<ProfileDto.ProfileInfoResponse>builder()
                .status(ResponseStatus.builder()
                        .httpStatus(HttpStatus.OK)
                        .httpCode(HttpStatus.OK.value())
                        .responseMessage("retrieve success")
                        .build())
                .content(profileService.findProfileInfo(profileOwnerPk, memberPk))
                .build());
    }

    @GetMapping( "/{profileOwnerPk}/follower")
    public ResponseEntity<?> findFollowersInfo(@RequestParam(required = false) Optional<Long> followerLastId,
                                               @PathVariable Long profileOwnerPk,
                                               @CurrentUser Long requesterPk) {
        List<FollowDto.FollowerInfo> followersInfo = followInfoService.findFollowersInfo(profileOwnerPk, followerLastId, requesterPk);

        if (followersInfo.isEmpty()) {
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.ok(
                ResponseCollectionModel.<FollowDto.FollowerInfo>builder()
                        .status(
                                ResponseStatus.builder()
                                        .httpCode(HttpStatus.OK.value())
                                        .httpStatus(HttpStatus.OK)
                                        .responseMessage("Followers retrieve successfully")
                                        .build()
                        )
                        .content(followersInfo)
                        .build()
                        .add(NextPageLinkGenerator.generateFollowInfoNextPageLink(followersInfo, profileOwnerPk))
        );
    }

    @GetMapping("/{profileOwnerPk}/following")
    public ResponseEntity<?> findFollowingsInfo(@RequestParam(required = false) Optional<Long> followingLastId,
                                                @PathVariable Long profileOwnerPk,
                                                @CurrentUser Long memberPk) {
        List<FollowDto.FollowingInfo> followingsInfo = followInfoService.findFollowingsInfo(profileOwnerPk, followingLastId, memberPk);

        if (followingsInfo.isEmpty()) {
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.ok(
                ResponseCollectionModel.<FollowDto.FollowingInfo>builder()
                        .status(
                                ResponseStatus.builder()
                                        .httpCode(HttpStatus.OK.value())
                                        .httpStatus(HttpStatus.OK)
                                        .responseMessage("Followings retrieve successfully")
                                        .build()
                        )
                        .content(followingsInfo)
                        .build()
                        .add(NextPageLinkGenerator.generateFollowInfoNextPageLink(followingsInfo, profileOwnerPk))
        );
    }
}
