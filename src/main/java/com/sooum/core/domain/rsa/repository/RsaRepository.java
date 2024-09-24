package com.sooum.core.domain.rsa.repository;

import com.sooum.core.domain.rsa.entity.Rsa;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RsaRepository extends JpaRepository<Rsa, Long> {
}
