package com.sooum.core.domain.report.entity;

import com.sooum.core.domain.common.entity.BaseEntity;
import com.sooum.core.domain.member.entity.Member;
import com.sooum.core.domain.report.reporttype.ReportType;
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
public class CommentReport extends BaseEntity {
    @Id @Tsid
    private Long pk;

    @NotNull
    @Enumerated(value = EnumType.STRING)
    private ReportType reportType;

    @NotNull
    @JoinColumn(name = "TARGET_CARD")
    @ManyToOne(fetch = FetchType.LAZY)
    private CommentReport targetCard;

    @NotNull
    @JoinColumn(name = "REPORTER")
    @ManyToOne(fetch = FetchType.LAZY)
    private Member reporter;

    @Builder
    public CommentReport(ReportType reportType, CommentReport targetCard, Member reporter) {
        this.reportType = reportType;
        this.targetCard = targetCard;
        this.reporter = reporter;
    }
}
