package com.sooum.core.domain.card.service;

import com.sooum.core.domain.block.service.BlockMemberService;
import com.sooum.core.domain.card.entity.CommentCard;
import com.sooum.core.domain.card.entity.FeedCard;
import com.sooum.core.domain.card.entity.FeedLike;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class FeedService {

    private final BlockMemberService blockMemberService;

/*    public List<LatestFeedCardDto.LatestFeedCardInfo> filterBlockedMembers(List<LatestFeedCardDto.LatestFeedCardInfo> feedCards, Long memberPk) {
        List<Long> allBlockToPk = blockMemberService.findAllBlockToPk(memberPk);
        feedCards.removeIf(feedCard -> allBlockToPk.contains(feedCard.getId()));
        return feedCards;
    }*/

    protected List<FeedCard> filterByBlockedMembers(List<FeedCard> feeds, Long memberPk) {
        List<Long> blockedMembersPk = blockMemberService.findAllBlockToPk(memberPk);
        return feeds.stream()
                .filter(feedCard -> !blockedMembersPk.contains(memberPk))
                .toList();
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
        return (int) comments.stream().filter(comment -> comment.getParentCard().getPk().equals(feed.getPk())).count();
    }
}
