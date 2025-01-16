package com.sooum.api.suspended.controller;

import com.sooum.api.suspended.dto.SuspensionDto;
import com.sooum.api.suspended.service.SuspendedUseCase;
import com.sooum.global.responseform.ResponseEntityModel;
import com.sooum.global.responseform.ResponseStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@RestController
@RequiredArgsConstructor
@RequestMapping("/members/suspension")
public class SuspendedController {
    private final SuspendedUseCase suspendedUseCase;

    @PostMapping
    public ResponseEntity<?> checkSuspendedMember(@RequestBody SuspensionDto.SuspensionRequest request) {
        Optional<SuspensionDto.SuspensionResponse> optSuspensionResponse = suspendedUseCase.checkMemberSuspension(request.getEncryptedDeviceId());

        if (optSuspensionResponse.isEmpty()) {
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.ok(
                ResponseEntityModel.<SuspensionDto.SuspensionResponse>builder()
                        .status(ResponseStatus.builder()
                                .httpCode(HttpStatus.OK.value())
                                .httpStatus(HttpStatus.OK)
                                .responseMessage("User suspension status retrieved successfully.")
                                .build())
                        .content(optSuspensionResponse.get())
                        .build()
        );
    }
}
