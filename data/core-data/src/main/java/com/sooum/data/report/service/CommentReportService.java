package com.sooum.data.report.service;

import com.sooum.data.card.entity.CommentCard;
import com.sooum.data.card.service.CommentCardService;
import com.sooum.data.card.service.CommentLikeService;
import com.sooum.data.member.entity.Member;
import com.sooum.data.report.entity.CommentReport;
import com.sooum.data.report.entity.reporttype.ReportType;
import com.sooum.data.report.repository.CommentReportRepository;
import com.sooum.data.tag.service.CommentTagService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CommentReportService {

    private final CommentCardService commentCardService;
    private final CommentReportRepository commentReportRepository;
    private final CommentTagService commentTagService;
    private final CommentLikeService commentLikeService;

    public boolean isCardReportedOverLimit(Long cardPk) {
        List<CommentReport> reports = commentReportRepository.findByTargetCard_Pk(cardPk);
        if(reports.size() >= 7) {
            commentReportRepository.deleteAllInBatch(reports);
            commentTagService.deleteByCommentCardPk(cardPk);
            commentLikeService.deleteAllFeedLikes(cardPk);
            commentCardService.deleteCommentCard(cardPk);
            return true;
        }
        return false;
    }

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
}
