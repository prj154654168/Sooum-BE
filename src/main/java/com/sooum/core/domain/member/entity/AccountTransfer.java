package com.sooum.core.domain.member.entity;

import com.sooum.core.domain.common.entity.BaseEntity;
import io.hypersistence.utils.hibernate.id.Tsid;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class AccountTransfer extends BaseEntity {
    @Id @Tsid
    private Long pk;

    @NotNull
    @Column(name = "TRANSFER_ID", unique = true)
    private String transferId;

    @NotNull
    @Column(name = "EXPIRATION_DATE")
    private LocalDateTime expirationDate;

    @NotNull
    @JoinColumn(name = "MEMBER")
    @ManyToOne(fetch = FetchType.LAZY)
    private Member member;

    @Builder
    public AccountTransfer(Member member, String transferId) {
        this.transferId = transferId;
        this.expirationDate = LocalDateTime.now().plusDays(1L);
        this.member = member;
    }

    public void updateTransferId (String transferId) {
        this.transferId = transferId;
        this.expirationDate = LocalDateTime.now().plusDays(1L);
    }

    public boolean isExpired() {
        return LocalDateTime.now().isAfter(expirationDate);
    }
}
