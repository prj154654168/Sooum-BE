package com.sooum.core.global.responseform;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.hateoas.CollectionModel;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@Getter
@Setter
public class ResponseEntityModel<T> extends CollectionModel<T> {
    private ResponseStatus status;

    @Builder
    public ResponseEntityModel(ResponseStatus status, Iterable<T> content) {
        super(content);
        this.status = status;
    }

    @Builder
    public record ResponseHandler(Integer httpCode, HttpStatus httpStatus, String responseMessage) {
    }
}
