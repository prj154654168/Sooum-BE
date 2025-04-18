package com.sooum.api.card.service;

import com.sooum.api.card.controller.FeedCardController;
import com.sooum.api.card.dto.TagFeedCardDto;
import com.sooum.api.img.service.ImgService;
import com.sooum.data.block.service.BlockMemberService;
import com.sooum.data.card.entity.CommentCard;
import com.sooum.data.card.entity.FeedCard;
import com.sooum.data.card.entity.FeedLike;
import com.sooum.data.card.service.CommentCardService;
import com.sooum.data.card.service.FeedLikeService;
import com.sooum.data.card.service.TagFeedService;
import com.sooum.global.util.CardUtils;
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
    private final BlockMemberService blockMemberService;
    private final ImgService imgService;

    public List<TagFeedCardDto> createTagFeedsInfo(Long tagPk,
                                                   Optional<Long> lastPk,
                                                   Optional<Double> latitude,
                                                   Optional<Double> longitude,
                                                   Long memberPk) {
        List<Long> blockMemberPks = blockMemberService.findAllBlockMemberPks(memberPk);
        List<FeedCard> filteredTagFeeds = tagFeedService.findTagFeeds(tagPk, lastPk, blockMemberPks);

        List<FeedLike> feedLikes = feedLikeService.findByTargetCards(filteredTagFeeds);
        List<CommentCard> commentCards = commentCardService.findCommentCardsIn(filteredTagFeeds);

        return NextPageLinkGenerator.appendEachCardDetailLink(filteredTagFeeds.stream()
                .map(feedCard -> TagFeedCardDto.builder()
                        .id(feedCard.getPk().toString())
                        .font(feedCard.getFont())
                        .fontSize(feedCard.getFontSize())
                        .content(feedCard.getContent())
                        .distance(DistanceUtils.calculate(feedCard.getLocation(), latitude, longitude))
                        .backgroundImgUrl(imgService.findCardImgUrl(feedCard.getImgType(), feedCard.getImgName()))
                        .createdAt(feedCard.getCreatedAt())
                        .isCommentWritten(CardUtils.isWrittenCommentCard(feedCard, commentCards, memberPk))
                        .isLiked(CardUtils.isLiked(feedCard, feedLikes, memberPk))
                        .likeCnt(CardUtils.countLikes(feedCard, feedLikes))
                        .commentCnt(CardUtils.countComments(feedCard, commentCards))
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
