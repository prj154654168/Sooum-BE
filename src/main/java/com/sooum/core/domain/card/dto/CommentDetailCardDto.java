package com.sooum.core.domain.card.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.sooum.core.domain.card.entity.font.Font;
import com.sooum.core.domain.card.entity.fontsize.FontSize;
import com.sooum.core.domain.member.dto.MemberDto;
import com.sooum.core.domain.tag.dto.TagDto;
import lombok.Builder;
import lombok.Getter;
import org.springframework.hateoas.Link;

import java.time.LocalDateTime;
import java.util.List;


@Getter
public class CommentDetailCardDto extends CardDto{
    private String previousCardId;
    private Link previousCardImgLink;
    @JsonProperty(value = "isOwnCard")
    private boolean isOwnCard;
    private Double distance;

    private MemberDto.DefaultMemberResponse member;
    private List<TagDto.ReadTagResponse> tags;

    @Builder
    public CommentDetailCardDto(String id, String content, LocalDateTime createdAt, int likeCnt, boolean isLiked, int commentCnt, boolean isCommentWritten, Link backgroundImgUrl, Font font, FontSize fontSize, String previousCardId, Link previousCardImgLink, boolean isOwnCard, MemberDto.DefaultMemberResponse member, List<TagDto.ReadTagResponse> tags, Double distance) {
        super(id, content, createdAt, likeCnt, isLiked, commentCnt, isCommentWritten, backgroundImgUrl, font, fontSize);
        this.previousCardId = previousCardId;
        this.previousCardImgLink = previousCardImgLink;
        this.isOwnCard = isOwnCard;
        this.member = member;
        this.tags = tags;
        this.distance = distance;
    }
}
