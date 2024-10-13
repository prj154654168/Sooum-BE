package com.sooum.core.domain.follow.repository;

import com.sooum.core.domain.follow.entity.Follow;
import com.sooum.core.domain.member.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface FollowRepository extends JpaRepository<Follow, Long> {

    @Query("select f from Follow f where f.fromMember = :fromMember and f.toMember = :toMember")
    Optional<Follow> findFollow(@Param("fromMember") Member fromMember, @Param("toMember") Member toMember);

    @Modifying
    @Query("delete from Follow f where f.fromMember = :fromMember and f.toMember = :toMember")
    void deleteFollower(@Param("fromMember") Member fromMember, @Param("toMember") Member toMember);

    @Query("select count(f) from Follow f where f.toMember = :profileOwner")
    Long findFollowerCnt(@Param("profileOwner") Member profileOwner);

    @Query("select count(f) from Follow f where f.fromMember = :profileOwner")
    Long findFollowingCnt(@Param("profileOwner") Member profileOwner);
}
