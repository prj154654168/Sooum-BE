package com.sooum.global.advice;

import com.sooum.global.responseform.ResponseStatus;
import com.sooum.global.slack.service.SlackService;
import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.ConstraintViolationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.util.ContentCachingRequestWrapper;

import java.util.NoSuchElementException;

@Slf4j
@RestControllerAdvice
@RequiredArgsConstructor
public class ValidExceptionHandler {
    private final SlackService slackService;

    @ExceptionHandler(NoSuchElementException.class)
    public ResponseEntity<ResponseStatus> noSuchElementException(NoSuchElementException e, ContentCachingRequestWrapper request) {
        log.error(e.getMessage(), e);
        slackService.sendSlackMsg(e, request);
        return ResponseEntity.badRequest()
                .body(
                        ResponseStatus.builder()
                                .httpCode(HttpStatus.BAD_REQUEST.value())
                                .httpStatus(HttpStatus.BAD_REQUEST)
                                .responseMessage(e.getMessage())
                                .build()
                );
    }
    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<ResponseStatus> entityNotFoundException(Exception e, ContentCachingRequestWrapper request) {
        log.error(e.getMessage(), e);
        slackService.sendSlackMsg(e, request);
        return ResponseEntity.badRequest()
                .body(
                        ResponseStatus.builder()
                                .httpCode(HttpStatus.BAD_REQUEST.value())
                                .httpStatus(HttpStatus.BAD_REQUEST)
                                .responseMessage(e.getMessage())
                                .build()
                );
    }

    @ExceptionHandler(EntityExistsException.class)
    public ResponseEntity<ResponseStatus> entityExistsException(Exception e, ContentCachingRequestWrapper request) {
        log.error(e.getMessage(), e);
        slackService.sendSlackMsg(e, request);
        return ResponseEntity.badRequest()
                .body(
                        ResponseStatus.builder()
                                .httpCode(HttpStatus.BAD_REQUEST.value())
                                .httpStatus(HttpStatus.BAD_REQUEST)
                                .responseMessage(e.getMessage())
                                .build()
                );
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ResponseStatus> ConstraintViolationException1(Exception e) {
        log.error(e.getMessage(), e);
        return ResponseEntity.badRequest()
                .body(
                        ResponseStatus.builder()
                                .httpCode(HttpStatus.BAD_REQUEST.value())
                                .httpStatus(HttpStatus.BAD_REQUEST)
                                .responseMessage(e.getMessage())
                                .build()
                );
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ResponseStatus> MethodArgumentNotValidException(Exception e) {
        log.error(e.getMessage(), e);
        return ResponseEntity.badRequest()
                .body(
                        ResponseStatus.builder()
                                .httpCode(HttpStatus.BAD_REQUEST.value())
                                .httpStatus(HttpStatus.BAD_REQUEST)
                                .responseMessage(e.getMessage())
                                .build()
                );
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Void> internalServerException(Exception e, ContentCachingRequestWrapper request) {
        log.error(e.getMessage(), e);
        slackService.sendSlackMsg(e, request);
        return ResponseEntity.internalServerError()
                .build();
    }
}
