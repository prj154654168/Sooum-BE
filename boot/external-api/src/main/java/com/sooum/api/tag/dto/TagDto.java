package com.sooum.api.tag.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.RepresentationModel;

import java.util.List;

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
    public static class RecommendTag extends RepresentationModel<RecommendTag> {
        private final String tagId;
        private final String tagContent;
        private final String tagUsageCnt;

        @Builder
        public RecommendTag(String tagId, String tagContent, String tagUsageCnt) {
            this.tagId = tagId;
            this.tagContent = tagContent;
            this.tagUsageCnt = tagUsageCnt;
        }
    }

    public record RelatedTag(
            String tagId,
            Integer count,
            String content
    ) {}

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

    @Getter
    public static class FavoriteTag extends RepresentationModel<FavoriteTag> {
        private String id;
        private String tagContent;
        private String tagUsageCnt;
        private List<PreviewCard> previewCards;

        @Builder
        public FavoriteTag(String tagContent, String tagUsageCnt, List<PreviewCard> previewCards, String id) {
            this.id = id;
            this.tagContent = tagContent;
            this.tagUsageCnt = tagUsageCnt;
            this.previewCards = previewCards;
        }

        @Getter
        public static class PreviewCard extends RepresentationModel<PreviewCard> {
            private String id;
            private String content;
            private Link backgroundImgUrl;


            @Builder
            public PreviewCard(String id, String content, Link backgroundImgUrl) {
                this.id = id;
                this.content = content;
                this.backgroundImgUrl = backgroundImgUrl;
            }
        }
    }
}
