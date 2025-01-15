package com.sooum.data.report.service;

import com.sooum.data.card.entity.CommentCard;
import com.sooum.data.member.entity.Member;
import com.sooum.data.report.entity.CommentReport;
import com.sooum.data.report.entity.reporttype.ReportType;
import com.sooum.data.report.repository.CommentReportRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CommentReportService {
    private final CommentReportRepository commentReportRepository;

    public boolean isDuplicateReport(Long cardPk, Long memberPk) {
        return commentReportRepository.existsByReporter_PkAndTargetCard_Pk(cardPk, memberPk);
    }

    public void save(Member member, CommentCard card, ReportType reportType) {
        commentReportRepository.save(CommentReport.builder()
                .reporter(member)
                .targetCard(card)
                .targetCardContent(card.getContent())
                .reportType(reportType)
                .writerIp(card.getWriterIp())
                .build());
    }

    public void deleteReport(CommentCard cardPk) {
        commentReportRepository.deleteAllByCommentCardPk(cardPk);
    }

    public List<CommentReport> findCommentReports(Long cardPk) {
        return commentReportRepository.findByTargetCard_Pk(cardPk);
    }

    public void deleteAllCommentReports(List<CommentReport> reports) {
        commentReportRepository.deleteAllInBatch(reports);
    }

    public void deleteAllBefore6Month() {
        commentReportRepository.deleteAfter6Month(LocalDateTime.now().minusMonths(6));
    }
}
