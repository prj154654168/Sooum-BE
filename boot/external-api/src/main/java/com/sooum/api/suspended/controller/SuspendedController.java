package com.sooum.api.suspended.controller;

import com.sooum.api.suspended.dto.SuspensionDto;
import com.sooum.api.suspended.service.SuspendedUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;

@RestController
@RequiredArgsConstructor
@RequestMapping("/members/suspension")
public class SuspendedController {
    private final SuspendedUseCase suspendedUseCase;

    @PostMapping
    public ResponseEntity<?> checkSuspendedMember(@RequestBody SuspensionDto suspensionDto) {
        Optional<LocalDateTime> suspensionUntil = suspendedUseCase.checkMemberSuspension(suspensionDto.getEncryptedDeviceId());

        if (suspensionUntil.isEmpty()) {
            return ResponseEntity.ok("No suspension found.");
        }

        return ResponseEntity.ok(Map.of("untilBan", suspensionUntil.get()));
    }
}
