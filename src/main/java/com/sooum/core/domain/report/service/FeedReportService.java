package com.sooum.core.domain.report.service;

import com.sooum.core.domain.card.entity.CommentCard;
import com.sooum.core.domain.card.entity.FeedCard;
import com.sooum.core.domain.card.repository.CommentCardRepository;
import com.sooum.core.domain.card.service.CommentCardService;
import com.sooum.core.domain.card.service.CommentLikeService;
import com.sooum.core.domain.card.service.FeedCardService;
import com.sooum.core.domain.card.service.FeedLikeService;
import com.sooum.core.domain.member.entity.Member;
import com.sooum.core.domain.report.entity.FeedReport;
import com.sooum.core.domain.report.entity.reporttype.ReportType;
import com.sooum.core.domain.report.repository.FeedReportRepository;
import com.sooum.core.domain.tag.service.CommentTagService;
import com.sooum.core.domain.tag.service.FeedTagService;
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
}
