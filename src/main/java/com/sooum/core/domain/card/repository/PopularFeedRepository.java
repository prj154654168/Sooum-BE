package com.sooum.core.domain.card.repository;

import com.sooum.core.domain.card.entity.PopularFeed;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface PopularFeedRepository extends JpaRepository<PopularFeed, Long> {
    @Query("select pc from PopularFeed pc join fetch pc.popularCard order by pc.pk desc")
    List<PopularFeed> findPopularFeeds(Pageable pageable);
}
