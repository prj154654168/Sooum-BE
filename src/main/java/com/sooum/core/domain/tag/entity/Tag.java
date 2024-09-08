package com.sooum.core.domain.tag.entity;


import com.sooum.core.domain.common.entity.BaseEntity;
import io.hypersistence.utils.hibernate.id.Tsid;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
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

    @Column(name = "count")
    private int count;

    @Builder
    public Tag(String content) {
        this.content = content;
        this.count = 0;
    }
}
