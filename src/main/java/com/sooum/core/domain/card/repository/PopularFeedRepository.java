package com.sooum.core.domain.card.repository;

import com.sooum.core.domain.card.entity.FeedCard;
import com.sooum.core.domain.card.entity.PopularFeed;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface PopularFeedRepository extends JpaRepository<PopularFeed, Long> {
    @Query("select pc.popularCard from PopularFeed pc " +
            "where (pc.popularCard.isStory = false or (pc.popularCard.isStory = true and pc.popularCard.createdAt > (current_timestamp - 1 day))) and pc.popularCard.isDeleted = false " +
            "order by pc.pk desc")
    List<FeedCard> findPopularFeeds(Pageable pageable);
}
