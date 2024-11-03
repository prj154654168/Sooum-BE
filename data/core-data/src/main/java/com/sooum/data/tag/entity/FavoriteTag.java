package com.sooum.data.tag.entity;


import com.sooum.data.common.entity.BaseEntity;
import com.sooum.data.member.entity.Member;
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
public class FavoriteTag extends BaseEntity {
    @Id @Tsid
    private Long pk;

    @NotNull
    @JoinColumn(name = "MEMBER")
    @ManyToOne(fetch = FetchType.LAZY)
    private Member member;

    @NotNull
    @JoinColumn(name = "TAG")
    @ManyToOne(fetch = FetchType.LAZY)
    private Tag tag;

    @Builder
    public FavoriteTag(Member member, Tag tag) {
        this.member = member;
        this.tag = tag;
    }
}
