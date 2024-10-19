package com.sooum.core.domain.tag.repository;

import com.sooum.core.domain.card.entity.CommentCard;
import com.sooum.core.domain.tag.entity.CommentTag;
import com.sooum.core.domain.tag.entity.Tag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CommentTagRepository extends JpaRepository<CommentTag, Long> {

    @Query("select ct from CommentTag ct join fetch ct.tag where ct.commentCard.pk = :cardPk")
    List<CommentTag> findAllByCommentCardPk(@Param("cardPk") Long cardPk);

    @Query("select ct from CommentTag ct where ct.commentCard in :cards")
    List<CommentTag> findAllByCommentCards(@Param("cards") List<CommentCard> commentCards);

    @Query("SELECT ct.tag FROM CommentTag ct WHERE ct.commentCard = :commentCard")
    List<Tag> findTagsByCommentCard(@Param("commentCard") CommentCard commentCard);
}
