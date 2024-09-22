package com.sooum.core.global.advice;

import com.sooum.core.global.responseform.ResponseStatus;
import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ValidExceptionHandler {
    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<ResponseStatus> entityNotFoundException(Exception e) {
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
    public ResponseEntity<ResponseStatus> entityExistsException(Exception e) {
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
        return ResponseEntity.badRequest()
                .body(
                        ResponseStatus.builder()
                                .httpCode(HttpStatus.BAD_REQUEST.value())
                                .httpStatus(HttpStatus.BAD_REQUEST)
                                .responseMessage(e.getMessage())
                                .build()
                );
    }
}
