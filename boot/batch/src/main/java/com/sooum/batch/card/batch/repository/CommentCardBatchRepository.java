package com.sooum.batch.card.batch.repository;

import com.sooum.data.card.entity.CommentCard;
import com.sooum.data.card.repository.CommentCardRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CommentCardBatchRepository extends CommentCardRepository {

    @Query("select cc from CommentCard cc where cc.masterCard in :commentCardPk")
    List<CommentCard> findCommentCardsForDeletion(@Param("commentCardPk")Long commentCardPk);
}
