package com.sooum.global.responseform;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.hateoas.CollectionModel;

@Getter
@Setter
public class ResponseCollectionModel<T> extends CollectionModel<T> {
    private ResponseStatus status;

    @Builder
    public ResponseCollectionModel(ResponseStatus status, Iterable<T> content) {
        super(content);
        this.status = status;
    }
}

