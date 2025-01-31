package com.sooum.api.card.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.sooum.api.member.dto.MemberDto;
import com.sooum.api.tag.dto.TagDto;
import com.sooum.data.card.entity.CommentCard;
import com.sooum.data.card.entity.CommentLike;
import com.sooum.data.card.entity.FeedCard;
import com.sooum.data.card.entity.FeedLike;
import com.sooum.data.card.entity.font.Font;
import com.sooum.data.card.entity.fontsize.FontSize;
import com.sooum.global.util.CardUtils;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.hateoas.Link;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;


@Getter
@Setter
public class CommentDetailCardDto extends CardDto{
    private String previousCardId;
    @JsonProperty(value = "isPreviousCardDelete")
    private boolean isPreviousCardDelete;
    private Link previousCardImgLink;
    @JsonProperty(value = "isOwnCard")
    private boolean isOwnCard;
    private Double distance;
    @JsonProperty(value = "isFeedCardStory")
    private boolean isFeedCardStory;

    private MemberDto.DefaultMemberResponse member;
    private List<TagDto.ReadTagResponse> tags;

    @Builder
    public CommentDetailCardDto(String id, String content, LocalDateTime createdAt, int likeCnt, boolean isLiked, int commentCnt, boolean isCommentWritten, Link backgroundImgUrl, Font font, FontSize fontSize, String previousCardId, boolean isPreviousCardDelete, Link previousCardImgLink, boolean isOwnCard, MemberDto.DefaultMemberResponse member, List<TagDto.ReadTagResponse> tags, Double distance, boolean isFeedCardStory) {
        super(id, content, createdAt, likeCnt, isLiked, commentCnt, isCommentWritten, backgroundImgUrl, font, fontSize);
        this.previousCardId = previousCardId;
        this.isPreviousCardDelete = isPreviousCardDelete;
        this.previousCardImgLink = previousCardImgLink;
        this.isOwnCard = isOwnCard;
        this.member = member;
        this.tags = tags;
        this.distance = distance;
        this.isFeedCardStory = isFeedCardStory;
    }
}