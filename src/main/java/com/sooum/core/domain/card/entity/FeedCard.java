package com.sooum.core.domain.card.entity;

import com.sooum.core.domain.card.entity.font.Font;
import com.sooum.core.domain.card.entity.fontsize.FontSize;
import com.sooum.core.domain.card.entity.imgtype.ImgType;
import com.sooum.core.domain.common.entity.BaseEntity;
import com.sooum.core.domain.member.entity.Member;
import io.hypersistence.utils.hibernate.id.Tsid;
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
public class FeedCard extends BaseEntity {
    @Id @Tsid
    private Long pk;

    @NotNull
    @Column(name = "CONTENT", columnDefinition = "TEXT")
    private String content;

    @NotNull
    @Enumerated(value = EnumType.STRING)
    private FontSize fontSize;
    @NotNull
    @Enumerated(value = EnumType.STRING)
    private Font font;

    @Column(name = "LOCATION", columnDefinition = "GEOMETRY")
    private Point location;

    @NotNull
    @Enumerated(value = EnumType.STRING)
    private ImgType imgType;
    @NotNull
    @Column(name = "IMG_NAME")
    private String imgName;

    @Column(name = "IS_PUBLIC")
    private boolean isPublic;

    @Column(name = "IS_STORY")
    private boolean isStory;

    @NotNull
    @JoinColumn(name = "WRITER")
    @ManyToOne(fetch = FetchType.LAZY)
    private Member writer;

    @Builder
    public FeedCard(String content, FontSize fontSize, Font font, Point location, ImgType imgType, String imgName, boolean isPublic, boolean isStory, Member writer) {
        this.content = content;
        this.fontSize = fontSize;
        this.font = font;
        this.location = location;
        this.imgType = imgType;
        this.imgName = imgName;
        this.isPublic = isPublic;
        this.isStory = isStory;
        this.writer = writer;
    }
}
