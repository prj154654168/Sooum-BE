package com.sooum.api.card.service;

import com.sooum.api.card.dto.DistanceCardDto;
import com.sooum.api.card.dto.distancefilter.DistanceFilter;
import com.sooum.api.img.service.ImgService;
import com.sooum.data.block.service.BlockMemberService;
import com.sooum.data.card.entity.CommentCard;
import com.sooum.data.card.entity.FeedCard;
import com.sooum.data.card.entity.FeedLike;
import com.sooum.data.card.service.CommentCardService;
import com.sooum.data.card.service.FeedCardService;
import com.sooum.data.card.service.FeedLikeService;
import com.sooum.global.util.DistanceUtils;
import com.sooum.global.util.NextPageLinkGenerator;
import lombok.RequiredArgsConstructor;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

import static com.sooum.api.card.service.FeedService.*;

@RequiredArgsConstructor
@Service
public class DistanceFeedService {
    private final GeometryFactory geometryFactory = new GeometryFactory();
    private final FeedCardService feedCardService;
    private final FeedLikeService feedLikeService;
    private final ImgService imgService;
    private final CommentCardService commentCardService;
    private final BlockMemberService blockMemberService;

    public List<DistanceCardDto> findDistanceFeeds(Optional<Long> lastPk, Long memberPk,
                                                   Double latitude, Double longitude, DistanceFilter distanceFilter) {
        Point userLocation = geometryFactory.createPoint(new Coordinate(longitude, latitude));

        double minDistance = distanceFilter.getMinDistance();
        double maxDistance = distanceFilter.getMaxDistance();

        List<FeedCard> filteredDistanceFeeds = findFilteredDistanceFeeds(lastPk, memberPk, userLocation, minDistance, maxDistance);

        List<FeedLike> feedLikeList = feedLikeService.findByTargetCards(filteredDistanceFeeds);
        List<CommentCard> commentCardList = commentCardService.findCommentCardsIn(filteredDistanceFeeds);

        return NextPageLinkGenerator.appendEachCardDetailLink(filteredDistanceFeeds.stream()
                .map(feedCard -> DistanceCardDto.builder()
                        .id(feedCard.getPk().toString())
                        .font(feedCard.getFont())
                        .fontSize(feedCard.getFontSize())
                        .content(feedCard.getContent())
                        .isStory(feedCard.isStory())
                        .distance(DistanceUtils.calculate(feedCard.getLocation(), latitude, longitude))
                        .backgroundImgUrl(imgService.findCardImgUrl(feedCard.getImgType(),feedCard.getImgName()))
                        .createdAt(feedCard.getCreatedAt())
                        .isCommentWritten(isWrittenCommentCard(feedCard, commentCardList, memberPk))
                        .isLiked(isLiked(feedCard, feedLikeList, memberPk))
                        .likeCnt(countLikes(feedCard, feedLikeList))
                        .commentCnt(countComments(feedCard, commentCardList))
                        .build()
                )
                .toList());
    }

    private List<FeedCard> findFilteredDistanceFeeds(Optional<Long> lastPk, Long memberPk, Point userLocation, double minDistance, double maxDistance) {
        List<Long> allBlockedPks = blockMemberService.findAllBlockMemberPks(memberPk);
        return feedCardService.findFeedsByDistance(lastPk, userLocation, minDistance, maxDistance, allBlockedPks);
    }
}