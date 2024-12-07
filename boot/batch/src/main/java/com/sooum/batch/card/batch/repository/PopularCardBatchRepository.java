package com.sooum.batch.card.batch.repository;

import com.sooum.data.card.repository.PopularFeedRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface PopularCardBatchRepository extends PopularFeedRepository {
    @Modifying
    @Query("delete from PopularFeed pf " +
            "where pf.popularityType = 'LIKE' " +
                "and pf.version = :version")
    void deletePreviousPopularFeedsByLike(@Param("version") int version);

    @Modifying
    @Query("delete from PopularFeed pf " +
            "where pf.popularityType = 'COMMENT' " +
                "and pf.version = :version")
    void deletePreviousPopularFeedsByComment(@Param("version") int version);
}
