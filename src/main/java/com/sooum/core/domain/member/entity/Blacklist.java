package com.sooum.core.domain.member.entity;

import com.sooum.core.domain.common.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Blacklist extends BaseEntity {

    @Id
    @Column(name = "TOKEN")
    private String token;

    private Instant expiredAt;

    @Builder
    public Blacklist(String token, Instant expiredAt) {
        this.token = token;
        this.expiredAt = expiredAt;
    }
}
