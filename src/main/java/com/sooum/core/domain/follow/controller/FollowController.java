package com.sooum.core.domain.follow.controller;

import com.sooum.core.domain.follow.dto.FollowDto;
import com.sooum.core.domain.follow.service.FollowManagementService;
import com.sooum.core.global.auth.annotation.CurrentUser;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/follower")
@RequiredArgsConstructor
public class FollowController {
    private final FollowManagementService followManagementService;

    @PostMapping
    public ResponseEntity<Void> saveFollower(@RequestBody FollowDto.RequestFollowDto toMemberDto, @CurrentUser Long fromMemberId) {
        followManagementService.saveFollower(fromMemberId, toMemberDto.getUserId());
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{toMemberId}")
    public ResponseEntity<Void> deleteFollower(@PathVariable Long toMemberId, @CurrentUser Long fromMemberId) {
        followManagementService.deleteFollower(fromMemberId, toMemberId);
        return ResponseEntity.noContent().build();
    }

}
