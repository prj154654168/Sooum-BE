package com.sooum.core.domain.member.entity;

import com.sooum.core.domain.common.entity.BaseEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Blacklist extends BaseEntity {

    @Id
    private Long pk;

    @Column(name = "ACCESS_TOKEN")
    private String accessToken;

    @NotNull
    @OneToOne(targetEntity = Member.class)
    @JoinColumn(name = "MEMBER")
    @MapsId
    private Member member;

    @Builder
    public Blacklist(String accessToken, Member member) {
        this.accessToken = accessToken;
        this.member = member;
    }
}
