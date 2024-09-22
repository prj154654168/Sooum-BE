package com.sooum.core.domain.card.repository;

import com.sooum.core.domain.card.entity.PopularFeed;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface PopularFeedRepository extends JpaRepository<PopularFeed, Long> {
    @Query("select pc from PopularFeed pc join fetch pc.popularCard " +
            "where pc.popularCard.isStory = false or (pc.popularCard.isStory = true and pc.popularCard.createdAt > :storyExpiredTime) " +
            "order by pc.pk desc")
    List<PopularFeed> findPopularFeeds(@Param("storyExpiredTime") LocalDateTime storyExpiredTime, Pageable pageable);

    @Modifying
    @Query("delete from PopularFeed pf where pf.popularCard.pk = :popularCardPk")
    void deletePopularCard(@Param("popularCardPk") Long popularCardPk);
}
