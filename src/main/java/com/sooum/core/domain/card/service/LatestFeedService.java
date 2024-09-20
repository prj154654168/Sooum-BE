package com.sooum.core.domain.card.service;

import com.sooum.core.domain.block.service.BlockMemberService;
import com.sooum.core.domain.card.dto.LatestFeedCardDto;
import com.sooum.core.domain.card.entity.CommentCard;
import com.sooum.core.domain.card.entity.FeedCard;
import com.sooum.core.domain.card.entity.FeedLike;
import com.sooum.core.domain.img.service.ImgService;
import com.sooum.core.global.util.DistanceUtils;
import com.sooum.core.global.util.NextPageLinkGenerator;
import org.springframework.hateoas.Link;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class LatestFeedService extends FeedService {
    private final FeedCardService feedCardService;
    private final FeedLikeService feedLikeService;
    private final CommentCardService commentCardService;
    private final ImgService imgService;
    private static final int MAX_PAGE_SIZE = 100;
    private static final int DEFAULT_PAGE_SIZE = 50;

    public List<LatestFeedCardDto> createLatestFeedInfo(Long lastCardPk, Long memberPk, Optional<Double> latitude, Optional<Double> longitude) {
        List<FeedCard> filteredLatestFeed = findFilteredLatestFeed(lastCardPk, memberPk);

        List<FeedLike> feedLikeList = feedLikeService.findByTargetList(filteredLatestFeed);
        List<CommentCard> commentCardList = commentCardService.findByTargetList(filteredLatestFeed);

        return NextPageLinkGenerator.appendEachCardDetailLink(filteredLatestFeed.stream()
                .map(feedCard -> LatestFeedCardDto.builder()
                        .id(feedCard.getPk())
                        .font(feedCard.getFont())
                        .fontSize(feedCard.getFontSize())
                        .content(feedCard.getContent())
                        .isStory(feedCard.isStory())
                        .storyExpirationTime(feedCard.getCreatedAt().plusDays(1L))
                        .distance(DistanceUtils.calculate(feedCard.getLocation(), latitude, longitude))
                        .backgroundImgUrl(Link.of(imgService.findImgUrl(feedCard.getImgType(),feedCard.getImgName())))
                        .createdAt(feedCard.getCreatedAt())
                        .isCommentWritten(isWrittenCommentCard(commentCardList, memberPk))
                        .isLiked(isLiked(feedCard, feedLikeList))
                        .likeCnt(countLikes(feedCard, feedLikeList))
                        .commentCnt(countComments(feedCard, commentCardList))
                        .build()
                )
                .toList());
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

    public LatestFeedService(BlockMemberService blockMemberService, FeedCardService feedCardService, FeedLikeService feedLikeService, CommentCardService commentCardService, ImgService imgService) {
        super(blockMemberService);
        this.feedCardService = feedCardService;
        this.feedLikeService = feedLikeService;
        this.commentCardService = commentCardService;
        this.imgService = imgService;
    }
}
