package com.sooum.core.domain.card.repository;

import com.sooum.core.domain.card.entity.FeedCard;
import com.sooum.core.domain.card.entity.FeedLike;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface FeedLikeRepository extends JpaRepository<FeedLike, Long> {
    @Query("select fl from FeedLike fl where fl.targetCard in :targetList")
    List<FeedLike> findByTargetList(@Param("targetList") List<FeedCard> targetList);
    @Query("select fl from FeedLike fl where fl.targetCard.pk = :likedCardPk and fl.likedMember.pk = :likedMemberPk")
    Optional<FeedLike> findFeedLiked(@Param("likedCardPk") Long likedCardPk, @Param("likedMemberPk") Long likedMemberPk);
    boolean existsByTargetCardPkAndLikedMemberPk(Long targetCardPk, Long likedMemberPk);
}
