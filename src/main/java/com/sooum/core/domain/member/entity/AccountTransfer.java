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
import java.util.UUID;

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
    public AccountTransfer(Member member) {
        this.transferId = createTransferId(member.getNickname());
        this.expirationDate = LocalDateTime.now().plusDays(1L);
        this.member = member;
    }

    private String createTransferId(String nickname) {
        String[] uuidSplit = UUID.randomUUID().toString().split("-");
        StringBuilder uuidResult = new StringBuilder();

        for (String uuid : uuidSplit) {
            uuidResult.append(uuid.charAt(0));
        }

        return nickname + uuidResult;
    }

    public void updateTransferId (String nickname) {
        this.transferId = createTransferId(nickname);
        this.expirationDate = LocalDateTime.now().plusDays(1L);
    }

    public boolean isExpired() {
        return LocalDateTime.now().isAfter(expirationDate);
    }
}
