package com.sooum.batch.card.batch.repository;

import com.sooum.data.card.entity.FeedCard;
import com.sooum.data.report.entity.FeedReport;
import com.sooum.data.report.repository.FeedReportRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface FeedReportBatchRepository extends FeedReportRepository {
    @Query("select fr from FeedReport fr where fr.targetCard = :targetCardList")
    List<FeedReport> findFeedReportsForDeletion(@Param("targetCardList") FeedCard targetCard);
}
