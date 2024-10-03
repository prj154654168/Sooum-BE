package com.sooum.core.domain.tag.dto;

import lombok.Builder;
import lombok.Getter;
import org.springframework.hateoas.RepresentationModel;

public class TagDto{

    @Getter
    public static class ReadTagResponse extends RepresentationModel<ReadTagResponse> {
        private String id;
        private String content;

        @Builder
        public ReadTagResponse(String id, String content) {
            this.id = id;
            this.content = content;
        }
    }

    public record RelatedTag(
            Integer count,
            String content
    ) {}
}
