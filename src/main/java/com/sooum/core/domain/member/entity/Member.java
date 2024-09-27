package com.sooum.core.domain.member.entity;

import com.sooum.core.domain.card.entity.FeedCard;
import com.sooum.core.domain.common.entity.BaseEntity;
import com.sooum.core.domain.member.entity.devicetype.DeviceType;
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
public class Member extends BaseEntity {
    @Id @Tsid
    private Long pk;

    @NotNull
    @Column(name = "DEVICE_ID", unique = true)
    private String deviceId;

    @NotNull
    @Enumerated(value = EnumType.STRING)
    private DeviceType deviceType;

    @NotNull
    @Column(name = "FIREBASE_TOKEN", columnDefinition = "VARBINARY(400) NOT NULL")
    private String firebaseToken;

    @NotNull
    @Column(name = "NICKNAME", columnDefinition = "VARBINARY(255) NOT NULL")
    private String nickname;

    @Column(name = "IS_ALLOW_NOTIFY")
    private boolean isAllowNotify;

    @Column(name = "BAN_COUNT")
    private int banCount;

    @Column(name = "DELETED_AT")
    private LocalDateTime deletedAt;

    @Column(name = "UNTIL_BAN")
    private LocalDateTime untilBan;

    @Enumerated(value = EnumType.STRING)
    @Column(name = "ROLE")
    private Role role;

    @JoinColumn(name = "PROFILE_CARD")
    @OneToOne(fetch = FetchType.LAZY)
    private FeedCard profileCard;

    @Builder
    public Member(String deviceId, DeviceType deviceType, String firebaseToken, String nickname, boolean isAllowNotify) {
        this.deviceId = deviceId;
        this.deviceType = deviceType;
        this.firebaseToken = firebaseToken;
        this.nickname = nickname;
        this.isAllowNotify = isAllowNotify;
        this.banCount = 0;
        this.deletedAt = null;
        this.untilBan = null;
        this.profileCard = null;
        this.role = Role.USER;
    }

    public int ban() {
        banCount++;
        role = Role.BANNED;

        return switch (banCount) {
            case 1 -> {
                untilBan = LocalDateTime.now().plusDays(1);
                yield 1;
            }
            case 2 -> {
                untilBan = LocalDateTime.now().plusDays(7);
                yield 7;
            }
            case 3 -> {
                untilBan = LocalDateTime.now().plusDays(14);
                yield 14;
            }
            default -> {
                untilBan = LocalDateTime.now().plusDays(30);
                yield 30;
            }
        };
    }
}
