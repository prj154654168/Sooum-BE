package com.sooum.api.member.controller;

import com.sooum.api.member.dto.MemberDto;
import com.sooum.api.suspended.service.SuspendedUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Profile("test")
@RequestMapping("/members")
@RestController
@RequiredArgsConstructor
public class MemberTestController {
    private final SuspendedUseCase suspendedUseCase;

    @PostMapping("/rejoin")
    public ResponseEntity<Void> rejoinMember(@RequestBody MemberDto.ReJoinRequest reJoinRequest) {
        suspendedUseCase.deleteMemberSuspensionForRejoin(reJoinRequest);
        return ResponseEntity.noContent().build();
    }
}
