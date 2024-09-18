package com.sooum.core.domain.card.service;

import com.sooum.core.domain.block.service.BlockMemberService;
import com.sooum.core.domain.card.dto.DistanceCardDto;
import com.sooum.core.domain.card.dto.LatestFeedCardDto;
import com.sooum.core.domain.card.dto.distancefilter.DistanceFilter;
import com.sooum.core.domain.card.entity.CommentCard;
import com.sooum.core.domain.card.entity.FeedCard;
import com.sooum.core.domain.card.entity.FeedLike;
import com.sooum.core.global.util.DistanceUtils;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class DistanceFeedService extends FeedService {
    private final GeometryFactory geometryFactory = new GeometryFactory();
    private final FeedCardService feedCardService;
    private final FeedLikeService feedLikeService;
    private final commentCardService commentCardService;

    public DistanceFeedService(BlockMemberService blockMemberService, FeedCardService feedCardService, FeedLikeService feedLikeService, commentCardService commentCardService) {
        super(blockMemberService);
        this.feedCardService = feedCardService;
        this.feedLikeService = feedLikeService;
        this.commentCardService = commentCardService;
    }


    public List<DistanceCardDto> findDistanceFeeds(Long lastId, Long memberPk,
                                                   Double latitude, Double longitude, DistanceFilter distanceFilter) {

        Point userLocation = geometryFactory.createPoint(new Coordinate(longitude, latitude));

        int minDistance = distanceFilter.getMinDistance();
        int maxDistance = distanceFilter.getMaxDistance();

        List<FeedCard> feedsByDistance = feedCardService.findFeedsByDistance(userLocation, lastId, minDistance, maxDistance);

        List<FeedCard> filteredDistanceFeeds= filterBlockedMembers(feedsByDistance, memberPk);

        List<FeedLike> feedLikeList = feedLikeService.findByTargetList(filteredDistanceFeeds);
        List<CommentCard> commentCardList = commentCardService.findByTargetList(filteredDistanceFeeds);

        return filteredDistanceFeeds.stream()
                .map(feedCard -> DistanceCardDto.builder()
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
}
