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

    @Query("select f from FeedCard f " +
            "where (:lastPk is null or f.pk < :lastPk) " +
            "and f.writer.pk not in :blockMemberPks " +
            "and (St_Distance(f.location, :userLocation) <= :maxDist " +
                "and St_Distance(f.location, :userLocation) >= :minDist " +
                "and (f.isStory = false or (f.isStory = true and f.createdAt > (current_timestamp - 1 day)))) " +
                "and f.isDeleted = false " +
                "and f.isPublic = true " +
            "order by f.pk desc")
    List<FeedCard> findNextByDistance(@Param("lastPk") Long lastPk, @Param("userLocation") Point userLocation,
                                      @Param("minDist") double minDist, @Param("maxDist") double maxDist,
                                      @Param("blockMemberPks") List<Long> blockMemberPks, Pageable pageable);

    @Modifying
    @Query("delete from FeedCard f where f.pk = :cardPk")
    void deleteFeedCard(@Param("cardPk") Long cardPk);

    @Query("select f from FeedCard f where f.pk = :feedCardPk")
    FeedCard findFeedCard(@Param("feedCardPk") Long feedCardPk);

    @Query("select count(f) from FeedCard f where f.writer = :cardOwnerMember")
    Long findFeedCardCnt(@Param("cardOwnerMember") Member cardOwnerMember);

    @Query("select fc.pk from FeedCard fc where fc.writer.pk in :memberPks")
    List<Long> findFeedCardIdsByWriterIn(@Param("memberPks") List<Long> memberPks);

    @Query("select fc from FeedCard fc where fc.writer.pk = :memberPk")
    List<FeedCard> findByMemberPk(@Param("memberPk") Long memberPk, Pageable pageable);

    @Query("select fc from FeedCard fc where fc.isDeleted = false and fc.writer.pk = :memberPk order by fc.pk desc")
    List<FeedCard> findCommentCardsFirstPage(@Param("memberPk") Long memberPk, PageRequest pageRequest);

    @Query("select fc from FeedCard fc where fc.isDeleted = false and fc.writer.pk = :memberPk and fc.pk < :lastPk order by fc.pk desc ")
    List<FeedCard> findCommentCardsNextPage(@Param("memberPk") Long memberPk, @Param("lastPk") Long lastPk, PageRequest pageRequest);

    @Modifying
    @Query("update FeedCard fc set fc.writer = null, fc.isDeleted = true WHERE fc.writer.pk = :memberPk")
    void clearWriterByMemberPk(@Param("memberPk") Long memberPk);
}
