package com.sooum.global.responseform;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.hateoas.EntityModel;

@Getter
@Setter
public class ResponseEntityModel<T> extends EntityModel<T> {
    private ResponseStatus status;

    @Builder
    public ResponseEntityModel(ResponseStatus status, T content) {
        super(content);
        this.status = status;
    }
}

