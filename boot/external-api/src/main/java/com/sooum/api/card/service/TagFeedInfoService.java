package com.sooum.api.card.service;

import com.sooum.api.card.controller.FeedCardController;
import com.sooum.api.card.dto.TagFeedCardDto;
import com.sooum.api.img.service.ImgService;
import com.sooum.data.card.entity.CommentCard;
import com.sooum.data.card.entity.FeedCard;
import com.sooum.data.card.entity.FeedLike;
import com.sooum.data.card.service.CommentCardService;
import com.sooum.data.card.service.FeedCardService;
import com.sooum.data.card.service.FeedLikeService;
import com.sooum.data.card.service.TagFeedService;
import com.sooum.global.util.DistanceUtils;
import com.sooum.global.util.NextPageLinkGenerator;
import lombok.RequiredArgsConstructor;
import org.springframework.hateoas.Link;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Service
@RequiredArgsConstructor
public class TagFeedInfoService {
    private final TagFeedService tagFeedService;
    private final FeedLikeService feedLikeService;
    private final CommentCardService commentCardService;
    private final ImgService imgService;

    public List<TagFeedCardDto> createTagFeedsInfo(Long tagPk,
                                                   Optional<Long> lastPk,
                                                   Optional<Double> latitude,
                                                   Optional<Double> longitude,
                                                   Long memberPk) {
        List<FeedCard> filteredTagFeeds = tagFeedService.findFilteredTagFeeds(tagPk, lastPk, memberPk);

        List<FeedLike> feedLikes = feedLikeService.findByTargetCards(filteredTagFeeds);
        List<CommentCard> commentCards = commentCardService.findByTargetList(filteredTagFeeds);

        return NextPageLinkGenerator.appendEachCardDetailLink(filteredTagFeeds.stream()
                .map(feedCard -> TagFeedCardDto.builder()
                        .id(feedCard.getPk().toString())
                        .font(feedCard.getFont())
                        .fontSize(feedCard.getFontSize())
                        .content(feedCard.getContent())
                        .distance(DistanceUtils.calculate(feedCard.getLocation(), latitude, longitude))
                        .backgroundImgUrl(imgService.findImgUrl(feedCard.getImgType(), feedCard.getImgName()))
                        .createdAt(feedCard.getCreatedAt())
                        .isCommentWritten(FeedService.isWrittenCommentCard(commentCards, memberPk))
                        .isLiked(FeedService.isLiked(feedCard, feedLikes, memberPk))
                        .likeCnt(FeedService.countLikes(feedCard, feedLikes))
                        .commentCnt(FeedService.countComments(feedCard, commentCards))
                        .build()
                )
                .toList());
    }

    public Link createNextTagFeedsUrl(Long tagPk, List<TagFeedCardDto> tagFeedsInfo) {
        String lastPk = tagFeedsInfo.get(tagFeedsInfo.size() - 1).getId();
        return linkTo(methodOn(FeedCardController.class).getClass())
                .slash("/tags/" + tagPk + "?lastPk=" + lastPk).withRel("next");
    }
}
