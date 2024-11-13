package com.sooum.data.suspended.repository;

import com.sooum.data.suspended.entity.Suspended;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SuspendedRepository extends JpaRepository<Suspended, Long> {
}
