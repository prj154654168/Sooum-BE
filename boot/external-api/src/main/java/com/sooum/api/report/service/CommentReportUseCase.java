package com.sooum.api.report.service;

import com.sooum.api.card.service.CardService;
import com.sooum.api.notification.dto.FCMDto;
import com.sooum.api.notification.service.NotificationUseCase;
import com.sooum.data.card.entity.CommentCard;
import com.sooum.data.card.service.CommentCardService;
import com.sooum.data.member.entity.Member;
import com.sooum.data.notification.entity.notificationtype.NotificationType;
import com.sooum.data.report.entity.CommentReport;
import com.sooum.data.report.entity.reporttype.ReportType;
import com.sooum.data.report.service.CommentReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CommentReportUseCase {
    private final CommentCardService commentCardService;
    private final CommentReportService commentReportService;
    private final CardService cardService;
    private final NotificationUseCase notificationUseCase;
    private final ApplicationEventPublisher sendFCMEventPublisher;

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
            deleteCommentAndAssociationsByReport(commentCard, reports);
            writerBan(card.getWriter());
        }
    }

    private void deleteCommentAndAssociationsByReport(CommentCard commentCard, List<CommentReport> reports) {
        cardService.deleteCommentAndAssociationsByReport(reports, commentCard);
        sendFCMEventPublisher.publishEvent(FCMDto.SystemFcmSendEvent.builder()
                .notificationType(NotificationType.DELETED)
                .targetDeviceType(commentCard.getWriter().getDeviceType())
                .targetFcmToken(commentCard.getWriter().getFirebaseToken())
                .source(this)
                .build()
        );
    }

    private void writerBan(Member writer) {
        notificationUseCase.saveBlockedHistoryAndDeletePreviousHistories(writer.getPk());
        writer.ban();
        sendFCMEventPublisher.publishEvent(FCMDto.SystemFcmSendEvent.builder()
                .notificationType(NotificationType.BLOCKED)
                .targetDeviceType(writer.getDeviceType())
                .targetFcmToken(writer.getFirebaseToken())
                .source(this)
                .build());
    }

    private boolean isReportedOverLimit(List<CommentReport> reports) {
        return reports.size() >= REPORT_LIMIT;
    }
}
