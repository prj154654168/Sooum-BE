package com.sooum.core.domain.tag.repository;

import com.sooum.core.domain.tag.entity.CommentTag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CommentTagRepository extends JpaRepository<CommentTag, Long> {

    @Query("select ct from CommentTag ct where ct.commentCard.pk = :cardPk")
    List<CommentTag> findAllByCommentCardPk(@Param("cardPk") Long cardPk);
}
