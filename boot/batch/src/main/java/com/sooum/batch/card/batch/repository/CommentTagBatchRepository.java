package com.sooum.batch.card.batch.repository;

import com.sooum.data.card.entity.CommentCard;
import com.sooum.data.tag.entity.CommentTag;
import com.sooum.data.tag.repository.CommentTagRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CommentTagBatchRepository extends CommentTagRepository {
    @Query("select ct from CommentTag ct where ct.commentCard in :commentCards")
    List<CommentTag> findCommentTagsForDeletion(@Param("commentCards") List<CommentCard> commentCards);
}
