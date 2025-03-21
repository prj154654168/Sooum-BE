package com.sooum.data.tag.repository;

import com.sooum.data.card.entity.CommentCard;
import com.sooum.data.tag.entity.CommentTag;
import com.sooum.data.tag.entity.Tag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface CommentTagRepository extends JpaRepository<CommentTag, Long> {

    @Query("select ct from CommentTag ct join fetch ct.tag where ct.commentCard.pk = :cardPk")
    List<CommentTag> findAllByCommentCardPk(@Param("cardPk") Long cardPk);

    @Query("select ct from CommentTag ct where ct.commentCard in :cards")
    List<CommentTag> findAllByCommentCards(@Param("cards") List<CommentCard> commentCards);

    @Query("SELECT ct.tag FROM CommentTag ct WHERE ct.commentCard = :commentCard")
    List<Tag> findTagsByCommentCard(@Param("commentCard") CommentCard commentCard);

    @Modifying
    @Transactional
    @Query("delete from CommentTag ct where ct.commentCard.writer.pk = :memberPk")
    void deleteCommentTag(@Param("memberPk") Long memberPk);
}
