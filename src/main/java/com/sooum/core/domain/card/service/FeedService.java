package com.sooum.core.domain.card.service;

import com.sooum.core.domain.block.service.BlockMemberService;
import com.sooum.core.domain.card.entity.CommentCard;
import com.sooum.core.domain.card.entity.FeedCard;
import com.sooum.core.domain.card.entity.FeedLike;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class FeedService {

    private final BlockMemberService blockMemberService;

    public List<FeedCard> filterBlockedMembers(List<FeedCard> feedCards, Long memberPk) {
        List<Long> allBlockToPk = blockMemberService.findAllBlockToPk(memberPk);
        return feedCards.stream()
                .filter(feedCard -> !allBlockToPk.contains(feedCard.getPk()))
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
        return (int) comments.stream().filter(comment -> comment.getParentCardPk().equals(feed.getPk())).count();
    }
}
