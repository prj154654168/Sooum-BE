package com.sooum.core.domain.member.controller;

import com.sooum.core.domain.member.dto.ProfileDto;
import com.sooum.core.domain.member.service.ProfileService;
import com.sooum.core.global.auth.annotation.CurrentUser;
import com.sooum.core.global.responseform.ResponseEntityModel;
import com.sooum.core.global.responseform.ResponseStatus;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/profiles")
@RequiredArgsConstructor
public class ProfileController {

    private final ProfileService profileService;

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

    @GetMapping("/nickname/{nickname}/available")
    public ResponseEntity<ResponseEntityModel<ProfileDto.NicknameAvailable>> verifyNicknameAvailable(@PathVariable("nickname") String nickname) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(
                ResponseEntityModel.<ProfileDto.NicknameAvailable>builder()
                        .status(
                                ResponseStatus.builder()
                                        .httpStatus(HttpStatus.OK)
                                        .httpCode(HttpStatus.OK.value())
                                        .responseMessage("Verify nickname successfully")
                                        .build()
                        )
                        .content(profileService.verifyNicknameAvailable(nickname))
                        .build()
        );
    }

    @PatchMapping("")
    public ResponseEntity<Void> updateProfile(@RequestBody @Valid ProfileDto.ProfileUpdate profileUpdate,
                                              @CurrentUser Long memberPk) {
        profileService.updateProfile(profileUpdate, memberPk);

        return ResponseEntity.noContent().build();
    }
}
