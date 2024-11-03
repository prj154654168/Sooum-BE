package com.sooum.data.visitor.repository;

import com.sooum.data.member.entity.Member;
import com.sooum.data.visitor.entity.Visitor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface VisitorRepository extends JpaRepository<Visitor, Long> {

    @Query("select v " +
            "from Visitor v " +
            "where v.visitDate = current_date and v.profileOwner = :profileOwner and v.visitor = :visitor")
    Optional<Visitor> findCurrentDateVisitor(@Param("profileOwner") Member profileOwner, @Param("visitor") Member visitor);

    @Query("select count(v) " +
            "from Visitor v " +
            "where v.profileOwner = :profileOwnerMember and v.visitDate = current_date ")
    Long findCurrentDateVisitorCnt(@Param("profileOwnerMember") Member profileOwnerMember);
}
