package com.sooum.core.domain.card.service;

import com.sooum.core.domain.block.service.BlockMemberService;
import com.sooum.core.domain.card.controller.FeedCardController;
import com.sooum.core.domain.card.dto.TagFeedCardDto;
import com.sooum.core.domain.card.entity.CommentCard;
import com.sooum.core.domain.card.entity.FeedCard;
import com.sooum.core.domain.card.entity.FeedLike;
import com.sooum.core.domain.img.service.ImgService;
import com.sooum.core.domain.tag.repository.FeedTagRepository;
import com.sooum.core.global.util.DistanceUtils;
import com.sooum.core.global.util.NextPageLinkGenerator;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.hateoas.Link;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TagFeedService {
    private final FeedTagRepository feedTagRepository;
    private final BlockMemberService blockMemberService;
    private final FeedLikeService feedLikeService;
    private final CommentCardService commentCardService;
    private final ImgService imgService;
    private static final int MAX_PAGE_SIZE = 100;
    private final static int DEFAULT_PAGE_SIZE = 50;

    public List<FeedCard> findTagFeeds(Optional<Long> lastPk, String tagContent) {
        PageRequest pageRequest = PageRequest.of(0, MAX_PAGE_SIZE);
        return lastPk.isEmpty()
                ? feedTagRepository.findFeeds(tagContent, pageRequest)
                : feedTagRepository.findFeeds(tagContent, lastPk.get(), pageRequest);
    }

    public List<TagFeedCardDto> createTagFeedsInfo(String tagContent,
                                                   Optional<Long> lastPk,
                                                   Optional<Double> latitude,
                                                   Optional<Double> longitude,
                                                   Long memberPk) {
        List<FeedCard> filteredTagFeeds = findFilteredTagFeeds(tagContent, lastPk, memberPk);

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
                        .isLiked(FeedService.isLiked(feedCard, feedLikes))
                        .likeCnt(FeedService.countLikes(feedCard, feedLikes))
                        .commentCnt(FeedService.countComments(feedCard, commentCards))
                        .build()
                )
                .toList());
    }


    private List<FeedCard> findFilteredTagFeeds(String tagContent, Optional<Long> lastPk, Long memberId) {
        List<FeedCard> resultFeedCards = new ArrayList<>();

        while (resultFeedCards.size() < DEFAULT_PAGE_SIZE) {
            List<FeedCard> findTagFeeds = findTagFeeds(lastPk, tagContent);

            if (!findTagFeeds.isEmpty()) {
                lastPk = Optional.of(findTagFeeds.get(findTagFeeds.size() - 1).getPk());
            }

            List<FeedCard> filteredFeedCards = blockMemberService.filterBlockedMembers(findTagFeeds, memberId);
            resultFeedCards.addAll(filteredFeedCards);

            if (isEndOfPage(findTagFeeds)) {
                break;
            }
        }
        return resultFeedCards.size() > DEFAULT_PAGE_SIZE ? resultFeedCards.subList(0, DEFAULT_PAGE_SIZE) : resultFeedCards;
    }

    private static boolean isEndOfPage(List<FeedCard> findTagFeeds) {
        return findTagFeeds.size() < MAX_PAGE_SIZE;
    }

    public Link createNextTagFeedsUrl(String tagContent, List<TagFeedCardDto> tagFeedsInfo) {
        String lastPk = tagFeedsInfo.get(tagFeedsInfo.size() - 1).getId();
        return linkTo(methodOn(FeedCardController.class).getClass())
                .slash("/tags/" + tagContent + "?lastPk=" + lastPk).withRel("next");
    }
}
