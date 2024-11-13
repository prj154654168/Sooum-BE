package com.sooum.api.report.controller;

import com.sooum.api.report.service.ReportService;
import com.sooum.data.report.entity.reporttype.ReportType;
import com.sooum.global.auth.annotation.CurrentUser;
import com.sooum.global.responseform.ResponseStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@RestController
@RequiredArgsConstructor
@RequestMapping("/report")
public class ReportController {

    private final ReportService reportService;

    @PostMapping("/cards/{cardPk}")
    public ResponseEntity<?> reportFeedCard(@PathVariable("cardPk") Long cardPk,
                                            @RequestParam("reportType") ReportType reportType,
                                            @CurrentUser Long memberPk) {
        reportService.report(cardPk, reportType, memberPk);
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
