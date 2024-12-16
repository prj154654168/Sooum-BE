package com.sooum.data.notification.entity;

import com.sooum.data.card.entity.Card;
import com.sooum.data.card.entity.font.Font;
import com.sooum.data.card.entity.fontsize.FontSize;
import com.sooum.data.card.entity.imgtype.CardImgType;
import com.sooum.data.card.entity.parenttype.CardType;
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
import org.locationtech.jts.geom.Point;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class NotificationHistory extends BaseEntity {
    @Id @Tsid
    private Long pk;

    @Column(name = "targetCardPk")
    private Long targetCardPk;

    @Column(name = "CONTENT", columnDefinition = "TEXT")
    private String content;

    @Enumerated(value = EnumType.STRING)
    private FontSize fontSize;

    @Enumerated(value = EnumType.STRING)
    private Font font;

    @Enumerated(value = EnumType.STRING)
    private CardImgType imgType;

    @Column(name = "IMG_NAME")
    private String imgName;

    @Column(name = "isRead")
    private boolean isRead;

    @NotNull
    @Enumerated(value = EnumType.STRING)
    private NotificationType notificationType;

    @JoinColumn(name = "fromMember", foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    @ManyToOne(fetch = FetchType.LAZY)
    private Member fromMember;

    @NotNull
    @JoinColumn(name = "toMember", foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    @ManyToOne(fetch = FetchType.LAZY)
    private Member toMember;

    @Builder
    public NotificationHistory(Member fromMember, Member toMember, NotificationType notificationType, Card card) {
        this.isRead = false;
        this.content = card == null ? null : card.getContent();
        this.font = card == null ? null : card.getFont();
        this.fontSize = card == null ? null : card.getFontSize();
        this.imgName = card == null ? null : card.getImgName();
        this.imgType = card == null ? null : card.getImgType();
        this.targetCardPk = card == null ? null : card.getPk();
        this.notificationType = notificationType;
        this.fromMember = fromMember;
        this.toMember = toMember;
    }
}
