package com.sooum.data.suspended.service;

import com.sooum.data.suspended.entity.Suspended;
import com.sooum.data.suspended.repository.SuspendedRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class SuspendedService {
    private final SuspendedRepository suspendedRepository;

    public void save(Suspended suspended) {
        suspendedRepository.save(suspended);
    }

    public Optional<Suspended> findSuspensionUntilBan(String deviceId) {
        return suspendedRepository.findByDeviceIdAndUntilBanAfter(deviceId, LocalDateTime.now());
    }

    public void deleteByDeviceId(String deviceId) {
        suspendedRepository.deleteByDeviceId(deviceId);
    }
}