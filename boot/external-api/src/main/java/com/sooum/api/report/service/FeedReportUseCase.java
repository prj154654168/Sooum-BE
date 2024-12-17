package com.sooum.api.report.service;

import com.sooum.api.card.service.CardService;
import com.sooum.api.notification.dto.FCMDto;
import com.sooum.api.notification.service.NotificationUseCase;
import com.sooum.data.card.entity.FeedCard;
import com.sooum.data.card.service.FeedCardService;
import com.sooum.data.member.entity.Member;
import com.sooum.data.notification.entity.notificationtype.NotificationType;
import com.sooum.data.report.entity.FeedReport;
import com.sooum.data.report.entity.reporttype.ReportType;
import com.sooum.data.report.service.FeedReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class FeedReportUseCase {
    private final FeedCardService feedCardService;
    private final FeedReportService feedReportService;
    private final CardService cardService;
    private final NotificationUseCase notificationUseCase;
    private final ApplicationEventPublisher publisher;

    private static final int REPORT_LIMIT = 7;

    @Transactional
    public void reportFeed(Long cardPk, Member requester, ReportType reportType) {
        FeedCard card = feedCardService.findByPk(cardPk);
        feedReportService.save(requester, card, reportType);

        deleteFeedAndUpdateMemberBanIfReportOverLimit(card);
    }

    private void deleteFeedAndUpdateMemberBanIfReportOverLimit(FeedCard feedCard) {
        List<FeedReport> reports = feedReportService.findFeedReport(feedCard.getPk());
        if (isReportedOverLimit(reports)) {
            deleteFeedAndAssociationsByReport(feedCard, reports);
            writerBan(feedCard.getWriter());
        }
    }

    private void deleteFeedAndAssociationsByReport(FeedCard feedCard, List<FeedReport> reports) {
        cardService.deleteFeedAndAssociationsByReport(reports, feedCard);
        publisher.publishEvent(FCMDto.SystemFcmSendEvent.builder()
                .notificationType(NotificationType.DELETED)
                .deviceType(feedCard.getWriter().getDeviceType())
                .fcmToken(feedCard.getWriter().getFirebaseToken())
                .source(this)
                .build()
        );
    }

    private void writerBan(Member writer) {
        notificationUseCase.saveBlockedHistoryAndDeletePreviousHistories(writer.getPk());
        writer.ban();
        publisher.publishEvent(FCMDto.SystemFcmSendEvent.builder()
                .notificationType(NotificationType.BLOCKED)
                .deviceType(writer.getDeviceType())
                .fcmToken(writer.getFirebaseToken())
                .source(this)
                .build());
    }

    private boolean isReportedOverLimit(List<FeedReport> reports) {
        return reports.size() >= REPORT_LIMIT;
    }
}
