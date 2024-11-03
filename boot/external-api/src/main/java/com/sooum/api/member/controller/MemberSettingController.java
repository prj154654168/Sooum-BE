package com.sooum.api.member.controller;

import com.sooum.api.member.dto.AccountTransferDto;
import com.sooum.api.member.dto.MemberDto;
import com.sooum.api.member.dto.ProfileDto;
import com.sooum.api.member.service.AccountTransferUseCase;
import com.sooum.api.member.service.MemberUseCase;
import com.sooum.global.auth.annotation.CurrentUser;
import com.sooum.global.responseform.ResponseEntityModel;
import com.sooum.global.responseform.ResponseStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/settings")
public class MemberSettingController {
    private final AccountTransferUseCase accountTransferUseCase;
    private final MemberUseCase memberUseCase;

    @GetMapping("/transfer")
    public ResponseEntity<ResponseEntityModel<ProfileDto.AccountTransferCodeResponse>> findOrSaveAccountTransferId(@CurrentUser Long memberPk) {
        return ResponseEntity.ok(ResponseEntityModel.<ProfileDto.AccountTransferCodeResponse>builder()
                .status(ResponseStatus.builder()
                        .httpStatus(HttpStatus.OK)
                        .httpCode(HttpStatus.OK.value())
                        .responseMessage("Transfer code retrieve successfully")
                        .build())
                .content(accountTransferUseCase.findOrSaveAccountTransferId(memberPk))
                .build());
    }

    @PostMapping("/transfer")
    public ResponseEntity<Void> transferAccount(@RequestBody AccountTransferDto.TransferAccount transferAccount) {
        accountTransferUseCase.transferAccount(transferAccount);

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
                .content(accountTransferUseCase.updateAccountTransfer(memberPk))
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
                        .content(memberUseCase.findMemberStatus(memberPk))
                        .build()
        );
    }
}
