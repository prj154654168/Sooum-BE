package com.sooum.core.domain.member.controller;

import com.sooum.core.domain.member.dto.MemberDto;
import com.sooum.core.domain.member.service.MemberService;
import com.sooum.core.global.auth.annotation.CurrentUser;
import com.sooum.core.global.responseform.ResponseEntityModel;
import com.sooum.core.global.responseform.ResponseStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/settings")
public class MemberSettingController {
    private final MemberService memberService;

    @GetMapping("/status")
    public ResponseEntity<ResponseEntityModel<MemberDto.MemberStatus>> findMemberStatus(@CurrentUser Long memberPk) {
        return ResponseEntity.ok(
                ResponseEntityModel.<MemberDto.MemberStatus>builder()
                        .status(ResponseStatus.builder()
                                .httpCode(HttpStatus.OK.value())
                                .httpStatus(HttpStatus.OK)
                                .responseMessage("Member status successfully")
                                .build())
                        .content(memberService.findMemberStatus(memberPk))
                        .build()
        );
    }
}
