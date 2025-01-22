package com.sooum.data.member.entity;

import com.sooum.data.common.entity.BaseEntity;
import com.sooum.data.member.entity.devicetype.DeviceType;
import io.hypersistence.utils.hibernate.id.Tsid;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

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

    @Column(name = "FIREBASE_TOKEN", columnDefinition = "VARBINARY(400)")
    private String firebaseToken;

    @NotNull
    @Column(name = "NICKNAME", columnDefinition = "VARBINARY(255) NOT NULL")
    private String nickname;

    @Column(name = "IS_ALLOW_NOTIFY")
    private boolean isAllowNotify;

    @Column(name = "BAN_COUNT")
    private int banCount;

    @NotNull
    @Column(name = "TOTAL_VISITOR_CNT")
    private long totalVisitorCnt;

    @Column(name = "DELETED_AT")
    private LocalDateTime deletedAt;

    @Column(name = "UNTIL_BAN")
    private LocalDateTime untilBan;

    @Enumerated(value = EnumType.STRING)
    @Column(name = "ROLE")
    private Role role;

    @Setter
    @Column(name = "PROFILE_IMG_NAME")
    private String profileImgName;

    @Builder
    public Member(String deviceId, DeviceType deviceType, String firebaseToken, String nickname, boolean isAllowNotify) {
        this.deviceId = deviceId;
        this.deviceType = deviceType;
        this.firebaseToken = firebaseToken;
        this.nickname = nickname;
        this.isAllowNotify = isAllowNotify;
        this.banCount = 0;
        this.totalVisitorCnt = 0;
        this.deletedAt = null;
        this.untilBan = null;
        this.profileImgName = null;
        this.role = Role.USER;
    }

    public LocalDateTime ban() {
        banCount++;
        role = Role.BANNED;

        if(banCount == 1)
            untilBan = LocalDateTime.now().plusDays(1);
        else if(banCount == 2)
            untilBan = LocalDateTime.now().plusDays(7);
        else if(banCount == 3)
            untilBan = LocalDateTime.now().plusDays(14);
        else
            untilBan = LocalDateTime.now().plusDays(30);

        return untilBan;
    }

    public void unban() {
        role = Role.USER;
        untilBan = null;
    }

    public void updateNickname(String nickname) {
        this.nickname = nickname;
    }

    public void updateProfileImgName(String profileImgName) {
        this.profileImgName = profileImgName;
    }

    public void updateFCMToken(String fcmToken) {
        this.firebaseToken = fcmToken;
    }

    public void updateNotifyAllow(boolean isAllowNotify) {
        this.isAllowNotify = isAllowNotify;
    }
}
