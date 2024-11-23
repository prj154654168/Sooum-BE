package com.sooum.data.suspended.repository;

import com.sooum.data.suspended.entity.Suspended;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SuspendedRepository extends JpaRepository<Suspended, Long> {
    Optional<Suspended> findByDeviceId(String deviceId);
}
