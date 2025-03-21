package com.sooum.data.report.entity;

import com.sooum.data.card.entity.CommentCard;
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
public class CommentReport extends BaseEntity {
    @Id @Tsid
    private Long pk;

    @NotNull
    @Enumerated(value = EnumType.STRING)
    private ReportType reportType;

    @NotNull
    @JoinColumn(name = "TARGET_CARD", foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    @ManyToOne(fetch = FetchType.LAZY)
    private CommentCard targetCard;

    @NotNull
    @Column(columnDefinition = "TEXT")
    private String targetCardContent;

    @NotNull
    @JoinColumn(name = "REPORTER", foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    @ManyToOne(fetch = FetchType.LAZY)
    private Member reporter;

    @NotNull
    @Column
    private String writerIp;

    @Builder
    public CommentReport(ReportType reportType, CommentCard targetCard, String targetCardContent, Member reporter, String writerIp) {
        this.reportType = reportType;
        this.targetCard = targetCard;
        this.targetCardContent = targetCardContent;
        this.reporter = reporter;
        this.writerIp = writerIp;
    }
}
