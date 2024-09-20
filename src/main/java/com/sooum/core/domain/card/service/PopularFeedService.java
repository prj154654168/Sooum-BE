package com.sooum.core.domain.card.service;

import com.sooum.core.domain.block.service.BlockMemberService;
import com.sooum.core.domain.card.controller.FeedCardController;
import com.sooum.core.domain.card.dto.PopularCardDto;
import com.sooum.core.domain.card.dto.popularitytype.PopularityType;
import com.sooum.core.domain.card.entity.CommentCard;
import com.sooum.core.domain.card.entity.FeedCard;
import com.sooum.core.domain.card.entity.FeedLike;
import com.sooum.core.domain.card.entity.PopularFeed;
import com.sooum.core.domain.card.repository.PopularFeedRepository;
import com.sooum.core.domain.img.service.ImgService;
import com.sooum.core.global.util.DistanceUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.hateoas.Link;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Service
@Transactional(readOnly = true)
@Slf4j
public class PopularFeedService extends FeedService{
    private final PopularFeedRepository popularFeedRepository;
    private final ImgService imgService;
    private final FeedLikeService feedLikeService;
    private final CommentCardService commentCardService;
    private static final int MAX_SIZE = 200;

    public List<PopularCardDto.PopularCardRetrieve> findHomePopularFeeds(final Optional<Double> latitude,
                                                                         final Optional<Double> longitude,
                                                                         final Long memberPk) {
        PageRequest pageRequest = PageRequest.of(0, MAX_SIZE);
        LocalDateTime storyExpiredTime = LocalDateTime.now().minusDays(1L);
        List<PopularFeed> popularFeeds = popularFeedRepository.findPopularFeeds(storyExpiredTime, pageRequest);
        List<FeedCard> feeds = popularFeeds.stream().map(PopularFeed::getPopularCard).toList();
        List<FeedCard> filteredFeeds = filterBlockedMembers(feeds, memberPk);

        List<FeedLike> feedLikes = feedLikeService.findByTargetCards(filteredFeeds);
        List<CommentCard> comments = commentCardService.findByMasterCards(filteredFeeds);

        return filteredFeeds.stream().map(feed -> PopularCardDto.PopularCardRetrieve.builder()
                .id(feed.getPk())
                .contents(feed.getContent())
                .isStory(feed.isStory())
                .backgroundImgUrl(Link.of(imgService.findImgUrl(feed.getImgType(), feed.getImgName())))
                .font(feed.getFont())
                .fontSize(feed.getFontSize())
                .distance(DistanceUtils.calculate(feed.getLocation(), latitude, longitude))
                .createdAt(feed.getCreatedAt())
                .isLiked(isLiked(feed, feedLikes))
                .likeCnt(countLikes(feed, feedLikes))
                .isCommentWritten(isWrittenCommentCard(comments, memberPk))
                .commentCnt(countComments(feed, comments))
                .popularityType(findPopularityType(feed, popularFeeds))
                .build()
                        .add(linkTo(methodOn(FeedCardController.class).findFeedCardInfo(feed.getPk())).withRel("detail")))
                .toList();
    }

    private PopularityType findPopularityType(FeedCard feed, List<PopularFeed> popularFeeds) {
        return popularFeeds.stream().filter(popularFeed -> popularFeed.getPopularCard().equals(feed)).findFirst().get().getPopularityType();
    }

    public PopularFeedService(BlockMemberService blockMemberService, PopularFeedRepository popularFeedRepository, ImgService imgService, FeedLikeService feedLikeService, CommentCardService commentCardService) {
        super(blockMemberService);
        this.popularFeedRepository = popularFeedRepository;
        this.imgService = imgService;
        this.feedLikeService = feedLikeService;
        this.commentCardService = commentCardService;
    }
}
