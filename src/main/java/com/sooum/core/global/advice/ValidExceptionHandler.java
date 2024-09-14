package com.sooum.core.global.advice;

import jakarta.persistence.EntityNotFoundException;
import lombok.Builder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ValidExceptionHandler {
    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<ErrorForm> entityNotFoundException(Exception e) {
        return ResponseEntity.badRequest()
                .body(
                        ErrorForm.builder()
                                .httpMethod(HttpStatus.BAD_REQUEST.getReasonPhrase())
                                .responseMessage(e.getMessage())
                                .build()
                );
    }

    @Builder
    private record ErrorForm(String httpMethod, String responseMessage){}
}
