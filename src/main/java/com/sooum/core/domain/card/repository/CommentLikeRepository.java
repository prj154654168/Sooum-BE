package com.sooum.core.domain.card.repository;

import com.sooum.core.domain.card.entity.CommentLike;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface CommentLikeRepository extends JpaRepository<CommentLike, Long> {
    @Query("select cl from CommentLike cl where cl.targetCard.pk = :likedCardPk and cl.likedMember.pk = :likedMemberPk")
    Optional<CommentLike> findCommentLiked(@Param("likedCardPk") Long likedCardPk, @Param("likedMemberPk") Long likedMemberPk);
    boolean existsByTargetCardPkAndLikedMemberPk(Long targetCardPk, Long likedMemberPk);
}
