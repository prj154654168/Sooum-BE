package com.sooum.data.rsa.repository;

import com.sooum.data.rsa.entity.Rsa;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.Optional;

public interface RsaRepository extends JpaRepository<Rsa, Long> {

    Optional<Rsa> findByExpiredAtIsAfter(LocalDateTime now);
}
