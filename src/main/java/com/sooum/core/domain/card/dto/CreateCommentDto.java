package com.sooum.core.domain.card.dto;

import com.sooum.core.domain.card.entity.CommentCard;
import com.sooum.core.domain.card.entity.font.Font;
import com.sooum.core.domain.card.entity.fontsize.FontSize;
import com.sooum.core.domain.card.entity.imgtype.ImgType;
import com.sooum.core.domain.card.entity.parenttype.CardType;
import com.sooum.core.domain.member.entity.Member;
import lombok.Getter;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;

import java.util.List;

@Getter
public class CreateCommentDto extends CreateCardDto{
    private List<String> commentTags;

    public CreateCommentDto(String content, boolean isDistanceShared, double latitude, double longitude, Font font, ImgType imgType, String imgName, List<String> commentTags) {
        super(content, isDistanceShared, latitude, longitude, font, imgType, imgName);
        this.commentTags = commentTags;
    }

    public CommentCard of(CardType parentCardType, Long parentCardPk, Long masterCardPk, Member member) {
        return CommentCard.builder()
                .parentCardType(parentCardType)
                .masterCard(masterCardPk)
                .parentCardPk(parentCardPk)
                .content(this.getContent())
                .font(this.getFont())
                .fontSize(FontSize.MEDIUM)
                .imgType(this.getImgType())
                .imgName(this.getImgName())
                .location(this.isDistanceShared()
                        ? new GeometryFactory().createPoint(new Coordinate(this.getLongitude(), this.getLatitude()))
                        : null)
                .writer(member).build();
    }

    @Override
    public List<String> getTags() {
        return this.commentTags;
    }
}
