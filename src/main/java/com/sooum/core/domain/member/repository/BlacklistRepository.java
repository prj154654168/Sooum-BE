package com.sooum.core.domain.member.repository;

import com.sooum.core.domain.member.entity.Blacklist;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BlacklistRepository extends JpaRepository<Blacklist, Long> {
}
