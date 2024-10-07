package com.sooum.core.domain.img.entity;

import com.sooum.core.domain.card.entity.Card;
import com.sooum.core.domain.card.entity.CommentCard;
import com.sooum.core.domain.card.entity.FeedCard;
import com.sooum.core.domain.common.entity.BaseEntity;
import com.sooum.core.global.exceptionmessage.ExceptionMessage;
import io.hypersistence.utils.hibernate.id.Tsid;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UserUploadPic extends BaseEntity {
    @Id @Tsid
    private Long pk;

    @NotNull
    @Column(name = "IMG_NAME")
    private String imgName;

    @JoinColumn(name = "FEED_CARD")
    @ManyToOne(fetch = FetchType.LAZY)
    private FeedCard feedCard;

    @JoinColumn(name = "COMMENT_CARD")
    @ManyToOne(fetch = FetchType.LAZY)
    private CommentCard commentCard;

    @Builder
    public UserUploadPic(String imgName, Card card) {
        this.imgName = imgName;
        if (card instanceof FeedCard feedCard) {
            this.feedCard = feedCard;
        } else if (card instanceof CommentCard commentCard) {
            this.commentCard = commentCard;
        } else {
            throw new IllegalArgumentException(ExceptionMessage.UNHANDLED_OBJECT.getMessage());
        }
    }
}
