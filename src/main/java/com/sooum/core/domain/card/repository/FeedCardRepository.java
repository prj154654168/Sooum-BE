package com.sooum.core.domain.card.repository;

import com.sooum.core.domain.card.entity.FeedCard;
import com.sooum.core.domain.member.entity.Member;
import org.locationtech.jts.geom.Point;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface FeedCardRepository extends JpaRepository<FeedCard, Long> {
    @Query("select f " +
            "from FeedCard f " +
            "where (:lastId is null or f.pk < :lastId) " +
                "and f.writer.pk not in :blockMemberPkList " +
                "and (f.isStory=false or (f.isStory = true and f.createdAt > (current_timestamp - 1 day))) " +
                "and f.isDeleted = false " +
                "and f.isPublic = true " +
            "order by f.pk desc")
    List<FeedCard> findByNextPage(@Param("lastId") Long lastId, @Param("blockMemberPkList") List<Long> blockMemberPkList, Pageable pageable);

    @Query("SELECT f FROM FeedCard f " +
            "WHERE (St_Distance(f.location, :userLocation) <= :maxDist " +
                "AND St_Distance(f.location, :userLocation) >= :minDist " +
                "AND (f.isStory = false or (f.isStory = true AND f.createdAt > (current_timestamp - 1 day)))) " +
                "AND f.isDeleted = false " +
                "AND f.isPublic = true " +
            "ORDER BY f.pk DESC")
    List<FeedCard> findFirstByDistance (@Param("userLocation") Point userLocation, @Param("minDist") double minDist,
                                        @Param("maxDist") double maxDist, Pageable pageable);

    @Query("SELECT f FROM FeedCard f " +
            "WHERE (St_Distance(f.location, :userLocation) <= :maxDist " +
                "AND St_Distance(f.location, :userLocation) >= :minDist " +
                "AND (f.isStory = false or (f.isStory = true AND f.createdAt > (current_timestamp - 1 day)))) " +
                "AND f.isDeleted = false " +
                "AND f.pk < :lastId " +
                "AND f.isPublic = true " +
            "ORDER BY f.pk DESC")
    List<FeedCard> findNextByDistance(@Param("userLocation") Point userLocation, @Param("lastId") Long lastId,
                                      @Param("minDist") double minDist, @Param("maxDist") double maxDist, Pageable pageable);

    @Modifying
    @Query("delete from FeedCard f where f.pk = :cardPk")
    void deleteFeedCard(@Param("cardPk") Long cardPk);

    @Query("select f from FeedCard f where f.pk = :feedCardPk")
    FeedCard findFeedCard(@Param("feedCardPk") Long feedCardPk);

    @Query("select count(f) from FeedCard f where f.writer = :cardOwnerMember")
    Long findFeedCardCnt(@Param("cardOwnerMember") Member cardOwnerMember);

    @Query("select fc.pk from FeedCard fc where fc.writer.pk in :memberPks")
    List<Long> findFeedCardIdsByWriterIn(@Param("memberPks") List<Long> memberPks);


}
