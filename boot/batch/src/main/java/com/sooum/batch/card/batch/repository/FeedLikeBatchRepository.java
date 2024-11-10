package com.sooum.batch.card.batch.repository;

import com.sooum.data.card.entity.FeedCard;
import com.sooum.data.card.entity.FeedLike;
import com.sooum.data.card.repository.FeedLikeRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface FeedLikeBatchRepository extends FeedLikeRepository {
    @Query("select fl from FeedLike fl where fl.targetCard = :targetCard")
    List<FeedLike> findFeedLikesForDeletion(@Param("targetCard") FeedCard targetCard);
}
