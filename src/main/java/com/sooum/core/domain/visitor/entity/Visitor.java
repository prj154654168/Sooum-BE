package com.sooum.core.domain.visitor.entity;

import com.sooum.core.domain.common.entity.BaseEntity;
import com.sooum.core.domain.member.entity.Member;
import io.hypersistence.utils.hibernate.id.Tsid;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Getter
@Entity
@Table(indexes = @Index(name = "IDX_VISIT_DATE", columnList = "createdAt"))
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Visitor extends BaseEntity {
    @Id @Tsid
    private Long pk;

    @NotNull
    @Column(name = "VISIT_DATE")
    private LocalDate visitDate;

    @JoinColumn(name = "PROFILE_OWNER", foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    @ManyToOne(fetch = FetchType.LAZY)
    private Member profileOwner;

    @JoinColumn(name = "VISITOR", foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    @ManyToOne(fetch = FetchType.LAZY)
    private Member visitor;

    @Builder
    public Visitor(Member profileOwner, Member visitor) {
        this.profileOwner = profileOwner;
        this.visitor = visitor;
        this.visitDate = LocalDate.now();
    }
}
