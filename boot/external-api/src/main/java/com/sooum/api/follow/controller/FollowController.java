package com.sooum.api.follow.controller;

import com.sooum.api.follow.dto.FollowDto;
import com.sooum.api.follow.service.FollowManagementService;
import com.sooum.global.auth.annotation.CurrentUser;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/followers")
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
