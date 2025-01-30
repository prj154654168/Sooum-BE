package com.sooum.api.follow.controller;

import com.sooum.api.follow.dto.FollowDto;
import com.sooum.api.follow.service.FollowManagementUseCase;
import com.sooum.global.auth.annotation.CurrentUser;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/followers")
@RequiredArgsConstructor
public class FollowController {
    private final FollowManagementUseCase followManagementUseCase;

    @PostMapping
    public ResponseEntity<Void> saveFollower(@RequestBody FollowDto.RequestFollowDto toMemberDto, @CurrentUser Long fromMemberId) {
        followManagementUseCase.saveFollower(fromMemberId, toMemberDto.getUserId());
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{toMemberId}")
    public ResponseEntity<Void> deleteFollower(@PathVariable Long toMemberId, @CurrentUser Long fromMemberId) {
        followManagementUseCase.deleteFollower(fromMemberId, toMemberId);
        return ResponseEntity.noContent().build();
    }

}
