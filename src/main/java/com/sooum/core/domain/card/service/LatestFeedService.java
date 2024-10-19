package com.sooum.core.domain.card.service;

import com.sooum.core.domain.block.service.BlockMemberService;
import com.sooum.core.domain.card.dto.LatestFeedCardDto;
import com.sooum.core.domain.card.entity.CommentCard;
import com.sooum.core.domain.card.entity.FeedCard;
import com.sooum.core.domain.card.entity.FeedLike;
import com.sooum.core.domain.img.service.ImgService;
import com.sooum.core.global.util.DistanceUtils;
import com.sooum.core.global.util.NextPageLinkGenerator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

import static com.sooum.core.domain.card.service.FeedService.*;

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
                        .isCommentWritten(isWrittenCommentCard(commentCardList, memberPk))
                        .isLiked(isLiked(feedCard, feedLikeList, memberPk))
                        .likeCnt(countLikes(feedCard, feedLikeList))
                        .commentCnt(countComments(feedCard, commentCardList))
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
