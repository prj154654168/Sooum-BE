package com.sooum.core.domain.card.repository;

import com.sooum.core.domain.card.entity.CommentCard;
import com.sooum.core.domain.card.entity.FeedCard;
import com.sooum.core.domain.card.entity.parenttype.CardType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface CommentCardRepository extends JpaRepository<CommentCard, Long> {
    List<CommentCard> findByMasterCardIn(List<FeedCard> masterCards);

    @Query("select cc from CommentCard cc where cc.masterCard in :targetList")
    List<CommentCard> findByTargetList(@Param("targetList") List<FeedCard> targetList);

    @Query("select cc from CommentCard cc where cc.parentCardPk = :parentCardPk")
    List<CommentCard> findChildCards(@Param("parentCardPk") Long parentCardPk);

    @Query("select cc from CommentCard cc where cc.parentCardPk in :parentCardPk")
    List<CommentCard> findChildCards(@Param("parentCardPk") List<Long> parentCardPk);

    @Query("select cc from CommentCard cc where cc.pk = :commentCardPk")
    Optional<CommentCard> findCommentCard(@Param("commentCardPk") Long commentCardPk);

    @Query("select count(cc) from CommentCard cc where cc.parentCardPk = :parentCardPk and cc.parentCardType = :parentCardType")
    Integer countCommentsByParentCard(@Param("parentCardPk") Long parentCardPk, @Param("parentCardType") CardType parentCardType);

    @Query("select cc from CommentCard cc where cc.parentCardType = :parentCardType and cc.isDeleted = false" +
            " and cc.parentCardPk = :parentCardPk" +
            " and (cc.isStory = false or (cc.isStory = true and cc.createdAt > (current_timestamp - 1 day)))" +
            " order by cc.pk desc ")
    List<CommentCard> findCommentsInfo(@Param("parentCardPk") Long parentCardPk, @Param("parentCardType")CardType parentCardType);

    @Query("select cc from CommentCard cc where cc.parentCardType = :parentCardType and cc.isDeleted = false" +
            " and cc.parentCardPk = :parentCardPk" +
            " and (cc.isStory = false or (cc.isStory = true and cc.createdAt > (current_timestamp - 1 day)))" +
            " and cc.pk < :lastPk" +
            " order by cc.pk desc ")
    List<CommentCard> findCommentsInfoByLastPk(@Param("parentCardPk") Long parentCardPk,
                                               @Param("parentCardType")CardType parentCardType,
                                               @Param("lastPk") Long lastPk);
}
