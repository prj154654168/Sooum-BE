package com.sooum.batch.card.batch.repository;

import com.sooum.data.card.repository.PopularFeedRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface PopularCardBatchRepository extends PopularFeedRepository {
    @Modifying
    @Query("delete from PopularFeed pf " +
            "where pf.popularityType = 'LIKE' " +
                "and pf.createdAt < (current_timestamp - 5 minute)")
    void deletePreviousPopularFeedsByLike();

    @Modifying
    @Query("delete from PopularFeed pf " +
            "where pf.popularityType = 'COMMENT' " +
            "and pf.createdAt < (current_timestamp - 5 minute)")
    void deletePreviousPopularFeedsByComment();
}
