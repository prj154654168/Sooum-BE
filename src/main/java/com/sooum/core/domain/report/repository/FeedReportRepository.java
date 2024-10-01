package com.sooum.core.domain.report.repository;

import com.sooum.core.domain.report.entity.FeedReport;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FeedReportRepository extends JpaRepository<FeedReport, Long> {

    List<FeedReport> findByTargetCard_Pk(Long cardPk);

    boolean existsByReporter_PkAndTargetCard_Pk(Long reporterPk, Long cardPk);
}
