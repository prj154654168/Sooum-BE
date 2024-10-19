package com.sooum.core.domain.member.repository;

import com.sooum.core.domain.member.entity.AccountTransfer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface AccountTransferRepository extends JpaRepository<AccountTransfer, Long> {
    Optional<AccountTransfer> findByMember_Pk(Long memberPk);
    @Query("select at from AccountTransfer at where at.transferId = :transferId and at.expirationDate > current_timestamp")
    Optional<AccountTransfer> findAvailableAccountTransfer(@Param("transferId") String transferId);
}
