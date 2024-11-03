//package com.sooum.data.img.entity;
//
//import com.sooum.data.card.entity.Card;
//import com.sooum.data.card.entity.CommentCard;
//import com.sooum.data.card.entity.FeedCard;
//import com.sooum.data.common.entity.BaseEntity;
//import io.hypersistence.utils.hibernate.id.Tsid;
//import jakarta.persistence.*;
//import jakarta.validation.constraints.NotNull;
//import lombok.AccessLevel;
//import lombok.Builder;
//import lombok.Getter;
//import lombok.NoArgsConstructor;
//
//@Getter
//@Entity
//@NoArgsConstructor(access = AccessLevel.PROTECTED)
//public class UserUploadPic extends BaseEntity {
//    @Id @Tsid
//    private Long pk;
//
//    @NotNull
//    @Column(name = "IMG_NAME")
//    private String imgName;
//
//    @JoinColumn(name = "FEED_CARD")
//    @ManyToOne(fetch = FetchType.LAZY)
//    private FeedCard feedCard;
//
//    @JoinColumn(name = "COMMENT_CARD")
//    @ManyToOne(fetch = FetchType.LAZY)
//    private CommentCard commentCard;
//
//    @Builder
//    public UserUploadPic(String imgName, Card card) {
//        this.imgName = imgName;
//        if (card instanceof FeedCard feedCard) {
//            this.feedCard = feedCard;
//        } else if (card instanceof CommentCard commentCard) {
//            this.commentCard = commentCard;
//        } else {
//            throw new IllegalArgumentException();
//        }
//    }
//}
