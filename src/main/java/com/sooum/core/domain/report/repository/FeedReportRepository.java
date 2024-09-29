package com.sooum.core.domain.report.repository;

import com.sooum.core.domain.card.entity.FeedCard;
import com.sooum.core.domain.member.entity.Member;
import com.sooum.core.domain.report.entity.FeedReport;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FeedReportRepository extends JpaRepository<FeedReport, Long> {

    List<FeedReport> findByTargetCard(FeedCard card);

    boolean existsByReporterAndTargetCard(Member reporter, FeedCard card);
}
