package com.sooum.data.card.entity;

import com.sooum.data.card.entity.font.Font;
import com.sooum.data.card.entity.fontsize.FontSize;
import com.sooum.data.card.entity.imgtype.CardImgType;
import com.sooum.data.member.entity.Member;
import jakarta.persistence.Column;
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
    @Column(name = "IS_STORY")
    private boolean isStory;

    @Column(name = "IS_PUBLIC")
    private boolean isPublic;

    @Builder
    public FeedCard(String content, FontSize fontSize, Font font, Point location, CardImgType imgType, String imgName, Member writer, boolean isStory, boolean isPublic) {
        super(content, fontSize, font, location, imgType, imgName, writer);
        this.isStory = isStory;
        this.isPublic = isPublic;
    }
}