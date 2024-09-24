package com.sooum.core.domain.card.entity;

import com.sooum.core.domain.card.entity.font.Font;
import com.sooum.core.domain.card.entity.fontsize.FontSize;
import com.sooum.core.domain.card.entity.imgtype.ImgType;
import com.sooum.core.domain.member.entity.Member;
import jakarta.persistence.Entity;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.locationtech.jts.geom.Point;


@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class FeedCard extends Card {

    @Builder
    public FeedCard(String content, FontSize fontSize, Font font, Point location, ImgType imgType, String imgName, boolean isPublic, boolean isStory, Member writer) {
        super(content, fontSize, font, location, imgType, imgName, isPublic, isStory, writer);
    }
}
