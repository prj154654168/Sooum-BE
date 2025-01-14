package com.sooum.api.card.dto;

import com.sooum.data.card.entity.CommentCard;
import com.sooum.data.card.entity.font.Font;
import com.sooum.data.card.entity.fontsize.FontSize;
import com.sooum.data.card.entity.imgtype.CardImgType;
import com.sooum.data.card.entity.parenttype.CardType;
import com.sooum.data.member.entity.Member;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;

import java.util.List;

@Getter
@NoArgsConstructor
public class CreateCommentDto extends CreateCardDto{
    private List<String> commentTags;

    public CreateCommentDto(String content, boolean isDistanceShared, double latitude, double longitude, Font font, CardImgType imgType, String imgName, List<String> commentTags) {
        super(content, isDistanceShared, latitude, longitude, font, imgType, imgName);
        this.commentTags = commentTags;
    }

    public CommentCard of(CardType parentCardType, Long parentCardPk, Long masterCardPk, Member member, String writerIp) {
        return CommentCard.builder()
                .parentCardType(parentCardType)
                .masterCard(masterCardPk)
                .parentCardPk(parentCardPk)
                .content(this.getContent())
                .font(this.getFont())
                .fontSize(FontSize.NONE)
                .imgType(this.getImgType())
                .imgName(this.getImgName())
                .location(this.isDistanceShared()
                        ? new GeometryFactory().createPoint(new Coordinate(this.getLongitude(), this.getLatitude()))
                        : null)
                .writer(member)
                .writerIp(writerIp)
                .build();
    }

    @Override
    public List<String> getTags() {
        return this.commentTags;
    }
}
