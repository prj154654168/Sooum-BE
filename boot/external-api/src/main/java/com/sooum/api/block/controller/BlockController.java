package com.sooum.api.block.controller;

import com.sooum.api.block.dto.BlockDto;
import com.sooum.data.block.service.BlockMemberService;
import com.sooum.global.auth.annotation.CurrentUser;
import com.sooum.global.responseform.ResponseStatus;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@RestController
@RequestMapping("/blocks")
@RequiredArgsConstructor
public class
BlockController {
    private final BlockMemberService blockMemberService;

    @PostMapping
    ResponseEntity<ResponseStatus> blockMember(@RequestBody @Valid BlockDto blockDto, @CurrentUser Long fromMemberPk) {
        blockMemberService.saveBlockMember(fromMemberPk, blockDto.toMemberId());

        return ResponseEntity.created(URI.create("/block/" + blockDto.toMemberId()))
                .body(ResponseStatus.builder()
                        .httpCode(HttpStatus.CREATED.value())
                        .httpStatus(HttpStatus.CREATED)
                        .responseMessage("Member blocked successfully")
                        .build());
    }

    @DeleteMapping("/{toMemberId}")
    ResponseEntity<Void> removeBlockMember(@PathVariable Long toMemberId, @CurrentUser Long fromMemberPk) {
        blockMemberService.deleteBlockMember(fromMemberPk, toMemberId);
        return ResponseEntity.noContent().build();
    }




}