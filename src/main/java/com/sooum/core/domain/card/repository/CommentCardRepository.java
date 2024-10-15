package com.sooum.core.domain.card.repository;

import com.sooum.core.domain.card.entity.CommentCard;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface CommentCardRepository extends JpaRepository<CommentCard, Long> {

    List<CommentCard> findAllByMasterCard(Long masterCardPk);

    List<CommentCard> findByMasterCardIn(List<Long> masterCards);

    @Query("select cc from CommentCard cc where cc.masterCard in :targetList")
    List<CommentCard> findByTargetList(@Param("targetList") List<Long> targetPkList);

    @Query("select cc from CommentCard cc where cc.parentCardPk = :parentCardPk")
    List<CommentCard> findChildCards(@Param("parentCardPk") Long parentCardPk);

    @Query("select cc from CommentCard cc where cc.parentCardPk in :parentCardPk")
    List<CommentCard> findChildCards(@Param("parentCardPk") List<Long> parentCardPk);

    @Query("select cc from CommentCard cc where cc.pk = :commentCardPk")
    Optional<CommentCard> findCommentCard(@Param("commentCardPk") Long commentCardPk);

    @Query("select count(cc) from CommentCard cc where cc.isDeleted = false and cc.parentCardPk = :parentCardPk")
    Integer countCommentsByParentCard(@Param("parentCardPk") Long parentCardPk);

    @Query("select cc from CommentCard cc where cc.isDeleted = false and cc.parentCardPk = :parentCardPk" +
            " order by cc.pk desc ")
    List<CommentCard> findCommentsInfo(@Param("parentCardPk") Long parentCardPk,
                                       Pageable pageable);

    @Query("select cc from CommentCard cc where cc.isDeleted = false and cc.parentCardPk = :parentCardPk" +
            " and cc.pk < :lastPk" +
            " order by cc.pk desc ")
    List<CommentCard> findCommentsInfoByLastPk(@Param("parentCardPk") Long parentCardPk,
                                               @Param("lastPk") Long lastPk,
                                               Pageable pageable);

    @Query("select cc from CommentCard cc where cc.isDeleted = false and cc.writer.pk = :memberPk order by cc.pk desc ")
    List<CommentCard> findCommentCardsByMemberPk(@Param("memberPk") Long memberPK);
}
