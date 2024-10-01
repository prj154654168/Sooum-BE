package com.sooum.core.domain.tag.repository;

import com.sooum.core.domain.tag.entity.FeedTag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface FeedTagRepository extends JpaRepository<FeedTag, Long> {

    @Query("select ft from FeedTag ft where ft.feedCard.pk = :cardPk")
    List<FeedTag> findAllByFeedCard_Pk(Long cardPk);
}
