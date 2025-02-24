package com.sooum.api.card.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.sooum.data.card.entity.CommentCard;
import com.sooum.data.card.entity.FeedCard;
import com.sooum.data.card.entity.FeedLike;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.hateoas.Link;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Getter
@Setter
public class LatestFeedCardDto extends CardDto {
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSSSS")
    private LocalDateTime storyExpirationTime;
    private final Double distance;
    @Builder
    public LatestFeedCardDto(Long memberPk, FeedCard feedCard, List<CommentCard> commentCards, List<FeedLike> feedLikes,  Link backgroundImgUrl, Optional<Double> latitude, Optional<Double> longitude) {
        super(memberPk, feedCard, commentCards, feedLikes, backgroundImgUrl);
        this.storyExpirationTime = feedCard.isStory() ? feedCard.getCreatedAt().plusDays(1L) : null;
        this.distance = getDistance(feedCard.getLocation(), latitude, longitude);
    }
}
