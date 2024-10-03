package com.sooum.core.domain.card.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.sooum.core.domain.card.entity.FeedCard;
import com.sooum.core.domain.card.entity.font.Font;
import com.sooum.core.domain.card.entity.fontsize.FontSize;
import com.sooum.core.domain.card.entity.imgtype.ImgType;
import com.sooum.core.domain.member.entity.Member;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;

import java.util.List;

@Getter
@Setter
public class CreateFeedCardDto extends CreateCardDto {
    @JsonProperty(value = "isPublic")
    private boolean isPublic;
    @JsonProperty(value = "isStory")
    private boolean isStory;
    private List<String> feedTags;

    @Builder
    public CreateFeedCardDto(String content, boolean isDistanceShared, double latitude, double longitude, Font font, ImgType imgType, String imgName, boolean isPublic, boolean isStory, List<String> feedTags) {
        super(content, isDistanceShared, latitude, longitude, font, imgType, imgName);
        this.isPublic = isPublic;
        this.isStory = isStory;
        this.feedTags = feedTags;
    }

    public FeedCard of(Member member) {
        return FeedCard.builder()
                .content(this.getContent())
                .font(this.getFont())
                .fontSize(FontSize.MEDIUM)
                .isStory(this.isStory())
                .imgType(this.getImgType())
                .imgName(this.getImgName())
                .isPublic(this.isPublic())
                .location(this.isDistanceShared()
                        ? new GeometryFactory().createPoint(new Coordinate(this.getLongitude(), this.getLatitude()))
                        : null)
                .writer(member).build();
    }

    @Override
    public List<String> getTags() {
        return this.feedTags;
    }
}
