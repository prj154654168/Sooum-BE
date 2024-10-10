package com.sooum.core.domain.card.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.sooum.core.domain.card.entity.font.Font;
import com.sooum.core.domain.card.entity.fontsize.FontSize;
import com.sooum.core.domain.member.dto.MemberDto;
import com.sooum.core.domain.tag.dto.TagDto;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.hateoas.Link;

import java.time.LocalDateTime;
import java.util.List;


@Getter
@Setter
public class FeedDetailCardDto extends CardDto {
    private LocalDateTime storyExpirationTime;
    @JsonProperty(value = "isOwnCard")
    private boolean isOwnCard;
    private Double distance;

    private MemberDto.DefaultMemberResponse member;
    private List<TagDto.ReadTagResponse> tags;

    @Builder
    public FeedDetailCardDto(String id, String content, LocalDateTime createdAt, int likeCnt, boolean isLiked, int commentCnt, boolean isCommentWritten, Link backgroundImgUrl, Font font, FontSize fontSize, boolean isOwnCard, boolean isStory, MemberDto.DefaultMemberResponse member, List<TagDto.ReadTagResponse> tags, Double distance) {
        super(id, content, createdAt, likeCnt, isLiked, commentCnt, isCommentWritten, backgroundImgUrl, font, fontSize);
        this.storyExpirationTime = isStory ? createdAt.plusDays(1L) : null;
        this.isOwnCard = isOwnCard;
        this.member = member;
        this.tags = tags;
        this.distance = distance;
    }
}