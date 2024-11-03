package com.sooum.data.tag.entity;


import com.sooum.data.card.entity.FeedCard;
import com.sooum.data.common.entity.BaseEntity;
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
public class FeedTag extends BaseEntity {
    @Id @Tsid
    private Long pk;

    @NotNull
    @JoinColumn(name = "FEED_CARD")
    @ManyToOne(fetch = FetchType.LAZY)
    private FeedCard feedCard;

    @NotNull
    @JoinColumn(name = "TAG")
    @ManyToOne(fetch = FetchType.LAZY)
    private Tag tag;

    @Builder
    public FeedTag(FeedCard feedCard, Tag tag) {
        this.feedCard = feedCard;
        this.tag = tag;
    }
}
