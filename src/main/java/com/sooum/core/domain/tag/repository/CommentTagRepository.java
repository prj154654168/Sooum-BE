package com.sooum.core.domain.tag.repository;

import com.sooum.core.domain.card.entity.CommentCard;
import com.sooum.core.domain.tag.entity.CommentTag;
import com.sooum.core.domain.tag.entity.Tag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CommentTagRepository extends JpaRepository<CommentTag, Long> {
    @Query("SELECT ct.tag FROM CommentTag ct WHERE ct.commentCard = :commentCard")
    List<Tag> findTagsByCommentCard(@Param("commentCard") CommentCard commentCard);
}
