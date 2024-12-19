package com.sooum.global.exceptionhandler;

import com.sooum.api.member.exception.BannedUserException;
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

    @ExceptionHandler(BannedUserException.class)
    public ResponseEntity<ResponseStatus> bannedUserException(BannedUserException e) {
        return ResponseEntity.badRequest()
                .body(
                        ResponseStatus.builder()
                                .httpCode(HttpStatus.LOCKED.value())
                                .httpStatus(HttpStatus.LOCKED)
                                .responseMessage(e.getMessage())
                                .build()
                );
    }
}