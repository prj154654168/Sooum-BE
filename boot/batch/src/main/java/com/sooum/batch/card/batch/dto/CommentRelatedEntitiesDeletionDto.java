package com.sooum.batch.card.batch.dto;

import com.sooum.data.card.entity.CommentCard;
import com.sooum.data.card.entity.CommentLike;
import com.sooum.data.report.entity.CommentReport;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
public class CommentRelatedEntitiesDeletionDto {
    private final List<CommentCard> commentCards;
    private final List<CommentLike> commentLikes;
    private final List<CommentReport> commentReports;

    @Builder
    public CommentRelatedEntitiesDeletionDto(List<CommentCard> commentCards, List<CommentLike> commentLike, List<CommentReport> commentReports) {
        this.commentCards = commentCards;
        this.commentLikes = commentLike;
        this.commentReports = commentReports;
    }
}
