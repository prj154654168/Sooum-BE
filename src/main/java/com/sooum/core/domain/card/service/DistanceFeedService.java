package com.sooum.core.domain.card.service;

import com.sooum.core.domain.block.service.BlockMemberService;
import com.sooum.core.domain.card.dto.DistanceCardDto;
import com.sooum.core.domain.card.dto.distancefilter.DistanceFilter;
import com.sooum.core.domain.card.entity.CommentCard;
import com.sooum.core.domain.card.entity.FeedCard;
import com.sooum.core.domain.card.entity.FeedLike;
import com.sooum.core.domain.img.service.ImgService;
import com.sooum.core.global.util.DistanceUtils;
import com.sooum.core.global.util.NextPageLinkGenerator;
import lombok.RequiredArgsConstructor;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.springframework.hateoas.Link;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

import static com.sooum.core.domain.card.service.FeedService.*;

@RequiredArgsConstructor
@Service
public class DistanceFeedService {
    private final GeometryFactory geometryFactory = new GeometryFactory();
    private final FeedCardService feedCardService;
    private final FeedLikeService feedLikeService;
    private final ImgService imgService;
    private final CommentCardService commentCardService;
    private final BlockMemberService blockMemberService;

    private static final int MAX_PAGE_SIZE = 100;
    private static final int DEFAULT_PAGE_SIZE = 50;

    public List<DistanceCardDto> findDistanceFeeds(Long lastId, Long memberPk,
                                                   Double latitude, Double longitude, DistanceFilter distanceFilter) {
        Point userLocation = geometryFactory.createPoint(new Coordinate(longitude, latitude));

        double minDistance = distanceFilter.getMinDistance();
        double maxDistance = distanceFilter.getMaxDistance();

        List<FeedCard> filteredDistanceFeeds = findFilteredDistanceFeeds(lastId, memberPk, userLocation, minDistance, maxDistance);

        List<FeedLike> feedLikeList = feedLikeService.findByTargetCards(filteredDistanceFeeds);

        List<CommentCard> commentCardList = commentCardService.findByTargetList(filteredDistanceFeeds);

        return NextPageLinkGenerator.appendEachCardDetailLink(filteredDistanceFeeds.stream()
                .map(feedCard -> DistanceCardDto.builder()
                        .id(feedCard.getPk())
                        .font(feedCard.getFont())
                        .fontSize(feedCard.getFontSize())
                        .content(feedCard.getContent())
                        .isStory(feedCard.isStory())
                        .distance(DistanceUtils.calculate(feedCard.getLocation(), latitude, longitude))
                        .backgroundImgUrl(imgService.findImgUrl(feedCard.getImgType(),feedCard.getImgName()))
                        .createdAt(feedCard.getCreatedAt())
                        .isCommentWritten(isWrittenCommentCard(commentCardList, memberPk))
                        .isLiked(isLiked(feedCard, feedLikeList))
                        .likeCnt(countLikes(feedCard, feedLikeList))
                        .commentCnt(countComments(feedCard, commentCardList))
                        .build()
                )
                .toList());
    }

    private List<FeedCard> findFilteredDistanceFeeds(Long lastId, Long memberPk, Point userLocation, double minDistance, double maxDistance) {
        ArrayList<FeedCard> resultFeedCards = new ArrayList<>();

        while (resultFeedCards.size() < DEFAULT_PAGE_SIZE) {
            List<FeedCard> feedsByDistance = feedCardService.findFeedsByDistance(userLocation, lastId, minDistance, maxDistance);

            if (feedsByDistance.isEmpty()) {
                break;
            }
            lastId = feedsByDistance.get(feedsByDistance.size() - 1).getPk();

            List<FeedCard> filteredFeedCards = blockMemberService.filterBlockedMembers(feedsByDistance, memberPk);
            resultFeedCards.addAll(filteredFeedCards);

            if (isEndOfPage(feedsByDistance)) {
                break;
            }
        }

        return resultFeedCards.size() > DEFAULT_PAGE_SIZE
                ? resultFeedCards.subList(0, DEFAULT_PAGE_SIZE)
                : resultFeedCards;
    }

    private static boolean isEndOfPage(List<FeedCard> feedsByDistance) {
        return feedsByDistance.size() < MAX_PAGE_SIZE;
    }
}