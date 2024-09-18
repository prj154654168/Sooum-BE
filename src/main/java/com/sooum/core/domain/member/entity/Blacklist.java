package com.sooum.core.domain.member.entity;

import com.sooum.core.domain.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Blacklist extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long pk;

    @Column(name = "ACCESS_TOKEN")
    private String accessToken;

    @OneToOne(targetEntity = Member.class)
    @JoinColumn(name = "MEMBER")
    private Member member;

    @Builder
    public Blacklist(String accessToken, Member member) {
        this.accessToken = accessToken;
        this.member = member;
    }
}
