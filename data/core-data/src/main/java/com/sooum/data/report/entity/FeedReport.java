package com.sooum.data.report.entity;

import com.sooum.data.card.entity.FeedCard;
import com.sooum.data.common.entity.BaseEntity;
import com.sooum.data.member.entity.Member;
import com.sooum.data.report.entity.reporttype.ReportType;
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
public class FeedReport extends BaseEntity {
    @Id @Tsid
    private Long pk;

    @NotNull
    @Enumerated(value = EnumType.STRING)
    private ReportType reportType;

    @NotNull
    @JoinColumn(name = "TARGET_CARD")
    @ManyToOne(fetch = FetchType.LAZY)
    private FeedCard targetCard;

    @NotNull
    @JoinColumn(name = "REPORTER")
    @ManyToOne(fetch = FetchType.LAZY)
    private Member reporter;

    @Builder
    public FeedReport(ReportType reportType, FeedCard targetCard, Member reporter) {
        this.reportType = reportType;
        this.targetCard = targetCard;
        this.reporter = reporter;
    }
}
