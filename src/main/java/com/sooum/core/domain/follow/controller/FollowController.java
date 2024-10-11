package com.sooum.core.domain.follow.controller;

import com.sooum.core.domain.follow.service.FollowManagementService;
import com.sooum.core.global.auth.annotation.CurrentUser;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class FollowController {
    private final FollowManagementService followManagementService;

    @PostMapping("/{toMemberId}/follow")
    public ResponseEntity<Void> saveFollower(@PathVariable Long toMemberId, @CurrentUser Long fromMemberId) {
        followManagementService.saveFollower(fromMemberId, toMemberId);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{toMemberId}/follow")
    public ResponseEntity<Void> deleteFollower(@PathVariable Long toMemberId, @CurrentUser Long fromMemberId) {
        followManagementService.deleteFollower(fromMemberId, toMemberId);
        return ResponseEntity.noContent().build();
    }

}
