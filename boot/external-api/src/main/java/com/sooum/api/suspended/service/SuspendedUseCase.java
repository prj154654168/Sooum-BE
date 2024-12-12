package com.sooum.api.suspended.service;

import com.sooum.api.rsa.service.RsaUseCase;
import com.sooum.data.suspended.service.SuspendedService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class SuspendedUseCase {
    private final SuspendedService suspendedService;
    private final RsaUseCase rsaUseCase;

    public Optional<LocalDateTime> checkMemberSuspension(String encryptedDeviceId) {
        String deviceId = rsaUseCase.decodeDeviceId(encryptedDeviceId);
        return suspendedService.findSuspensionUntilBan(deviceId);
    }
}