package com.sooum.core.domain.tag.repository;

import com.sooum.core.domain.tag.entity.FeedTag;
import com.sooum.core.domain.card.entity.FeedCard;
import com.sooum.core.domain.tag.entity.FeedTag;
import com.sooum.core.domain.tag.entity.Tag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface FeedTagRepository extends JpaRepository<FeedTag, Long> {

    @Query("select ft from FeedTag ft where ft.feedCard.pk = :cardPk")
    List<FeedTag> findAllByFeedCardPk(@Param("cardPk") Long cardPk);

    @Query("SELECT ft.tag FROM FeedTag ft WHERE ft.feedCard = :feedCard")
    List<Tag> findTagsByFeedCard(@Param("feedCard") FeedCard feedCard);
}