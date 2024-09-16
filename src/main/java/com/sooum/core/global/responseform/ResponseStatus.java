package com.sooum.core.global.responseform;

import lombok.Builder;
import org.springframework.http.HttpStatus;

@Builder
public record ResponseStatus(Integer httpCode, HttpStatus httpStatus, String responseMessage) {
}
