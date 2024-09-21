package com.sooum.core.domain.card.repository;

import com.sooum.core.domain.card.entity.CommentCard;
import com.sooum.core.domain.card.entity.FeedCard;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CommentCardRepository extends JpaRepository<CommentCard, Long> {
    List<CommentCard> findByMasterCardIn(List<FeedCard> masterCards);
    @Query("select cc from CommentCard cc where cc.masterCard in :targetList")
    List<CommentCard> findByTargetList(@Param("targetList") List<FeedCard> targetList);
}
