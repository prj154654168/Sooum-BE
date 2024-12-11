package com.sooum.api.report.dto;

import com.sooum.data.report.entity.reporttype.ReportType;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class ReportDto {
    @Getter
    @NoArgsConstructor
    public static class Request {
        private ReportType reportType;
    }
}
