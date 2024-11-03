package com.sooum.data.card.repository;

import com.sooum.data.card.entity.CommentCard;
import com.sooum.data.card.entity.CommentLike;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface CommentLikeRepository extends JpaRepository<CommentLike, Long> {
    @Query("select cl from CommentLike cl where cl.targetCard.pk = :likedCardPk and cl.likedMember.pk = :likedMemberPk")
    Optional<CommentLike> findCommentLiked(@Param("likedCardPk") Long likedCardPk, @Param("likedMemberPk") Long likedMemberPk);
    boolean existsByTargetCardPkAndLikedMemberPk(Long targetCardPk, Long likedMemberPk);
    List<CommentLike> findByTargetCardIn(List<CommentCard> commentCards);

    List<CommentLike> findAllByTargetCard_Pk(Long cardPk);
    @Query("delete from CommentLike cl where cl.targetCard in :cards")
    void deleteByCommentCard(@Param("cards") List<CommentCard> commentCards);

    Integer countByTargetCard_Pk(Long cardPk);

}
