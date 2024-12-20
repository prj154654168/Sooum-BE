package com.sooum.data.report.service;

import com.sooum.data.card.entity.CommentCard;
import com.sooum.data.member.entity.Member;
import com.sooum.data.report.entity.CommentReport;
import com.sooum.data.report.entity.reporttype.ReportType;
import com.sooum.data.report.repository.CommentReportRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

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
                .reportType(reportType)
                .build());
    }

    public void deleteReport(CommentCard cardPk) {
        commentReportRepository.deleteAllByCommentCardPk(cardPk);
    }

    public void deleteAllCommentReports(Long memberPk) {
        commentReportRepository.deleteAllCommentReports(memberPk);
    }

    public List<CommentReport> findCommentReports(Long cardPk) {
        return commentReportRepository.findByTargetCard_Pk(cardPk);
    }

    public void deleteAllCommentReports(List<CommentReport> reports) {
        commentReportRepository.deleteAllInBatch(reports);
    }
}
