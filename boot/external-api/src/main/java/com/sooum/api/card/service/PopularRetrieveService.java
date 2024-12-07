package com.sooum.api.card.service;

import com.sooum.api.card.dto.PopularCardRetrieve;
import com.sooum.api.img.service.ImgService;
import com.sooum.data.block.service.BlockMemberService;
import com.sooum.data.card.entity.CommentCard;
import com.sooum.data.card.entity.FeedCard;
import com.sooum.data.card.entity.FeedLike;
import com.sooum.data.card.service.CommentCardService;
import com.sooum.data.card.service.FeedLikeService;
import com.sooum.data.card.service.PopularFeedService;
import com.sooum.global.util.DistanceUtils;
import com.sooum.global.util.NextPageLinkGenerator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PopularRetrieveService {
    private final ImgService imgService;
    private final FeedLikeService feedLikeService;
    private final PopularFeedService popularFeedService;
    private final CommentCardService commentCardService;
    private final BlockMemberService blockMemberService;

    public List<PopularCardRetrieve> findHomePopularFeeds(final Optional<Double> latitude,
                                                          final Optional<Double> longitude,
                                                          final Long memberPk) {
        List<Long> blockedMembers = blockMemberService.findAllBlockMemberPks(memberPk);
        List<FeedCard> popularFeeds = popularFeedService.getPopularFeeds(blockedMembers);

        List<FeedLike> feedLikes = feedLikeService.findByTargetCards(popularFeeds);
        List<CommentCard> comments = commentCardService.findByTargetList(popularFeeds);

        return NextPageLinkGenerator.appendEachCardDetailLink(popularFeeds.stream()
                .map(feed -> PopularCardRetrieve.builder()
                        .id(feed.getPk().toString())
                        .content(feed.getContent())
                        .isStory(feed.isStory())
                        .backgroundImgUrl(imgService.findCardImgUrl(feed.getImgType(), feed.getImgName()))
                        .font(feed.getFont())
                        .fontSize(feed.getFontSize())
                        .distance(DistanceUtils.calculate(feed.getLocation(), latitude, longitude))
                        .createdAt(feed.getCreatedAt())
                        .isLiked(FeedService.isLiked(feed, feedLikes, memberPk))
                        .likeCnt(FeedService.countLikes(feed, feedLikes))
                        .isCommentWritten(FeedService.isWrittenCommentCard(comments, memberPk))
                        .commentCnt(FeedService.countComments(feed, comments))
                        .build()
                )
                .toList());
    }
}
