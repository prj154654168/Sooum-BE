package com.sooum.core.domain.card.repository;

import com.sooum.core.domain.card.entity.FeedCard;
import com.sooum.core.domain.card.entity.FeedLike;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface FeedLikeRepository extends JpaRepository<FeedLike, Long> {
    @Query("select fl from FeedLike fl where fl.targetCard in :targetList")
    List<FeedLike> findByTargetList(@Param("targetList") List<FeedCard> targetList);

    @Modifying
    @Query("delete from FeedLike fl where fl.targetCard.pk = :feedCardPk")
    void deleteAllFeedLikes(@Param("feedCardPk") Long feedCardPk);
}
