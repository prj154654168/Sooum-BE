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
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;


@RequiredArgsConstructor
@Service
public class LatestFeedUseCase {
    private final FeedCardService feedCardService;
    private final FeedLikeService feedLikeService;
    private final CommentCardService commentCardService;
    private final ImgService imgService;
    private final BlockMemberService blockMemberService;

    @Transactional(readOnly = true)
    public List<LatestFeedCardDto> createLatestFeedInfo(Optional<Long> lastCardPk,
                                                        Long memberPk,
                                                        Optional<Double> latitude,
                                                        Optional<Double> longitude) {

        List<FeedCard> filteredFeedCards = findBlockMemberFilteredLatestFeed(lastCardPk, memberPk);
        List<FeedLike> feedLikes = findFeedLikesInLatestFeed(filteredFeedCards);
        List<CommentCard> commentCards = findCommentCardsInLatestFeed(filteredFeedCards);

        return convertToLatestFeedDtos(memberPk, latitude, longitude, filteredFeedCards, feedLikes, commentCards);
    }

    private List<LatestFeedCardDto> convertToLatestFeedDtos(Long memberPk,
                                                            Optional<Double> latitude,
                                                            Optional<Double> longitude,
                                                            List<FeedCard> feedCards,
                                                            List<FeedLike> feedLikes,
                                                            List<CommentCard> commentCards) {

        return NextPageLinkGenerator.appendEachCardDetailLink(feedCards.stream()
                .map(feedCard -> LatestFeedCardDto.builder()
                        .id(feedCard.getPk().toString())
                        .font(feedCard.getFont())
                        .fontSize(feedCard.getFontSize())
                        .content(feedCard.getContent())
                        .isStory(feedCard.isStory())
                        .distance(DistanceUtils.calculate(feedCard.getLocation(), latitude, longitude))
                        .backgroundImgUrl(imgService.findCardImgUrl(feedCard.getImgType(),feedCard.getImgName()))
                        .createdAt(feedCard.getCreatedAt())
                        .isCommentWritten(FeedService.isWrittenCommentCard(commentCards, memberPk))
                        .isLiked(FeedService.isLiked(feedCard, feedLikes, memberPk))
                        .likeCnt(FeedService.countLikes(feedCard, feedLikes))
                        .commentCnt(FeedService.countComments(feedCard, commentCards))
                        .build()
                )
                .toList());
    }

    private List<CommentCard> findCommentCardsInLatestFeed(List<FeedCard> filteredLatestFeed) {
        return commentCardService.findCommentCardsIn(filteredLatestFeed);
    }

    private List<FeedLike> findFeedLikesInLatestFeed(List<FeedCard> filteredLatestFeed) {
        return feedLikeService.findByTargetCards(filteredLatestFeed);
    }

    private List<FeedCard> findBlockMemberFilteredLatestFeed(Optional<Long> lastCardId, Long memberId) {
        List<Long> allBlockToPk = blockMemberService.findAllBlockMemberPks(memberId);
        return feedCardService.findByLastId(lastCardId, allBlockToPk);
    }
}
