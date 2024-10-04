package com.sooum.core.domain.card.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.sooum.core.domain.card.entity.font.Font;
import com.sooum.core.domain.member.dto.MemberDto;
import com.sooum.core.domain.tag.dto.TagDto;
import lombok.Getter;
import lombok.Setter;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.RepresentationModel;

import java.time.LocalDateTime;
import java.util.List;

@Setter
@Getter
public abstract class CardDetailDto extends RepresentationModel<CardDetailDto> {
    private String id;
    private String content;
    private Link backgroundImgUrl;
    private Font font;
    private Double distance;
    private LocalDateTime createdAt;
    @JsonProperty(value = "isOwnCard")
    private boolean isOwnCard;

    private MemberDto.DefaultMemberResponse member;
    private List<TagDto.ReadTagResponse> tags;

    public CardDetailDto(Font font, String id, String content, Link backgroundImgUrl, Double distance, LocalDateTime createdAt, boolean isOwnCard, MemberDto.DefaultMemberResponse member, List<TagDto.ReadTagResponse> tags) {
        this.font = font;
        this.id = id;
        this.content = content;
        this.backgroundImgUrl = backgroundImgUrl;
        this.distance = distance;
        this.createdAt = createdAt;
        this.isOwnCard = isOwnCard;
        this.member = member;
        this.tags = tags;
    }
}
