package com.sooum.core.domain.follow.repository;

import com.sooum.core.domain.follow.entity.Follow;
import com.sooum.core.domain.member.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface FollowRepository extends JpaRepository<Follow, Long> {
    @Modifying
    @Query("delete from Follow f where f.fromMember = :fromMember and f.toMember = :toMember")
    void deleteFollower(@Param("fromMember") Member fromMember, @Param("toMember") Member toMember);
}
