package com.sooum.core.domain.report.repository;

import com.sooum.core.domain.card.entity.Card;
import com.sooum.core.domain.card.entity.FeedCard;
import com.sooum.core.domain.member.entity.Member;
import com.sooum.core.domain.report.entity.FeedReport;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface FeedReportRepository extends JpaRepository<FeedReport, Long> {

    @Query("select count(fr) from FeedReport fr where fr.targetCard = :card")
    Integer countFeedReportByCard(Card card);

    boolean existsByReporterAndTargetCard(Member reporter, FeedCard card);
}
