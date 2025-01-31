package com.sooum.global.util;

import com.sooum.data.card.entity.*;

import java.util.List;
import java.util.Objects;

public abstract class CardUtils {
    public static boolean isWrittenCommentCard(Card card, List<CommentCard> commentCardList, Long memberPk) {
        return commentCardList.stream()
                .filter(commentCard -> commentCard.getParentCardPk().equals(card.getPk()))
                .anyMatch(commentCard -> commentCard.getWriter().getPk().equals(memberPk));
    }

    public static boolean isLiked(FeedCard feed, List<FeedLike> feedLikes, Long memberPk) {
        return feedLikes.stream().anyMatch(feedLike ->
                feedLike.getTargetCard().getPk().equals(feed.getPk()) && feedLike.getLikedMember().getPk().equals(memberPk)
        );
    }

    public static boolean isLiked(CommentCard comment, List<CommentLike> commentLikes, Long memberPk) {
        return commentLikes.stream().anyMatch(commentLike ->
                commentLike.getTargetCard().getPk().equals(comment.getPk())&& commentLike.getLikedMember().getPk().equals(memberPk)
        );
    }

    public static int countLikes(FeedCard feed, List<FeedLike> feedLikes) {
        return (int) feedLikes.stream().filter(feedLike -> feedLike.getTargetCard().getPk().equals(feed.getPk())).count();
    }

    public static int countLikes(CommentCard comment, List<CommentLike> commentLikes) {
        return (int) commentLikes.stream().filter(CommentLike -> CommentLike.getTargetCard().getPk().equals(comment.getPk())).count();
    }

    public static int countComments(FeedCard feed, List<CommentCard> comments) {
        return (int) comments.stream().filter(comment -> comment.getParentCardPk().equals(feed.getPk())).count();
    }

    public static int countComments(CommentCard parentComment, List<CommentCard> comments) {
        return (int) comments.stream().filter(comment -> comment.getParentCardPk().equals(parentComment.getPk())).count();
    }
}
