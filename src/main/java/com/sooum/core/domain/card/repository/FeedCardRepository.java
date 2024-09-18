package com.sooum.core.domain.card.repository;

import com.sooum.core.domain.card.entity.FeedCard;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface FeedCardRepository extends JpaRepository<FeedCard, Long> {
    @Query("select f from FeedCard f where f.pk < :lastId order by f.pk desc")
    List<FeedCard> findByNextPage(@Param("lastId") Long lastId, Pageable pageable);

    @Query("select f from FeedCard f order by f.pk desc")
    List<FeedCard> findFirstPage(Pageable pageable);
}
