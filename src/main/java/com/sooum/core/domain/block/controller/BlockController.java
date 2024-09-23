package com.sooum.core.domain.block.controller;

import com.sooum.core.domain.block.service.BlockMemberService;
import com.sooum.core.global.auth.annotation.CurrentUser;
import com.sooum.core.global.responseform.ResponseStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;

@RestController
@RequestMapping("/cards/blocks")
@RequiredArgsConstructor
public class BlockController {
    private final BlockMemberService blockMemberService;

    @PostMapping("/{toMemberPk}")
    ResponseEntity<ResponseStatus> blockMember(@PathVariable Long toMemberPk, @CurrentUser Long fromMemberPk) {
        blockMemberService.saveBlockMember(fromMemberPk, toMemberPk);

        return ResponseEntity.created(URI.create("/blocks/" + toMemberPk))
                .body(ResponseStatus.builder()
                        .httpCode(HttpStatus.CREATED.value())
                        .httpStatus(HttpStatus.CREATED)
                        .responseMessage("Member blocked successfully")
                        .build());
    }
}