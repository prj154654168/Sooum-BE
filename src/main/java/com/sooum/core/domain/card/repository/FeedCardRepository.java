package com.sooum.core.domain.card.repository;

import com.sooum.core.domain.card.entity.FeedCard;
import org.locationtech.jts.geom.Point;
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

    @Query("SELECT f FROM FeedCard f WHERE St_Distance(f.location, :userLocation) <= :maxDist "
            + "AND St_Distance(f.location, :userLocation) >= :minDist"
            + " ORDER BY f.pk DESC")
    List<FeedCard> findFirstByDistance (@Param("userLocation") Point userLocation, @Param("minDist") double minDist,
                                        @Param("maxDist") double maxDist, Pageable pageable);

    @Query("SELECT f FROM FeedCard f WHERE St_Distance(f.location, :userLocation) <= :maxDist"
            + " AND St_Distance(f.location, :userLocation) >= :minDist "
            + " AND f.pk < :lastId ORDER BY f.pk DESC")
    List<FeedCard> findNextByDistance(@Param("userLocation") Point userLocation, @Param("lastId") Long lastId,
                                      @Param("minDist") double minDist, @Param("maxDist") double maxDist, Pageable pageable);
}
