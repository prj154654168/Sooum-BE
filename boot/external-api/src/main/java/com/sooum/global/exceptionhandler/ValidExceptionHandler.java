package com.sooum.global.exceptionhandler;

import com.sooum.api.card.exception.ParentCardDeletedException;
import com.sooum.api.member.exception.BannedUserException;
import com.sooum.api.member.exception.DuplicateTokenException;
import com.sooum.global.config.jwt.exception.BlackListTokenException;
import com.sooum.global.config.jwt.exception.ExpiredRefreshTokenException;
import com.sooum.global.responseform.ResponseStatus;
import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.NoSuchElementException;

@RestControllerAdvice
@RequiredArgsConstructor
public class ValidExceptionHandler {
    @ExceptionHandler(NoSuchElementException.class)
    public ResponseEntity<ResponseStatus> noSuchElementException(NoSuchElementException e) {
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
    public ResponseEntity<ResponseStatus> entityNotFoundException(EntityNotFoundException e) {
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
    public ResponseEntity<ResponseStatus> entityExistsException(EntityExistsException e) {
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
    public ResponseEntity<Void> internalServerException(Exception e) {
        return ResponseEntity.internalServerError()
                .build();
    }

    @ExceptionHandler(BlackListTokenException.class)
    public ResponseEntity<ResponseStatus> blackListTokenException(BlackListTokenException e) {
        return ResponseEntity.status(HttpStatus.I_AM_A_TEAPOT)
                .body(
                        ResponseStatus.builder()
                                .httpCode(HttpStatus.I_AM_A_TEAPOT.value())
                                .httpStatus(HttpStatus.I_AM_A_TEAPOT)
                                .responseMessage(e.getMessage())
                                .build()
                );
    }

    @ExceptionHandler(ExpiredRefreshTokenException.class)
    public ResponseEntity<ResponseStatus> expiredRefreshTokenException(ExpiredRefreshTokenException e) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(
                        ResponseStatus.builder()
                                .httpCode(HttpStatus.FORBIDDEN.value())
                                .httpStatus(HttpStatus.FORBIDDEN)
                                .responseMessage(e.getMessage())
                                .build()
                );
    }

    @ExceptionHandler(BannedUserException.class)
    public ResponseEntity<ResponseStatus> bannedUserException(BannedUserException e) {
        return ResponseEntity.status(HttpStatus.LOCKED)
                .body(
                        ResponseStatus.builder()
                                .httpCode(HttpStatus.LOCKED.value())
                                .httpStatus(HttpStatus.LOCKED)
                                .responseMessage(e.getMessage())
                                .build()
                );
    }

    @ExceptionHandler(DuplicateTokenException.class)
    public ResponseEntity<ResponseStatus> duplicateTokenException(DuplicateTokenException e) {
        return ResponseEntity.status(HttpStatus.I_AM_A_TEAPOT)
                .body(
                        ResponseStatus.builder()
                                .httpCode(HttpStatus.I_AM_A_TEAPOT.value())
                                .httpStatus(HttpStatus.I_AM_A_TEAPOT)
                                .responseMessage(e.getMessage())
                                .build()
                );
    }

    @ExceptionHandler(ParentCardDeletedException.class)
    public ResponseEntity<ResponseStatus> parentCardDeletedException(ParentCardDeletedException e) {
        return ResponseEntity.status(HttpStatus.PAYMENT_REQUIRED)
                .body(
                        ResponseStatus.builder()
                                .httpCode(HttpStatus.PAYMENT_REQUIRED.value())
                                .httpStatus(HttpStatus.PAYMENT_REQUIRED)
                                .responseMessage(e.getMessage())
                                .build()
                );
    }
}