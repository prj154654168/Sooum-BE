package com.sooum.core.domain.report.controller;

import com.sooum.core.domain.report.entity.reporttype.ReportType;
import com.sooum.core.domain.report.service.CommentReportService;
import com.sooum.core.domain.report.service.FeedReportService;
import com.sooum.core.global.auth.annotation.CurrentUser;
import com.sooum.core.global.responseform.ResponseStatus;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;

@RestController
@RequiredArgsConstructor
@RequestMapping("/report")
public class ReportController {

    private final FeedReportService feedReportService;
    private final CommentReportService commentReportService;

    @PostMapping("/feed")
    public ResponseEntity<?> reportFeedCard(@CurrentUser Long memberPk,
                                            @RequestParam("cardPk") Long cardPk,
                                            @RequestParam("reportType") ReportType reportType,
                                            HttpServletRequest request) {
        feedReportService.report(memberPk, cardPk, reportType, request);
        return ResponseEntity.created(URI.create(""))
                .body(
                        ResponseStatus.builder()
                                .httpCode(HttpStatus.CREATED.value())
                                .httpStatus(HttpStatus.CREATED)
                                .responseMessage("report successfully")
                                .build()
                );
    }

    @PostMapping("/comment")
    public ResponseEntity<?> reportCommentCard(@CurrentUser Long memberPk,
                                               @RequestParam("cardPk") Long cardPk,
                                               @RequestParam("reportType") ReportType reportType,
                                               HttpServletRequest request) {
        commentReportService.report(memberPk, cardPk, reportType, request);
        return ResponseEntity.created(URI.create(""))
                .body(
                        ResponseStatus.builder()
                                .httpCode(HttpStatus.CREATED.value())
                                .httpStatus(HttpStatus.CREATED)
                                .responseMessage("report successfully")
                                .build()
                );
    }
}
