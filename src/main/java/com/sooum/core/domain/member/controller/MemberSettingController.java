package com.sooum.core.domain.member.controller;

import com.sooum.core.domain.member.dto.AccountTransferDto;
import com.sooum.core.domain.member.dto.MemberDto;
import com.sooum.core.domain.member.dto.ProfileDto;
import com.sooum.core.domain.member.service.AccountTransferService;
import com.sooum.core.domain.member.service.MemberService;
import com.sooum.core.global.auth.annotation.CurrentUser;
import com.sooum.core.global.responseform.ResponseEntityModel;
import com.sooum.core.global.responseform.ResponseStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/settings")
public class MemberSettingController {
    private final AccountTransferService accountTransferService;
    private final MemberService memberService;

    @GetMapping("/transfer")
    public ResponseEntity<ResponseEntityModel<ProfileDto.AccountTransferCodeResponse>> findOrSaveAccountTransferId(@CurrentUser Long memberPk) {
        return ResponseEntity.ok(ResponseEntityModel.<ProfileDto.AccountTransferCodeResponse>builder()
                .status(ResponseStatus.builder()
                        .httpStatus(HttpStatus.OK)
                        .httpCode(HttpStatus.OK.value())
                        .responseMessage("Transfer code retrieve successfully")
                        .build())
                .content(accountTransferService.findOrSaveAccountTransferId(memberPk))
                .build());
    }

    @PostMapping("/transfer")
    public ResponseEntity<Void> transferAccount(@RequestBody AccountTransferDto.TransferAccount transferAccount) {
        accountTransferService.transferAccount(transferAccount);

        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/transfer")
    public ResponseEntity<ResponseEntityModel<ProfileDto.AccountTransferCodeResponse>> updateAccountTransferId(@CurrentUser Long memberPk) {
        return ResponseEntity.ok(ResponseEntityModel.<ProfileDto.AccountTransferCodeResponse>builder()
                .status(ResponseStatus.builder()
                        .httpStatus(HttpStatus.OK)
                        .httpCode(HttpStatus.OK.value())
                        .responseMessage("Transfer code retrieve successfully")
                        .build())
                .content(accountTransferService.updateAccountTransfer(memberPk))
                .build());
    }

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
