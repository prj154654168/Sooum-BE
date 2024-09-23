package com.sooum.core.domain.rsa.entity;


import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Rsa {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "PUBLIC_KEY", columnDefinition = "TEXT")
    private String publicKey;

    @Column(name = "PRIVATE_KEY", columnDefinition = "TEXT")
    private String privateKey;

    @Column(name = "EXPIRED_AT")
    private Instant expiredAt;

    @Builder
    public Rsa(String publicKey, String privateKey, Instant expiredAt) {
        this.publicKey = publicKey;
        this.privateKey = privateKey;
        this.expiredAt = expiredAt;
    }
}
