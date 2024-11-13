package com.sooum.data.suspended.service;

import com.sooum.data.suspended.entity.Suspended;
import com.sooum.data.suspended.repository.SuspendedRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SuspendedService {
    private final SuspendedRepository suspendedRepository;

    public void save(Suspended suspended) {
        suspendedRepository.save(suspended);
    }
}
