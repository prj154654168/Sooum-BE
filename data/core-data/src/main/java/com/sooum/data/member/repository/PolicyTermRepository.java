package com.sooum.data.member.repository;

import com.sooum.data.member.entity.PolicyTerm;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface PolicyTermRepository extends JpaRepository<PolicyTerm, Long> {
    @Modifying
    @Query("delete from PolicyTerm p where p.member.pk = :memberPk")
    void deletePolicyTerm(@Param("memberPk") Long memberPk);
}
