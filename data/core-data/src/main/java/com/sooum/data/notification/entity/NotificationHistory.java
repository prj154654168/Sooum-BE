package com.sooum.data.notification.entity;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import com.sooum.data.card.entity.Card;
import com.sooum.data.card.entity.CommentCard;
import com.sooum.data.card.entity.FeedCard;
import com.sooum.data.card.entity.font.Font;
import com.sooum.data.card.entity.fontsize.FontSize;
import com.sooum.data.card.entity.imgtype.CardImgType;
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

import java.time.LocalDateTime;

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

    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    private LocalDateTime readAt;

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

    @Builder(access = AccessLevel.PRIVATE)
    private NotificationHistory(Member fromMember, Member toMember, NotificationType notificationType, Card card, Long targetCardPk, LocalDateTime readAt) {
        this.isRead = false;
        this.content = card == null ? null : card.getContent();
        this.font = card == null ? null : card.getFont();
        this.fontSize = card == null ? null : card.getFontSize();
        this.imgName = card == null ? null : card.getImgName();
        this.imgType = card == null ? null : card.getImgType();
        this.targetCardPk = targetCardPk;
        this.notificationType = notificationType;
        this.fromMember = fromMember;
        this.toMember = toMember;
        this.readAt = readAt;
    }

    public static NotificationHistory ofCommentWrite(Member fromMember, Long targetCardPk, Card parentCard) {
        return ofGeneral(fromMember, targetCardPk, parentCard, NotificationType.COMMENT_WRITE);
    }

    public static NotificationHistory ofFeedLike(Member fromMember, FeedCard targetCard) {
        return ofGeneral(fromMember, targetCard.getPk(), targetCard, NotificationType.FEED_LIKE);
    }

    public static NotificationHistory ofCommentLike(Member fromMember, CommentCard targetCard) {
        return ofGeneral(fromMember, targetCard.getPk(), targetCard, NotificationType.COMMENT_LIKE);
    }

    private static NotificationHistory ofGeneral(Member fromMember, Long targetCardPk, Card targetCard, NotificationType notificationType) {
        return NotificationHistory.builder()
                .notificationType(notificationType)
                .fromMember(fromMember)
                .toMember(targetCard.getWriter())
                .targetCardPk(targetCardPk)
                .card(targetCard)
                .build();
    }

    public static NotificationHistory ofBlocked(Member toMember) {
        return ofSystem(toMember, NotificationType.BLOCKED);
    }

    public static NotificationHistory ofDeleted(Member toMember) {
        return ofSystem(toMember, NotificationType.DELETED);
    }

    private static NotificationHistory ofSystem(Member toMember, NotificationType notificationType) {
        return NotificationHistory.builder()
                .toMember(toMember)
                .notificationType(notificationType)
                .build();
    }
}
