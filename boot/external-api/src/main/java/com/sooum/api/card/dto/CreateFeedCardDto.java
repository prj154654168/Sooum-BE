package com.sooum.api.card.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.sooum.data.card.entity.FeedCard;
import com.sooum.data.card.entity.font.Font;
import com.sooum.data.card.entity.fontsize.FontSize;
import com.sooum.data.card.entity.imgtype.CardImgType;
import com.sooum.data.common.deactivatewords.DeactivateWords;
import com.sooum.data.member.entity.Member;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;

import java.util.List;
import java.util.Objects;

@Getter
@Setter
@NoArgsConstructor
public class CreateFeedCardDto extends CreateCardDto {
    @JsonProperty(value = "isPublic")
    private boolean isPublic;
    @JsonProperty(value = "isStory")
    private boolean isStory;
    private List<String> feedTags;

    public CreateFeedCardDto(String content, boolean isDistanceShared, double latitude, double longitude, Font font, CardImgType imgType, String imgName, boolean isPublic, boolean isStory, List<String> feedTags) {
        super(content, isDistanceShared, latitude, longitude, font, imgType, imgName);
        this.isPublic = isPublic;
        this.isStory = isStory;
        this.feedTags = feedTags;
    }

    public FeedCard of(Member member, String writerIp) {
        return FeedCard.builder()
                .content(this.getContent())
                .font(this.getFont())
                .fontSize(FontSize.NONE)
                .isStory(this.isStory())
                .imgType(this.getImgType())
                .imgName(this.getImgName())
                .isPublic(this.isPublic())
                .location(this.isDistanceShared()
                        ? new GeometryFactory().createPoint(new Coordinate(this.getLongitude(), this.getLatitude()))
                        : null)
                .isFeedActive(Objects.isNull(this.feedTags) || DeactivateWords.deactivateWordsList.stream().noneMatch(word -> feedTags.contains(word)))
                .writer(member)
                .writerIp(writerIp)
        .build();
    }

    @Override
    public List<String> getTags() {
        return this.feedTags;
    }
}
