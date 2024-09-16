package com.sooum.core.domain.card.service;

import com.sooum.core.domain.block.service.BlockMemberService;
import com.sooum.core.domain.card.dto.LatestFeedCardDto;
import com.sooum.core.domain.card.entity.CommentCard;
import com.sooum.core.domain.card.entity.FeedCard;
import com.sooum.core.domain.card.entity.FeedLike;
import com.sooum.core.global.util.DistanceUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
public class LatestFeedService extends FeedService {
    private final FeedCardService feedCardService;
    private final FeedLikeService feedLikeService;
    private final com.sooum.core.domain.card.service.commentCardService commentCardService;
    private static final int MAX_PAGE_SIZE = 100;
    private static final int DEFAULT_PAGE_SIZE = 50;

    public List<LatestFeedCardDto> createLatestFeedInfo(Long lastCardId, Long memberPk, Optional<Double> latitude, Optional<Double> longitude) {
        List<FeedCard> filteredLatestFeed = findFilteredLatestFeed(lastCardId, memberPk);

        List<FeedLike> feedLikeList = feedLikeService.findByTargetList(filteredLatestFeed);
        List<CommentCard> commentCardList = commentCardService.findByTargetList(filteredLatestFeed);
        return filteredLatestFeed.stream()
                .map(feedCard -> LatestFeedCardDto.builder()
                        .id(feedCard.getPk())
                        .font(feedCard.getFont())
                        .fontSize(feedCard.getFontSize())
                        .content(feedCard.getContent())
                        .isStory(feedCard.isStory())
                        .storyExpirationTime(feedCard.getCreatedAt().plusDays(1L))
                        .distance(DistanceUtils.calculate(feedCard.getLocation(), latitude, longitude))
                        .backgroundImgUrl(null)//todo
                        .createdAt(feedCard.getCreatedAt())
                        .isCommentWritten(isWrittenCommentCard(commentCardList, memberPk))
                        .isLiked(isLiked(feedCard, feedLikeList))
                        .likeCnt(countLikes(feedCard, feedLikeList))
                        .commentCnt(countComments(feedCard, commentCardList))
                        .build()
                )
                .toList();
    }


    private List<FeedCard> findFilteredLatestFeed(Long lastCardId, Long memberId) {

        ArrayList<FeedCard> resultFeedCards = new ArrayList<>();

        while (resultFeedCards.size() < 50) {
            List<FeedCard> byLastId = feedCardService.findByLastId(lastCardId);

            if (!byLastId.isEmpty()) {
                lastCardId = byLastId.get(byLastId.size() - 1).getPk();
            }

            List<FeedCard> filteredFeedCards = filterBlockedMembers(byLastId, memberId);
            resultFeedCards.addAll(filteredFeedCards);

            if (isEndOfPage(byLastId)) {
                break;
            }
        }
        return resultFeedCards.size() > DEFAULT_PAGE_SIZE ? resultFeedCards.subList(0, DEFAULT_PAGE_SIZE) : resultFeedCards;
    }

    private static boolean isEndOfPage(List<FeedCard> byLastId) {
        return byLastId.size() < MAX_PAGE_SIZE;
    }

    public LatestFeedService(BlockMemberService blockMemberService, FeedCardService feedCardService, FeedLikeService feedLikeService, com.sooum.core.domain.card.service.commentCardService commentCardService) {
        super(blockMemberService);
        this.feedCardService = feedCardService;
        this.feedLikeService = feedLikeService;
        this.commentCardService = commentCardService;
    }
}
