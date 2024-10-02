package com.sooum.core.domain.card.dto;

import com.sooum.core.domain.card.entity.font.Font;
import com.sooum.core.domain.member.dto.MemberDto;
import com.sooum.core.domain.tag.dto.TagDto;
import lombok.Builder;
import lombok.Getter;
import org.springframework.hateoas.Link;

import java.time.LocalDateTime;
import java.util.List;


@Getter
public class CommentDetailCardDto extends CardDetailDto {
    private long previousCardId;
    private Link previousCardImgLink;

    @Builder
    public CommentDetailCardDto(Font font, String id, String content, Link backgroundImgUrl, Double distance, LocalDateTime createdAt, boolean isOwnCard, MemberDto.DefaultMemberResponse member, List<TagDto.ReadTagResponse> tags, long previousCardId, Link previousCardImgLink) {
        super(font, id, content, backgroundImgUrl, distance, createdAt, isOwnCard, member, tags);
        this.previousCardId = previousCardId;
        this.previousCardImgLink = previousCardImgLink;
    }

}
