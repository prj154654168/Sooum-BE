package com.sooum.core.domain.member.repository;

import com.sooum.core.domain.member.entity.AccountTransfer;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AccountTransferRepository extends JpaRepository<AccountTransfer, Long> {
    Optional<AccountTransfer> findByMember_Pk(Long memberPk);
    Optional<AccountTransfer> findByTransferId(String transferId);
}
