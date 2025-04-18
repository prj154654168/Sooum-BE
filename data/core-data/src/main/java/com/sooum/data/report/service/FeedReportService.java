package com.sooum.data.report.service;

import com.sooum.data.card.entity.FeedCard;
import com.sooum.data.member.entity.Member;
import com.sooum.data.report.entity.FeedReport;
import com.sooum.data.report.entity.reporttype.ReportType;
import com.sooum.data.report.repository.FeedReportRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class FeedReportService {
    private final FeedReportRepository feedReportRepository;

    public void deleteReport(Long feedCardPk) {
        feedReportRepository.deleteAllByFeedCardPk(feedCardPk);
    }

    public boolean isDuplicateReport(Long cardPk, Long memberPk) {
        return feedReportRepository.existsByReporter_PkAndTargetCard_Pk(memberPk, cardPk);
    }

    public void save(Member member, FeedCard feedCard, ReportType reportType) {
        feedReportRepository.save(FeedReport.builder()
                .reporter(member)
                .targetCard(feedCard)
                .targetCardContent(feedCard.getContent())
                .reportType(reportType)
                .writerIp(feedCard.getWriterIp())
                .build());
    }

    public List<FeedReport> findFeedReport(Long feedCardPk) {
        return feedReportRepository.findByTargetCardPk(feedCardPk);
    }

    public void deleteAllFeedReports(List<FeedReport> reports) {
        feedReportRepository.deleteAllInBatch(reports);
    }

    public void deleteAllBefore6Month() {
        feedReportRepository.deleteAfter6Month(LocalDateTime.now().minusMonths(6));
    }
}
