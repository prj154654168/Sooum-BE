package com.sooum.core.domain.member.repository;

import com.sooum.core.domain.member.entity.PolicyTerm;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PolicyTermRepository extends JpaRepository<PolicyTerm, Long> {
}
