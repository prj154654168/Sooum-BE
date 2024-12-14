package com.sooum.data.app.entity;

import com.sooum.data.common.entity.BaseEntity;
import com.sooum.data.member.entity.devicetype.DeviceType;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class AppVersion extends BaseEntity {
    @Id
    private Long pk;

    @Enumerated(EnumType.STRING)
    @NotNull
    @Column
    DeviceType deviceType;

    @Column
    String latestVersion;
}
