package com.sooum.batch.card.batch.repository;

import com.sooum.data.card.repository.PopularFeedRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface PopularCardBatchRepository extends PopularFeedRepository {
    @Modifying
    @Query("delete from PopularFeed pf " +
            "where pf.popularityType = 'LIKE' " +
                "and pf.createdAt < (current_timestamp - 5 minute) " +
                "and pf.version = :version")
    void deletePreviousPopularFeedsByLike(@Param("version") int version);

    @Modifying
    @Query("delete from PopularFeed pf " +
            "where pf.popularityType = 'COMMENT' " +
                "and pf.createdAt < (current_timestamp - 5 minute) " +
                "and pf.version = :version")
    void deletePreviousPopularFeedsByComment(@Param("version") int version);

    @Query("select max(pf.version) from PopularFeed pf where pf.popularityType = 'LIKE'")
    Optional<Integer> findLatestVersionByLike();

    @Query("select max(pf.version) from PopularFeed pf where pf.popularityType = 'COMMENT'")
    Optional<Integer> findLatestVersionByComment();
}
