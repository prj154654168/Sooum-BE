package com.sooum.data.report.service;

import com.sooum.data.card.entity.CommentCard;
import com.sooum.data.card.entity.FeedCard;
import com.sooum.data.card.repository.CommentCardRepository;
import com.sooum.data.card.service.CommentCardService;
import com.sooum.data.card.service.CommentLikeService;
import com.sooum.data.card.service.FeedCardService;
import com.sooum.data.card.service.FeedLikeService;
import com.sooum.data.member.entity.Member;
import com.sooum.data.report.entity.FeedReport;
import com.sooum.data.report.entity.reporttype.ReportType;
import com.sooum.data.report.repository.FeedReportRepository;
import com.sooum.data.tag.service.CommentTagService;
import com.sooum.data.tag.service.FeedTagService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class FeedReportService {

    private final FeedCardService feedCardService;
    private final FeedReportRepository feedReportRepository;
    private final CommentCardService commentCardService;
    private final CommentCardRepository commentCardRepository;
    private final CommentTagService commentTagService;
    private final FeedTagService feedTagService;
    private final FeedLikeService feedLikeService;
    private final CommentLikeService commentLikeService;

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
                .reportType(reportType)
                .build());
    }

    public boolean isCardReportedOverLimit(Long cardPk) {
        List<FeedReport> reports = feedReportRepository.findByTargetCardPk(cardPk);
        if(reports.size() >= 7) {
            feedReportRepository.deleteAllInBatch(reports);

            if(commentCardService.hasChildCard(cardPk)) {
                List<CommentCard> comments = commentCardService.findByMasterCardPk(cardPk);
                commentTagService.deleteByCommentCards(comments);
                commentLikeService.deleteByCommentCards(comments);
                commentCardRepository.deleteAllInBatch(comments);
            }

            feedTagService.deleteByFeedCardPk(cardPk);
            feedLikeService.deleteAllFeedLikes(cardPk);
            feedCardService.deleteFeedCard(cardPk);
            return true;
        }
        return false;
    }

    public void deleteAllFeedReports(Long memberPk) {
        feedReportRepository.deleteAllFeedReports(memberPk);
    }
}
