package com.sooum.core.domain.report.exception;

import jakarta.persistence.EntityExistsException;

public class DuplicateReportException extends EntityExistsException {
    public DuplicateReportException() {
        super("이미 신고된 카드입니다.");
    }
}
