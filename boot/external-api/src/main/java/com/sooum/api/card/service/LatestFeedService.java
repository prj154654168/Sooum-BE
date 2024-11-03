package com.sooum.api.card.service;

import com.sooum.api.card.dto.LatestFeedCardDto;
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
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;


@RequiredArgsConstructor
@Service
public class LatestFeedService {
    private final FeedCardService feedCardService;
    private final FeedLikeService feedLikeService;
    private final CommentCardService commentCardService;
    private final ImgService imgService;
    private final BlockMemberService blockMemberService;

    private static final int MAX_PAGE_SIZE = 100;
    private static final int DEFAULT_PAGE_SIZE = 50;

    public List<LatestFeedCardDto> createLatestFeedInfo(Optional<Long> lastCardPk, Long memberPk, Optional<Double> latitude, Optional<Double> longitude) {
        List<FeedCard> filteredLatestFeed = findFilteredLatestFeed(lastCardPk, memberPk);

        List<FeedLike> feedLikeList = feedLikeService.findByTargetCards(filteredLatestFeed);
        List<CommentCard> commentCardList = commentCardService.findByTargetList(filteredLatestFeed);

        return NextPageLinkGenerator.appendEachCardDetailLink(filteredLatestFeed.stream()
                .map(feedCard -> LatestFeedCardDto.builder()
                        .id(feedCard.getPk().toString())
                        .font(feedCard.getFont())
                        .fontSize(feedCard.getFontSize())
                        .content(feedCard.getContent())
                        .isStory(feedCard.isStory())
                        .distance(DistanceUtils.calculate(feedCard.getLocation(), latitude, longitude))
                        .backgroundImgUrl(imgService.findImgUrl(feedCard.getImgType(),feedCard.getImgName()))
                        .createdAt(feedCard.getCreatedAt())
                        .isCommentWritten(FeedService.isWrittenCommentCard(commentCardList, memberPk))
                        .isLiked(FeedService.isLiked(feedCard, feedLikeList, memberPk))
                        .likeCnt(FeedService.countLikes(feedCard, feedLikeList))
                        .commentCnt(FeedService.countComments(feedCard, commentCardList))
                        .build()
                )
                .toList());
    }

    private List<FeedCard> findFilteredLatestFeed(Optional<Long> lastCardId, Long memberId) {
        List<Long> allBlockToPk = blockMemberService.findAllBlockToPk(memberId);
        return feedCardService.findByLastId(lastCardId, allBlockToPk);
    }

    private static boolean isEndOfPage(List<FeedCard> byLastId) {
        return byLastId.size() < MAX_PAGE_SIZE;
    }
}
