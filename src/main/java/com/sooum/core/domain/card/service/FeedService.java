package com.sooum.core.domain.card.service;

import com.sooum.core.domain.card.entity.*;
import com.sooum.core.domain.card.entity.parenttype.CardType;
import com.sooum.core.global.exceptionmessage.ExceptionMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class FeedService {

    private final CommentCardService commentCardService;
    private final FeedCardService feedCardService;
    private final PopularFeedService popularFeedService;
    private final FeedLikeService feedLikeService;

    public Card findParentCard(CommentCard commentCard) {
        if(commentCard.getParentCardType().equals(CardType.COMMENT_CARD)){
            return commentCardService.findByPk(commentCard.getParentCardPk());
        }
        if (commentCard.getParentCardType().equals(CardType.FEED_CARD)){
            return feedCardService.findByPk(commentCard.getParentCardPk());
        }
        throw new IllegalArgumentException(ExceptionMessage.UNHANDLED_TYPE.getMessage());
    }

    @Transactional
    public void deleteCommentCard(Long commentCardPk) {
        if (isNotCommentCardOwner(commentCardPk, commentCardPk)) return;

        CommentCard commentCard = commentCardService.findCommentCard(commentCardPk);
        commentCardService.deleteOnlyDeletedChild(commentCard.getPk());
        if (commentCardService.hasChildCard(commentCard.getPk())) {
            commentCard.changeDeleteStatus();
            while (isParentDeletable(commentCard)) {
                if (isParentCommentType(commentCard)) {
                    CommentCard parent = commentCardService.findCommentCard(commentCard.getParentCardPk());
                    commentCardService.deleteCommentCard(parent.getPk());
                    commentCard = parent;
                }
                else if (isParentFeedType(commentCard)) {
                    FeedCard parent = feedCardService.findFeedCard(commentCard.getParentCardPk());
                    if (hasOnlyOneChild(parent)) {
                        feedCardService.deleteFeedCard(parent.getPk());
                    }
                    break;
                }
            }
        }else {
            while (isParentDeletable(commentCard)) {
                if (isParentCommentType(commentCard)) {
                    CommentCard parent = commentCardService.findCommentCard(commentCard.getParentCardPk());
                    commentCardService.deleteCommentCard(commentCard.getPk());
                    commentCard = parent;
                }
                else if (isParentFeedType(commentCard)) {
                    FeedCard parent = feedCardService.findFeedCard(commentCard.getParentCardPk());
                    if (hasOnlyOneChild(parent)) {
                        feedCardService.deleteFeedCard(parent.getPk());
                    }
                    commentCardService.deleteCommentCard(commentCardPk);
                    break;
                }
            }
            if (hasParentCard(commentCard) && isOnlyChild(commentCard)) {
                commentCardService.deleteCommentCard(commentCard.getPk());
            }
        }
    }

    private boolean isNotCommentCardOwner(Long commentCardPk, Long writerPk) {
        return !commentCardService.findCommentCard(commentCardPk).getWriter().getPk().equals(writerPk);
    }

    private boolean isParentDeletable(CommentCard commentCard) {
        return hasParentCard(commentCard) && isParentInDeletedState(commentCard) && isOnlyChild(commentCard);
    }

    private boolean isParentInDeletedState(Card card) {
        if (card instanceof CommentCard commentCard) {
            if (commentCard.getParentCardType().equals(CardType.FEED_CARD) && feedCardService.isExistFeedCard(commentCard.getParentCardPk())) {
                return feedCardService.findFeedCard(commentCard.getParentCardPk()).isDeleted();

            }
            if (commentCard.getParentCardType().equals(CardType.COMMENT_CARD) && commentCardService.isExistCommentCard(commentCard.getParentCardPk())) {
                return commentCardService.findCommentCard(commentCard.getParentCardPk()).isDeleted();
            }
        }
        return false;
    }

    private static boolean isParentFeedType(CommentCard commentCard) {
        return commentCard.getParentCardType().equals(CardType.FEED_CARD);
    }

    private static boolean isParentCommentType(CommentCard commentCard) {
        return commentCard.getParentCardType().equals(CardType.COMMENT_CARD);
    }

    @Transactional
    public void deleteFeedCard(Long feedCardPk, Long writerPk) {
        if (isNotFeedCardOwner(feedCardPk, writerPk)) return;

        List<CommentCard> childCommentCardList = commentCardService.findChildCommentCardList(feedCardPk);
        if (isCommentDeletable(childCommentCardList)) {
            feedCardService.deleteFeedCard(feedCardPk);
            return;
        }
        if (commentCardService.hasChildCard(feedCardPk)) {
            FeedCard feedCard = feedCardService.findFeedCard(feedCardPk);
            feedCard.changeDeleteStatus();
        }else{
            deleteFeedCardAndAssociations(feedCardPk);
            feedCardService.deleteFeedCard(feedCardPk);
        }
    }

    private boolean isNotFeedCardOwner(Long feedCardPk, Long writerPk) {
        return !feedCardService.findFeedCard(feedCardPk).getWriter().getPk().equals(writerPk);
    }

    private static boolean isCommentDeletable(List<CommentCard> childCommentCardList) {
        return childCommentCardList.size() == 1 && childCommentCardList.get(0).isDeleted();
    }

    private void deleteFeedCardAndAssociations(Long feedCardPk) {
        popularFeedService.deletePopularCard(feedCardPk);
        feedLikeService.deleteAllFeedLikes(feedCardPk);
        //todo 피드 신고 추가되면 같이 삭제되도록 로직 추가
        //todo tag 추가되면 같이 삭제되도록 로직 추가
        //todo userimg 추가되면 같이 삭제되도록 로직 추가
    }

    private boolean hasOnlyOneChild(FeedCard parent) {
        return commentCardService.findChildCommentCardList(parent.getPk()).size() == 1;
    }

    private boolean isOnlyChild(Card card) {
        if (card instanceof CommentCard commentCard) {
            return commentCardService.findChildCommentCardList(commentCard.getParentCardPk())
                    .size() == 1;
        }

        if (card instanceof FeedCard feedCard) {
            return commentCardService.findChildCommentCardList(feedCard.getPk())
                    .size() == 1;
        }
        return false;
    }

    private static boolean hasParentCard(CommentCard commentCard) {
        return commentCard.getParentCardPk() != null;
    }

    public static boolean isWrittenCommentCard(List<CommentCard> commentCardList, Long memberPk) {
        return commentCardList.stream().anyMatch(commentCard -> commentCard.getWriter().getPk().equals(memberPk));
    }

    public static boolean isLiked(FeedCard feed, List<FeedLike> feedLikes) {
        return feedLikes.stream().anyMatch(feedLike -> feedLike.getTargetCard().getPk().equals(feed.getPk()));
    }

    public static boolean isLiked(CommentCard comment, List<CommentLike> commentLikes) {
        return commentLikes.stream().anyMatch(commentLike -> commentLike.getTargetCard().getPk().equals(comment.getPk()));
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
