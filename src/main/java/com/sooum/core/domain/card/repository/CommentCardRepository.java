package com.sooum.core.domain.card.repository;

import com.sooum.core.domain.card.entity.CommentCard;
import com.sooum.core.domain.card.entity.FeedCard;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CommentCardRepository extends JpaRepository<CommentCard, Long> {
    List<CommentCard> findByMasterCardIn(List<FeedCard> masterCards);
}
