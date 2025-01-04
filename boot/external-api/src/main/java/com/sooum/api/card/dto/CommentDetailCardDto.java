package com.sooum.api.card.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.sooum.api.member.dto.MemberDto;
import com.sooum.api.tag.dto.TagDto;
import com.sooum.data.card.entity.font.Font;
import com.sooum.data.card.entity.fontsize.FontSize;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.hateoas.Link;

import java.time.LocalDateTime;
import java.util.List;


@Getter
@Setter
public class CommentDetailCardDto extends CardDto{
    private String previousCardId;
    private Link previousCardImgLink;
    @JsonProperty(value = "isOwnCard")
    private boolean isOwnCard;
    private Double distance;
    @JsonProperty(value = "isFeedCardStory")
    private boolean isFeedCardStory;

    private MemberDto.DefaultMemberResponse member;
    private List<TagDto.ReadTagResponse> tags;

    @Builder
    public CommentDetailCardDto(String id, String content, LocalDateTime createdAt, int likeCnt, boolean isLiked, int commentCnt, boolean isCommentWritten, Link backgroundImgUrl, Font font, FontSize fontSize, String previousCardId, Link previousCardImgLink, boolean isOwnCard, MemberDto.DefaultMemberResponse member, List<TagDto.ReadTagResponse> tags, Double distance, boolean isFeedCardStory) {
        super(id, content, createdAt, likeCnt, isLiked, commentCnt, isCommentWritten, backgroundImgUrl, font, fontSize);
        this.previousCardId = previousCardId;
        this.previousCardImgLink = previousCardImgLink;
        this.isOwnCard = isOwnCard;
        this.member = member;
        this.tags = tags;
        this.distance = distance;
        this.isFeedCardStory = isFeedCardStory;
    }
}