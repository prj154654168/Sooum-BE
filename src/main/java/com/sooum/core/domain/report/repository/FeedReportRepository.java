package com.sooum.core.domain.report.repository;

import com.sooum.core.domain.report.entity.FeedReport;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface FeedReportRepository extends JpaRepository<FeedReport, Long> {

    @Query("select fr from FeedReport fr where fr.targetCard.pk = :cardPk")
    List<FeedReport> findByTargetCard_Pk(Long cardPk);

    boolean existsByReporter_PkAndTargetCard_Pk(Long reporterPk, Long cardPk);
}
