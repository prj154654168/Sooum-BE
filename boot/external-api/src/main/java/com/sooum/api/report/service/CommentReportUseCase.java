package com.sooum.api.report.service;

import com.sooum.api.card.service.CardService;
import com.sooum.api.notification.service.SaveNotificationService;
import com.sooum.data.card.entity.CommentCard;
import com.sooum.data.card.service.CommentCardService;
import com.sooum.data.member.entity.Member;
import com.sooum.data.report.entity.CommentReport;
import com.sooum.data.report.entity.reporttype.ReportType;
import com.sooum.data.report.service.CommentReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CommentReportUseCase {
    private final CommentCardService commentCardService;
    private final CommentReportService commentReportService;
    private final CardService cardService;
    private final SaveNotificationService saveNotificationService;

    private static final int REPORT_LIMIT = 7;

    @Transactional
    public void reportComment(Long cardPk, Member member, ReportType reportType) {
        CommentCard commentCard = commentCardService.findByPk(cardPk);
        commentReportService.save(member, commentCard, reportType);

        deleteCommentAndUpdateMemberBanIfReportOverLimit(commentCard, commentCard);
    }

    private void deleteCommentAndUpdateMemberBanIfReportOverLimit(CommentCard commentCard, CommentCard card) {
        List<CommentReport> reports = commentReportService.findCommentReports(commentCard.getPk());
        if (isReportedOverLimit(reports)) {
            cardService.deleteCommentAndAssociationsByReport(reports, commentCard);
            writerBan(card.getWriter());
        }
    }

    private void writerBan(Member writer) {
        saveNotificationService.saveBlockedHistory(writer.getPk());
        writer.ban();
    }

    private boolean isReportedOverLimit(List<CommentReport> reports) {
        return reports.size() >= REPORT_LIMIT;
    }
}
