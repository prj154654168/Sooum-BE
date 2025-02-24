package com.sooum.batch.suspended.service;

import com.sooum.batch.suspended.repository.SuspendedBatchRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class DeleteSuspensionService {
    private final SuspendedBatchRepository suspendedBatchRepository;

    public void deleteExpiredSuspended() {
        suspendedBatchRepository.deleteExpiredSuspensions(LocalDateTime.now());
    }
}
