package com.sooum.core.domain.card.entity;

import com.sooum.core.domain.common.entity.BaseEntity;
import com.sooum.core.domain.member.entity.Member;
import io.hypersistence.utils.hibernate.id.Tsid;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Objects;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class FeedLike extends BaseEntity {
    @Id @Tsid
    private Long pk;

    @NotNull
    @JoinColumn(name = "TARGET_CARD")
    @ManyToOne(fetch = FetchType.LAZY)
    private FeedCard targetCard;

    @NotNull
    @JoinColumn(name = "LIKED_MEMBER")
    @ManyToOne(fetch = FetchType.LAZY)
    private Member likedMember;

    @Builder
    public FeedLike(FeedCard targetCard, Member likedMember) {
        this.targetCard = targetCard;
        this.likedMember = likedMember;
    }
}
