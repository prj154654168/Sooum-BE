package com.sooum.api.member.controller;

import com.sooum.api.follow.dto.FollowDto;
import com.sooum.api.follow.service.FollowInfoService;
import com.sooum.api.member.dto.ProfileDto;
import com.sooum.api.member.service.ProfileService;
import com.sooum.global.auth.annotation.CurrentUser;
import com.sooum.global.responseform.ResponseCollectionModel;
import com.sooum.global.responseform.ResponseEntityModel;
import com.sooum.global.responseform.ResponseStatus;
import com.sooum.global.util.NextPageLinkGenerator;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/profiles")
@RequiredArgsConstructor
public class ProfileController {
    private final ProfileService profileService;
    private final FollowInfoService followInfoService;

    @GetMapping("/my")
    ResponseEntity<ResponseEntityModel<ProfileDto.MyProfileInfoResponse>>
    findMyProfileInfo(@CurrentUser Long memberPk) {

        return ResponseEntity.ok(ResponseEntityModel.<ProfileDto.MyProfileInfoResponse>builder()
                .status(ResponseStatus.builder()
                        .httpStatus(HttpStatus.OK)
                        .httpCode(HttpStatus.OK.value())
                        .responseMessage("retrieve success")
                        .build())
                .content(profileService.findMyProfileInfo(memberPk))
                .build());
    }

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

    @PostMapping("/nickname/available")
    public ResponseEntity<ResponseEntityModel<ProfileDto.NicknameAvailableResponse>> verifyNicknameAvailable(@RequestBody ProfileDto.NicknameAvailableRequest requestDto) {
        return ResponseEntity.ok(
                ResponseEntityModel.<ProfileDto.NicknameAvailableResponse>builder()
                        .status(
                                ResponseStatus.builder()
                                        .httpStatus(HttpStatus.OK)
                                        .httpCode(HttpStatus.OK.value())
                                        .responseMessage("Verify nickname successfully")
                                        .build()
                        )
                        .content(profileService.verifyNicknameAvailable(requestDto.getNickname()))
                        .build()
        );
    }

    @PatchMapping("")
    public ResponseEntity<Void> updateProfile(@RequestBody @Valid ProfileDto.ProfileUpdate profileUpdate,
                                              @CurrentUser Long memberPk) {
        profileService.updateProfile(profileUpdate, memberPk);

        return ResponseEntity.noContent().build();
    }

    @GetMapping( value = {"/follower", "/{profileOwnerPk}/follower"})
    public ResponseEntity<?> findFollowersInfo(@RequestParam(required = false) Optional<Long> followerLastId,
                                               @PathVariable Optional<Long> profileOwnerPk,
                                               @CurrentUser Long requesterPk) {
        List<FollowDto.FollowerInfo> followersInfo = profileOwnerPk.isEmpty()
                ? followInfoService.findFollowersInfo(requesterPk, followerLastId, requesterPk)
                : followInfoService.findFollowersInfo(profileOwnerPk.get(), followerLastId, requesterPk);

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
                        .add(profileOwnerPk.isEmpty()
                                ? NextPageLinkGenerator.generateFollowInfoNextPageLink(followersInfo, requesterPk)
                                : NextPageLinkGenerator.generateFollowInfoNextPageLink(followersInfo, profileOwnerPk.get()))
        );
    }

    @GetMapping(value = {"/following", "/{profileOwnerPk}/following"})
    public ResponseEntity<?> findFollowingsInfo(@RequestParam(required = false) Optional<Long> followingLastId,
                                                @PathVariable Optional<Long> profileOwnerPk,
                                                @CurrentUser Long requesterPk) {
        List<FollowDto.FollowingInfo> followingsInfo = profileOwnerPk.isEmpty()
                ? followInfoService.findFollowingsInfo(requesterPk, followingLastId, requesterPk)
                : followInfoService.findFollowingsInfo(profileOwnerPk.get(), followingLastId, requesterPk);

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
                        .add(profileOwnerPk.isEmpty()
                                ? NextPageLinkGenerator.generateFollowInfoNextPageLink(followingsInfo, requesterPk)
                                : NextPageLinkGenerator.generateFollowInfoNextPageLink(followingsInfo, profileOwnerPk.get()))
        );
    }
}
