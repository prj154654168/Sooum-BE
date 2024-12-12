package com.sooum.data.report.repository;

import com.sooum.data.report.entity.FeedReport;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface FeedReportRepository extends JpaRepository<FeedReport, Long> {

    @Query("select fr from FeedReport fr where fr.targetCard.pk = :cardPk")
    List<FeedReport> findByTargetCardPk(@Param("cardPk") Long cardPk);

    boolean existsByReporter_PkAndTargetCard_Pk(Long reporterPk, Long cardPk);

    @Modifying
    @Query("delete from FeedReport f where f.targetCard.pk = :feedCardPk")
    void deleteAllByFeedCardPk(@Param("feedCardPk") Long feedCardPk);

    @Modifying
    @Query("delete from FeedReport f where f.reporter.pk = :memberPk or f.targetCard.writer.pk = :memberPk")
    void deleteAllFeedReports(@Param("memberPk") Long memberPk);
}
