package com.sooum.core.domain.card.entity;

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
public class PopularCard extends BaseEntity {
    @Id @Tsid
    private Long pk;

    @NotNull
    @JoinColumn(name = "POPULAR_CARD")
    @OneToOne(fetch = FetchType.LAZY)
    private FeedCard popularCard;

    @Builder
    public PopularCard(FeedCard popularCard) {
        this.popularCard = popularCard;
    }
}
