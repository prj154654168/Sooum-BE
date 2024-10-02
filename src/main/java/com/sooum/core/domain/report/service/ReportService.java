package com.sooum.core.domain.report.service;

import com.sooum.core.domain.card.entity.Card;
import com.sooum.core.domain.card.entity.CommentCard;
import com.sooum.core.domain.card.entity.FeedCard;
import com.sooum.core.domain.card.service.CommentCardService;
import com.sooum.core.domain.card.service.FeedCardService;
import com.sooum.core.domain.member.entity.Member;
import com.sooum.core.domain.member.service.MemberService;
import com.sooum.core.domain.report.entity.reporttype.ReportType;
import com.sooum.core.domain.report.exception.DuplicateReportException;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ReportService {

    private final MemberService memberService;
    private final FeedCardService feedCardService;
    private final CommentCardService commentCardService;
    private final FeedReportService feedReportService;
    private final CommentReportService commentReportService;

    @Transactional
    public void report(Long cardPk, ReportType reportType, Long memberPk) {
        validateDuplicateReport(cardPk, memberPk);

        Member member = memberService.findByPk(memberPk);
        if(feedCardService.isExistFeedCard(cardPk)) {
            FeedCard feedCard = feedCardService.findByPk(cardPk);
            reportFeed(feedCard, member, reportType);
        } else {
            CommentCard commentCard = commentCardService.findByPk(cardPk);
            reportComment(commentCard, member, reportType);
        }
    }

    private void reportFeed(FeedCard card, Member member, ReportType reportType) {
        feedReportService.save(member, card, reportType);

        if (isReportedOverLimit(card))
            card.getWriter().ban();
    }

    private void reportComment(CommentCard card, Member member, ReportType reportType) {
        commentReportService.save(member, card, reportType);

        if(isReportedOverLimit(card))
            card.getWriter().ban();
    }


    private boolean isReportedOverLimit(Card card) {
        if(card instanceof FeedCard)
            return feedReportService.isCardReportedOverLimit(card.getPk());
        if(card instanceof CommentCard)
            return commentReportService.isCardReportedOverLimit(card.getPk());

        throw new EntityNotFoundException();
    }


    private void validateDuplicateReport(Long cardPk, Long memberPk) {
        if(feedReportService.isDuplicateReport(cardPk, memberPk)
                || commentReportService.isDuplicateReport(cardPk, memberPk))
            throw new DuplicateReportException();
    }
}
