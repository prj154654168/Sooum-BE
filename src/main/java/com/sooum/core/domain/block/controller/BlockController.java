package com.sooum.core.domain.block.controller;

import com.sooum.core.domain.block.dto.BlockDto;
import com.sooum.core.domain.block.service.BlockMemberService;
import com.sooum.core.global.auth.annotation.CurrentUser;
import com.sooum.core.global.responseform.ResponseStatus;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@RestController
@RequestMapping("/blocks")
@RequiredArgsConstructor
public class BlockController {
    private final BlockMemberService blockMemberService;

    @PostMapping
    ResponseEntity<ResponseStatus> blockMember(@RequestBody @Valid BlockDto blockDto, @CurrentUser Long fromMemberPk) {
        blockMemberService.saveBlockMember(fromMemberPk, blockDto.toMemberPk());

        return ResponseEntity.created(URI.create("/block/" + blockDto.toMemberPk()))
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