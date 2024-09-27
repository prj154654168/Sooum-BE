package com.sooum.core.domain.report.controller;

import com.sooum.core.domain.report.entity.reporttype.ReportType;
import com.sooum.core.domain.report.service.FeedReportService;
import com.sooum.core.global.auth.annotation.CurrentUser;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/report")
public class ReportController {

    private final FeedReportService feedReportService;

    @PostMapping("/feed")
    public ResponseEntity<?> reportFeedCard(@CurrentUser Long memberPk,
                                            @RequestParam Long cardPk,
                                            @RequestParam ReportType reportType,
                                            HttpServletRequest request) {
        feedReportService.report(memberPk, cardPk, reportType, request);
        return ResponseEntity.ok(null);
    }

    @PostMapping("/comment")
    public ResponseEntity<?> reportCommentCard(@CurrentUser Long memberPk, @RequestParam Long cardPk) {
        return null;
    }
}
