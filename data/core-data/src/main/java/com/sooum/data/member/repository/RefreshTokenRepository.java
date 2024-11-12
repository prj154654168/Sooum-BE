package com.sooum.data.member.repository;

import com.sooum.data.member.entity.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {
    @Modifying
    @Query("delete from RefreshToken r where r.member.pk = :memberPk")
    void deleteRefreshToken(@Param("memberPk") Long memberPk);
}
