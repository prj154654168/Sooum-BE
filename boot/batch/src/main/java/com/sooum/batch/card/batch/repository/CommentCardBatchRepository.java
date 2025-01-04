package com.sooum.batch.card.batch.repository;

import com.sooum.data.card.entity.CommentCard;
import com.sooum.data.card.entity.FeedCard;
import com.sooum.data.card.repository.CommentCardRepository;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CommentCardBatchRepository extends CommentCardRepository {

    @Query("select cc from CommentCard cc where cc.masterCard in :feedCardPk")
    List<CommentCard> findCommentCardsForDeletion(@Param("feedCardPk") Long feedCardPk);

    @Query("select fc from CommentCard cc inner join FeedCard fc on fc.pk = cc.masterCard " +
            "where fc.isStory = false " +
                "and fc.isPublic = true " +
                "and fc.createdAt >= (current_timestamp - 2 day) " +
            "group by cc.masterCard " +
                "having count(distinct cc.writer) >= 2" +
            "order by count(fc.pk) desc ")
    List<FeedCard> findPopularCondFeedCards(Pageable pageable);
}
