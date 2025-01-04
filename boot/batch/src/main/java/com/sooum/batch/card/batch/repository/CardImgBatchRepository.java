package com.sooum.batch.card.batch.repository;

import com.sooum.data.card.entity.CommentCard;
import com.sooum.data.card.entity.FeedCard;
import com.sooum.data.img.entity.CardImg;
import com.sooum.data.img.repository.CardImgRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface CardImgBatchRepository extends CardImgRepository {

    @Modifying
    @Transactional
    @Query("update CardImg ci set ci.feedCard = null where ci.feedCard = :feedCard")
    void updateFeedCardImgNull(@Param("feedCard") FeedCard feedCard);

    @Modifying
    @Transactional
    @Query("update CardImg ci set ci.commentCard = null where ci.commentCard in :commentCards")
    void updateCommentCardImgNull(@Param("commentCards") List<CommentCard> commentCards);
}
