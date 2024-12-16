package com.sooum.api.report.service;

import com.sooum.api.report.exception.DuplicateReportException;
import com.sooum.data.card.entity.parenttype.CardType;
import com.sooum.data.card.service.CommentCardService;
import com.sooum.data.member.entity.Member;
import com.sooum.data.member.service.MemberService;
import com.sooum.data.report.entity.reporttype.ReportType;
import com.sooum.data.report.service.CommentReportService;
import com.sooum.data.report.service.FeedReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ReportService {
    private final MemberService memberService;
    private final CommentCardService commentCardService;
    private final FeedReportService feedReportService;
    private final CommentReportService commentReportService;
    private final FeedReportUseCase feedReportUseCase;
    private final CommentReportUseCase commentReportUseCase;

    @Transactional
    public void report(Long cardPk, ReportType reportType, Long requesterPk) {
        validateDuplicateReport(cardPk, requesterPk);

        Member requester = memberService.findMember(requesterPk);
        CardType cardType = commentCardService.findCardType(cardPk);
        switch (cardType) {
            case FEED_CARD -> feedReportUseCase.reportFeed(cardPk, requester, reportType);
            case COMMENT_CARD -> commentReportUseCase.reportComment(cardPk, requester, reportType);
        }
    }

    private void validateDuplicateReport(Long cardPk, Long memberPk) {
        if(feedReportService.isDuplicateReport(cardPk, memberPk) || commentReportService.isDuplicateReport(cardPk, memberPk))
            throw new DuplicateReportException();
    }
}
