package com.sooum.core.domain.card.service;

import com.sooum.core.domain.card.entity.Card;
import com.sooum.core.domain.card.entity.CommentCard;
import com.sooum.core.domain.card.entity.FeedCard;
import com.sooum.core.domain.card.entity.FeedLike;
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
        //댓글 카드가 있나? -> 없으면 완전 삭제 , 있으면 삭제 되었다고 변경
        CommentCard commentCard = commentCardService.findCommentCard(commentCardPk);
        if (!commentCardService.hasCommentCard(commentCard.getPk())) { //자식 카드가 없을 때

            if (hasParentCard(commentCard) && hasSingleChildCard(commentCard)) { //부모 카드가 있으며, 부모는 자식이 나뿐일 때

                commentCardService.deleteCommentCard(commentCardPk);
                deleteParentCard(commentCard);
            }

            commentCardService.deleteCommentCard(commentCardPk);
        }else{
            commentCard.changeDeleteStatus();
        }
    }

    private void deleteParentCard(Card card) {
        if (card instanceof CommentCard commentCard) {
            commentCardService.deleteCommentCard(commentCard.getParentCardPk());
        }

        if (card instanceof FeedCard feedCard) {
            feedCardService.deleteFeedCard(feedCard.getPk());
        }
    }

    @Transactional
    public void deleteFeedCard(Long feedCardPk) {
        if (commentCardService.hasCommentCard(feedCardPk)) {
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

    private boolean hasSingleChildCard(Card card) {
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

    private static boolean hasParentCard(CommentCard childCard) {
        return childCard.getParentCardPk() != null;
    }



    public static boolean isWrittenCommentCard(List<CommentCard> commentCardList, Long memberPk) {
        return commentCardList.stream().anyMatch(commentCard -> commentCard.getWriter().getPk().equals(memberPk));
    }

    public static boolean isLiked(FeedCard feed, List<FeedLike> feedLikes) {
        return feedLikes.stream().anyMatch(feedLike -> feedLike.getTargetCard().getPk().equals(feed.getPk()));
    }

    public static int countLikes(FeedCard feed, List<FeedLike> feedLikes) {
        return (int) feedLikes.stream().filter(feedLike -> feedLike.getTargetCard().getPk().equals(feed.getPk())).count();
    }

    public static int countComments(FeedCard feed, List<CommentCard> comments) {
        return (int) comments.stream().filter(comment -> comment.getParentCardPk().equals(feed.getPk())).count();
    }
}
