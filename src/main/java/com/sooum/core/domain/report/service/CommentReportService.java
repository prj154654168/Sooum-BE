package com.sooum.core.domain.report.service;

import com.sooum.core.domain.card.entity.CommentCard;
import com.sooum.core.domain.card.service.CommentCardService;
import com.sooum.core.domain.member.entity.Member;
import com.sooum.core.domain.member.service.MemberBanService;
import com.sooum.core.domain.member.service.MemberService;
import com.sooum.core.domain.report.entity.CommentReport;
import com.sooum.core.domain.report.entity.reporttype.ReportType;
import com.sooum.core.domain.report.exception.DuplicateReportException;
import com.sooum.core.domain.report.repository.CommentReportRepository;
import com.sooum.core.global.config.jwt.InvalidTokenException;
import com.sooum.core.global.config.jwt.TokenProvider;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CommentReportService {

    private final MemberService memberService;
    private final CommentCardService commentCardService;
    private final CommentReportRepository commentReportRepository;
    private final TokenProvider tokenProvider;
    private final MemberBanService memberBanService;

    public void report(Long memberPk, Long cardPk, ReportType type, HttpServletRequest request) {
        Member member = memberService.findByPk(memberPk);
        CommentCard commentCard = commentCardService.findByPk(cardPk);

        validateDuplicateReport(member, commentCard);

        commentReportRepository.save(CommentReport.builder()
                .reporter(member)
                .targetCard(commentCard)
                .reportType(type)
                .build());

        if (isCardReportedOverLimit(commentCard)) {
            String accessToken = tokenProvider.getAccessToken(request)
                    .orElseThrow(InvalidTokenException::new);
            memberBanService.ban(member, accessToken);
        }
    }

    private void validateDuplicateReport(Member member, CommentCard card) {
        if(commentReportRepository.existsByReporterAndTargetCard(member, card))
            throw new DuplicateReportException();
    }

    private boolean isCardReportedOverLimit(CommentCard card) {
        List<CommentReport> reports = commentReportRepository.findByTargetCard(card);
        if(reports.size() >= 7) {
            commentReportRepository.deleteAllInBatch(reports);
            commentCardService.deleteCommentCard(card.getPk());
            return true;
        }
        return false;
    }
}
