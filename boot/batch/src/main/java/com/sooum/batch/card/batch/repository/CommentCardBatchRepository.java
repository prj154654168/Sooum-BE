package com.sooum.batch.card.batch.repository;

import com.sooum.data.card.entity.CommentCard;
import com.sooum.data.card.entity.FeedCard;
import com.sooum.data.card.repository.CommentCardRepository;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface CommentCardBatchRepository extends CommentCardRepository {

    @Query("select cc from CommentCard cc where cc.masterCard in :commentCardPk")
    List<CommentCard> findCommentCardsForDeletion(@Param("commentCardPk")Long commentCardPk);

    @Query("select fc from CommentCard cc inner join FeedCard fc on fc.pk = cc.masterCard " +
            "where fc.isStory = false " +
                "and fc.isPublic = true " +
                "and fc.createdAt >= :dateTime " +
            "group by fc.pk " +
                "having count(cc.pk) >= 2" +
            "order by count(fc.pk) desc ")
    List<FeedCard> findPopularCondFeedCards(@Param("dateTime") LocalDateTime dateTime, Pageable pageable);
}
