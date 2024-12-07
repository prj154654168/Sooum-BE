package com.sooum.data.card.repository;

import com.sooum.data.card.entity.FeedCard;
import com.sooum.data.member.entity.Member;
import org.locationtech.jts.geom.Point;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface FeedCardRepository extends JpaRepository<FeedCard, Long> {
    @Query("select f " +
            "from FeedCard f " +
            "where (:lastId is null or f.pk < :lastId) " +
                "and f.writer.pk not in :blockMemberPkList " +
                "and (f.isStory=false or (f.isStory = true and f.createdAt > :dateTime)) " +
                "and f.isDeleted = false " +
                "and f.isPublic = true " +
            "order by f.pk desc")
    List<FeedCard> findByNextPage(@Param("lastId") Long lastId,
                                  @Param("blockMemberPkList") List<Long> blockMemberPkList,
                                  @Param("dateTime") LocalDateTime dateTime,
                                  Pageable pageable);

    @Query("select f from FeedCard f " +
            "where (:lastPk is null or f.pk < :lastPk) " +
            "and f.writer.pk not in :blockMemberPks " +
            "and (St_Distance(f.location, :userLocation) <= :maxDist " +
                "and St_Distance(f.location, :userLocation) >= :minDist " +
                "and (f.isStory = false or (f.isStory = true and f.createdAt > :dateTime))) " +
                "and f.isDeleted = false " +
                "and f.isPublic = true " +
            "order by f.pk desc")
    List<FeedCard> findNextByDistance(@Param("lastPk") Long lastPk, @Param("userLocation") Point userLocation,
                                      @Param("minDist") double minDist, @Param("maxDist") double maxDist,
                                      @Param("blockMemberPks") List<Long> blockMemberPks,
                                      @Param("dateTime") LocalDateTime dateTime,
                                      Pageable pageable);

    @Query("select count(f) from FeedCard f where f.writer = :cardOwnerMember")
    Long findFeedCardCnt(@Param("cardOwnerMember") Member cardOwnerMember);


    @Query("select fc from FeedCard fc " +
            "where (fc.isStory=false or (fc.isStory = true and fc.createdAt > :dateTime)) " +
                "and fc.writer.pk = :memberPk " +
                "and (:lastPk is null or fc.pk < :lastPk) " +
            "order by fc.pk desc ")
    List<FeedCard> findMyFeedCards(@Param("memberPk") Long memberPk,
                                   @Param("lastPk") Long lastPk,
                                   @Param("dateTime") LocalDateTime dateTime,
                                   PageRequest pageRequest);

    @Query("select fc from FeedCard fc " +
            "where fc.isPublic = true " +
                "and (fc.isStory=false or (fc.isStory = true and fc.createdAt > :dateTime)) " +
                "and fc.writer.pk = :memberPk " +
                "and (:lastPk is null or fc.pk < :lastPk) " +
            "order by fc.pk desc ")
    List<FeedCard> findMemberFeedCards(@Param("memberPk") Long memberPk,
                                       @Param("lastPk") Long lastPk,
                                       @Param("dateTime") LocalDateTime dateTime,
                                       PageRequest pageRequest);

    @Modifying
    @Query("delete from FeedCard fc WHERE fc.writer.pk = :memberPk")
    void deleteFeedCardByMemberPk(@Param("memberPk") Long memberPk);

    @Query("select fc from FeedCard fc " +
            "where fc in :feedCardList")
    List<FeedCard> findFeedCardList(@Param("feedCardList") List<FeedCard> feedCardList);
}
