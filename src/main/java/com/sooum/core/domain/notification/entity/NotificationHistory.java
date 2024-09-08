package com.sooum.core.domain.notification.entity;

import com.sooum.core.domain.card.entity.CommentCard;
import com.sooum.core.domain.card.entity.FeedCard;
import com.sooum.core.domain.common.entity.BaseEntity;
import com.sooum.core.domain.member.entity.Member;
import com.sooum.core.domain.notification.notificationtype.NotificationType;
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

    @NotNull
    @Enumerated(value = EnumType.STRING)
    private NotificationType notificationType;

    @JoinColumn(name = "FEED_CARD")
    @ManyToOne(fetch = FetchType.LAZY)
    private FeedCard feedCard;

    @JoinColumn(name = "COMMENT_CARD")
    @ManyToOne(fetch = FetchType.LAZY)
    private CommentCard commentCard;

    @NotNull
    @JoinColumn(name = "MEMBER")
    @ManyToOne(fetch = FetchType.LAZY)
    private Member member;

    @Builder
    public NotificationHistory(NotificationType notificationType, FeedCard feedCard, Member member) {
        this.notificationType = notificationType;
        this.feedCard = feedCard;
        this.member = member;
    }

    @Builder
    public NotificationHistory(NotificationType notificationType, CommentCard commentCard, Member member) {
        this.notificationType = notificationType;
        this.commentCard = commentCard;
        this.member = member;
    }
}
