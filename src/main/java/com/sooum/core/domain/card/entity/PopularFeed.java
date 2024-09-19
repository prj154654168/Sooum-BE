package com.sooum.core.domain.card.entity;

import com.sooum.core.domain.card.dto.popularitytype.PopularityType;
import com.sooum.core.domain.common.entity.BaseEntity;
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
public class PopularFeed extends BaseEntity {
    @Id @Tsid
    private Long pk;

    @NotNull
    @JoinColumn(name = "POPULAR_CARD")
    @OneToOne(fetch = FetchType.LAZY)
    private FeedCard popularCard;

    @NotNull
    @Enumerated(value = EnumType.STRING)
    private PopularityType popularityType;

    @Builder
    public PopularFeed(FeedCard popularCard, PopularityType popularityType) {
        this.popularCard = popularCard;
        this.popularityType = popularityType;
    }
}
