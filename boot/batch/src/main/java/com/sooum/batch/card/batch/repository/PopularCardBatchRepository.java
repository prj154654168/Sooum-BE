package com.sooum.batch.card.batch.repository;

import com.sooum.data.card.repository.PopularFeedRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

public interface PopularCardBatchRepository extends PopularFeedRepository {
    @Modifying
    @Transactional
    @Query("delete from PopularFeed pf " +
            "where pf.popularityType = 'LIKE' " +
                "and pf.version = :version")
    void deletePreviousPopularFeedsByLike(@Param("version") int version);

    @Modifying
    @Transactional
    @Query("delete from PopularFeed pf " +
            "where pf.popularityType = 'COMMENT' " +
                "and pf.version = :version")
    void deletePreviousPopularFeedsByComment(@Param("version") int version);
}
