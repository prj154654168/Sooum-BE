package com.sooum.api.report.exception;

import java.util.NoSuchElementException;

public class DuplicateReportException extends NoSuchElementException {
    public DuplicateReportException() {
        super("이미 신고된 카드입니다.");
    }
}
