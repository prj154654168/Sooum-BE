package com.sooum.core.domain.tag.repository;

import com.sooum.core.domain.tag.entity.FeedTag;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FeedTagRepository extends JpaRepository<FeedTag, Long> {

    List<FeedTag> findAllByFeedCard_Pk(Long cardPk);
}
