package com.sooum.core.domain.tag.dto;

import lombok.Builder;
import lombok.Getter;
import org.springframework.hateoas.RepresentationModel;

public class TagDto{

    @Getter
    public static class ReadTagResponse extends RepresentationModel<ReadTagResponse> {
        private long id;
        private String content;

        @Builder
        public ReadTagResponse(long id, String content) {
            this.id = id;
            this.content = content;
        }
    }
}
