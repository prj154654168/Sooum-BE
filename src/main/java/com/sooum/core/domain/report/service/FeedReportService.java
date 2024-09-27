package com.sooum.core.domain.report.service;

import com.sooum.core.domain.card.entity.Card;
import com.sooum.core.domain.card.entity.FeedCard;
import com.sooum.core.domain.card.service.FeedCardService;
import com.sooum.core.domain.member.entity.Member;
import com.sooum.core.domain.member.service.MemberBanService;
import com.sooum.core.domain.member.service.MemberService;
import com.sooum.core.domain.report.entity.FeedReport;
import com.sooum.core.domain.report.entity.reporttype.ReportType;
import com.sooum.core.domain.report.exception.DuplicateReportException;
import com.sooum.core.domain.report.repository.FeedReportRepository;
import com.sooum.core.global.config.jwt.InvalidTokenException;
import com.sooum.core.global.config.jwt.TokenProvider;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class FeedReportService {

    private final MemberService memberService;
    private final FeedCardService feedCardService;
    private final FeedReportRepository feedReportRepository;
    private final MemberBanService memberBanService;
    private final TokenProvider tokenProvider;

    @Transactional
    public void report(Long memberPk, Long cardPk, ReportType type, HttpServletRequest request) {
        Member member = memberService.findByPk(memberPk);
        FeedCard feedCard = feedCardService.findByPk(cardPk);

        validateDuplicateReport(member, feedCard);

        feedReportRepository.save(FeedReport.builder()
                .reporter(member)
                .targetCard(feedCard)
                .reportType(type)
                .build());

        if (isCardReportedOverLimit(feedCard)) {
            String accessToken = tokenProvider.getAccessToken(request)
                    .orElseThrow(InvalidTokenException::new);
            memberBanService.ban(member, accessToken);
        }
    }

    private void validateDuplicateReport(Member member, FeedCard card) {
        if(feedReportRepository.existsByReporterAndTargetCard(member, card))
            throw new DuplicateReportException();
    }

    private boolean isCardReportedOverLimit(Card card) {
        if(feedReportRepository.countFeedReportByCard(card) > 7) {
            feedCardService.deleteFeedCard(card.getPk());
            return true;
        }
        return false;
    }
}
