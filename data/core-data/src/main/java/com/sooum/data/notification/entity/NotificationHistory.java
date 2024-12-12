package com.sooum.data.notification.entity;

import com.sooum.data.common.entity.BaseEntity;
import com.sooum.data.member.entity.Member;
import com.sooum.data.notification.entity.notificationtype.NotificationType;
import io.hypersistence.utils.hibernate.id.Tsid;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class NotificationHistory extends BaseEntity {
    @Id @Tsid
    private Long pk;

    @Column(name = "targetCardPk")
    private Long targetCardPk;

    @Column(name = "isRead")
    private boolean isRead;

    @NotNull
    @Enumerated(value = EnumType.STRING)
    private NotificationType notificationType;

    @NotNull
    @JoinColumn(name = "fromMember", foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    @ManyToOne(fetch = FetchType.LAZY)
    private Member fromMember;

    @NotNull
    @JoinColumn(name = "toMember", foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    @ManyToOne(fetch = FetchType.LAZY)
    private Member toMember;

    @Builder
    public NotificationHistory(Member fromMember, NotificationType notificationType, Long targetCardPk, Member toMember) {
        this.isRead = false;
        this.fromMember = fromMember;
        this.notificationType = notificationType;
        this.targetCardPk = targetCardPk;
        this.toMember = toMember;
    }
}
