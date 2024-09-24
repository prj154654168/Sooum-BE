package com.sooum.core.domain.card.service;

import com.sooum.core.domain.card.entity.*;
import com.sooum.core.domain.card.entity.parenttype.CardType;
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

    @Transactional
    public void deleteCommentCard(Long commentCardPk) {

        CommentCard commentCard = commentCardService.findCommentCard(commentCardPk);
        commentCardService.deleteOnlyDeletedChild(commentCard.getPk());
        if (!commentCardService.hasChildCard(commentCard.getPk())) {

            while (hasParentCard(commentCard) && isParentInDeletedState(commentCard) && isOnlyChild(commentCard)) {

                if (isParentCommentType(commentCard)) {
                    CommentCard parent = commentCardService.findCommentCard(commentCard.getParentCardPk());
                    commentCardService.deleteCommentCard(commentCard.getPk());
                    commentCard = parent;
                    continue;
                }
                if (isParentFeedType(commentCard)) {
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
        }else {
            commentCard.changeDeleteStatus();
            while (hasParentCard(commentCard) && isParentInDeletedState(commentCard) && isOnlyChild(commentCard)) {

                if (isParentCommentType(commentCard)) {
                    CommentCard parent = commentCardService.findCommentCard(commentCard.getParentCardPk());
                    commentCardService.deleteCommentCard(parent.getPk());
                    commentCard = parent;
                    continue;
                }
                if (isParentFeedType(commentCard)) {
                    FeedCard parent = feedCardService.findFeedCard(commentCard.getParentCardPk());
                    if (hasOnlyOneChild(parent)) {
                        feedCardService.deleteFeedCard(parent.getPk());
                    }
                    break;
                }
            }

        }
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
    public void deleteFeedCard(Long feedCardPk) {
        List<CommentCard> childCommentCardList = commentCardService.findChildCommentCardList(feedCardPk);
        if (childCommentCardList.size() == 1 && childCommentCardList.get(0).isDeleted()) {
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
