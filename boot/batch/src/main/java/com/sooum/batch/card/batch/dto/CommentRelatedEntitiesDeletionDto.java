package com.sooum.batch.card.batch.dto;

import com.sooum.data.card.entity.CommentCard;
import com.sooum.data.card.entity.CommentLike;
import com.sooum.data.notification.entity.NotificationHistory;
import com.sooum.data.report.entity.CommentReport;
import com.sooum.data.tag.entity.CommentTag;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
public class CommentRelatedEntitiesDeletionDto {
    private final List<CommentCard> commentCards;
    private final List<CommentLike> commentLikes;
    private final List<CommentReport> commentReports;
    private final List<NotificationHistory> notifications;
    private final List<CommentTag> commentTags;

    @Builder
    public CommentRelatedEntitiesDeletionDto(List<CommentCard> commentCards, List<CommentLike> commentLikes, List<CommentReport> commentReports, List<NotificationHistory> notifications, List<CommentTag> commentTags) {
        this.commentCards = commentCards;
        this.commentLikes = commentLikes;
        this.commentReports = commentReports;
        this.notifications = notifications;
        this.commentTags = commentTags;
    }
}
