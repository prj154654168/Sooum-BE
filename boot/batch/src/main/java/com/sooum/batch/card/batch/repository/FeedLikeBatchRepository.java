package com.sooum.batch.card.batch.repository;

import com.sooum.data.card.entity.FeedCard;
import com.sooum.data.card.entity.FeedLike;
import com.sooum.data.card.repository.FeedLikeRepository;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface FeedLikeBatchRepository extends FeedLikeRepository {
    @Query("select fl from FeedLike fl where fl.targetCard = :targetCard")
    List<FeedLike> findFeedLikesForDeletion(@Param("targetCard") FeedCard targetCard);

    @Query("select fl.targetCard from FeedLike fl inner join fl.targetCard f " +
            "where f.isStory = false " +
                "and f.isPublic = true " +
                "and fl.targetCard.createdAt >= :dateTime " +
            "group by fl.targetCard.pk " +
                "having count(fl.likedMember.pk) >= 2 " +
            "order by count(fl.targetCard.pk) desc")
    List<FeedCard> findPopularCondFeedCards(@Param("dateTime") LocalDateTime dateTime, Pageable pageable);
}
