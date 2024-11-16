package com.sooum.batch.card.batch.repository;

import com.sooum.data.card.entity.CommentCard;
import com.sooum.data.card.entity.CommentLike;
import com.sooum.data.card.repository.CommentLikeRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CommentLikeBatchRepository extends CommentLikeRepository {
    @Query("select cl from CommentLike cl where cl.targetCard in :targetCardList")
    List<CommentLike> findCommentLikesForDeletion(@Param("targetCardList")List<CommentCard> targetCardList);
}
