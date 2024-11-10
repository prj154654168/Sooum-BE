package com.sooum.batch.card.batch.dto;

import com.sooum.data.card.entity.FeedCard;
import com.sooum.data.card.entity.FeedLike;
import com.sooum.data.report.entity.FeedReport;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
public class FeedRelatedEntitiesDeletionDto {
    private final FeedCard feedCard;
    private final List<FeedLike> feedLikes;
    private final List<FeedReport> feedReports;

    @Builder
    public FeedRelatedEntitiesDeletionDto(FeedCard feedCard, List<FeedLike> feedLikes, List<FeedReport> feedReports) {
        this.feedCard = feedCard;
        this.feedLikes = feedLikes;
        this.feedReports = feedReports;
    }
}
