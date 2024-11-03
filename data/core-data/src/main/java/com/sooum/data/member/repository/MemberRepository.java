package com.sooum.data.member.repository;

import com.sooum.data.member.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long> {

    Optional<Member> findByDeviceId(String deviceId);

    Boolean existsByDeviceId(String deviceId);

    @Modifying
    @Query("update Member m set m.totalVisitorCnt = m.totalVisitorCnt + 1 where m = :profileOwnerMember")
    void incrementTotalVisitorCnt(@Param("profileOwnerMember") Member profileOwnerMember);

    @Query("select m.totalVisitorCnt from Member m where m = :profileOwner")
    Long findTotalVisitorCnt(@Param("profileOwner") Member profileOwner);
}
