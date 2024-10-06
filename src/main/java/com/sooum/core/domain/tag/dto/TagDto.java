package com.sooum.core.domain.tag.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
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

    @Getter
    @Setter
    @NoArgsConstructor
    public static class TagSummary {
        private String content;
        private int cardCnt;
        @JsonProperty(value = "isFavorite")
        private boolean isFavorite;

        @Builder
        public TagSummary(String content, int cardCnt, boolean isFavorite) {
            this.content = content;
            this.cardCnt = cardCnt;
            this.isFavorite = isFavorite;
        }
    }
}
