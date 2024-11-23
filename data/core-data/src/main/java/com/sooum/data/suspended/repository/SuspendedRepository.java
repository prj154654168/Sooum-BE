package com.sooum.data.suspended.repository;

import com.sooum.data.suspended.entity.Suspended;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.Optional;

public interface SuspendedRepository extends JpaRepository<Suspended, Long> {
    Optional<Suspended> findByDeviceIdAndUntilBanAfter(String deviceId, LocalDateTime now);
}
