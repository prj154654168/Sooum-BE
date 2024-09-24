package com.sooum.core.domain.card.entity;

import com.sooum.core.domain.card.entity.font.Font;
import com.sooum.core.domain.card.entity.fontsize.FontSize;
import com.sooum.core.domain.card.entity.imgtype.ImgType;
import com.sooum.core.domain.card.entity.parenttype.CardType;
import com.sooum.core.domain.member.entity.Member;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.locationtech.jts.geom.Point;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CommentCard extends Card {

    @Enumerated(value = EnumType.STRING)
    @Column(name = "PARENT_CARD_TYPE")
    private CardType parentCardType;

    @Column(name = "PARENT_CARD_PK")
    private Long parentCardPk;

    @NotNull
    @JoinColumn(name = "MASTER_CARD")
    @ManyToOne(fetch = FetchType.LAZY)
    private FeedCard masterCard;

    @Builder
    public CommentCard(String content, FontSize fontSize, Font font, Point location, ImgType imgType, String imgName, boolean isPublic, Member writer, CardType parentCardType, Long parentCardPk, FeedCard masterCard) {
        super(content, fontSize, font, location, imgType, imgName, isPublic, writer);
        this.parentCardType = parentCardType;
        this.parentCardPk = parentCardPk;
        this.masterCard = masterCard;
    }
}
