package com.sooum.core.domain.card.repository;

import com.sooum.core.domain.card.entity.FeedCard;
import com.sooum.core.domain.card.entity.FeedLike;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FeedLikeRepository extends JpaRepository<FeedLike, Long> {
    List<FeedLike> findByTargetCardIn(List<FeedCard> targetCards);
}
