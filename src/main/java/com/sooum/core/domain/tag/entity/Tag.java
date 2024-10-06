package com.sooum.core.domain.tag.entity;


import com.sooum.core.domain.common.entity.BaseEntity;
import io.hypersistence.utils.hibernate.id.Tsid;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Version;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Tag extends BaseEntity {
    @Id @Tsid
    private Long pk;

    @NotNull
    @Column(name = "CONTENT")
    private String content;

    @Column(name = "COUNT")
    private int count;

    @Column(name = "IS_ACTIVE")
    private boolean isActive;

    @Version
    private Long version;

    @Builder
    public Tag(String content, boolean isActive) {
        this.content = content;
        this.isActive = isActive;
        this.count = 1;
    }

    public static void minusCount(Tag tag) {
        tag.count--;
    }

    public static void plusCount(Tag tag) {
        tag.count++;
    }
}
