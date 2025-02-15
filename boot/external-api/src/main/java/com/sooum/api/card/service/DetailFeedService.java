package com.sooum.api.card.service;

import com.sooum.api.card.dto.CardDto;
import com.sooum.api.card.dto.CommentDetailCardDto;
import com.sooum.api.card.dto.CommentDetailCardDtoV2;
import com.sooum.api.card.dto.FeedDetailCardDto;
import com.sooum.api.img.service.ImgService;
import com.sooum.api.member.service.MemberInfoService;
import com.sooum.api.tag.service.TagUseCase;
import com.sooum.data.card.entity.Card;
import com.sooum.data.card.entity.CommentCard;
import com.sooum.data.card.entity.FeedCard;
import com.sooum.data.card.service.CommentCardService;
import com.sooum.data.card.service.FeedCardService;
import com.sooum.global.exceptionmessage.ExceptionMessage;
import com.sooum.global.util.DistanceUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.hateoas.Link;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class DetailFeedService {
    private final FeedCardService feedCardService;
    private final ImgService imgService;
    private final TagUseCase tagUseCase;
    private final MemberInfoService memberInfoService;
    private final CommentCardService commentCardService;
    private final FeedService feedService;

    @Transactional(readOnly = true)
    public CardDto findDetailFeedCard (Long cardPk, Long memberPk, Optional<Double> latitude, Optional<Double> longitude) {
        Card card = feedCardService.isExistFeedCard(cardPk)
                ? feedCardService.findFeedCard(cardPk)
                : commentCardService.findCommentCard(cardPk);

        return createDetailCardDto(card, memberPk, latitude, longitude);
    }

    public CardDto createDetailCardDto(Card card, Long memberPk, Optional<Double> latitude, Optional<Double> longitude) {
        if (card instanceof FeedCard feedCard) {
            return FeedDetailCardDto.builder()
                    .id(card.getPk().toString())
                    .backgroundImgUrl(imgService.findCardImgUrl(card.getImgType(), card.getImgName()))
                    .isStory(feedCard.isStory())
                    .createdAt(card.getCreatedAt())
                    .content(card.getContent())
                    .distance(DistanceUtils.calculate(card.getLocation(), latitude, longitude))
                    .font(card.getFont())
                    .fontSize(card.getFontSize())
                    .isOwnCard(memberPk.equals(card.getWriter().getPk()))
                    .isFeedCard(true)
                    .member(memberInfoService.getDefaultMember(card.getWriter()))
                    .tags(tagUseCase.readTags(card))
                    .build();
        }
        if (card instanceof CommentCard commentCard) {
            Card parentCard = feedService.findParentCard(commentCard);

            Optional<FeedCard> optMasterCard = feedCardService.findOptFeedCard(commentCard.getMasterCard());
            boolean isMasterCardStory = optMasterCard.map(FeedCard::isStory).orElse(false);

            boolean isParentCardDelete = isParentDelete(parentCard, commentCard.getParentCardPk());
            Link previousCardImgLink = parentCard != null ?
                    imgService.findCardImgUrl(parentCard.getImgType(),parentCard.getImgName()) : null;

            return CommentDetailCardDto.builder()
                    .id(card.getPk().toString())
                    .backgroundImgUrl(imgService.findCardImgUrl(card.getImgType(), card.getImgName()))
                    .createdAt(card.getCreatedAt())
                    .content(card.getContent())
                    .distance(DistanceUtils.calculate(card.getLocation(), latitude, longitude))
                    .font(card.getFont())
                    .fontSize(card.getFontSize())
                    .isOwnCard(memberPk.equals(card.getWriter().getPk()))
                    .member(memberInfoService.getDefaultMember(card.getWriter()))
                    .tags(tagUseCase.readTags(card))
                    .isPreviousCardDelete(isParentCardDelete)
                    .previousCardId(commentCard.getParentCardPk().toString())
                    .previousCardImgLink(previousCardImgLink)
                    .isFeedCardStory(isMasterCardStory)
                    .build();
        }
        throw new IllegalArgumentException(ExceptionMessage.UNHANDLED_OBJECT.getMessage());
    }

    @Transactional(readOnly = true)
    public CardDto findDetailFeedCardV2 (Long cardPk, Long memberPk, Optional<Double> latitude, Optional<Double> longitude) {
        Card card = feedCardService.isExistFeedCard(cardPk)
                ? feedCardService.findFeedCard(cardPk)
                : commentCardService.findCommentCard(cardPk);

        return createDetailCardDtoV2(card, memberPk, latitude, longitude);
    }

    public CardDto createDetailCardDtoV2(Card card, Long memberPk, Optional<Double> latitude, Optional<Double> longitude) {
        if (card instanceof FeedCard feedCard) {
            return FeedDetailCardDto.builder()
                    .id(card.getPk().toString())
                    .backgroundImgUrl(imgService.findCardImgUrl(card.getImgType(), card.getImgName()))
                    .isStory(feedCard.isStory())
                    .createdAt(card.getCreatedAt())
                    .content(card.getContent())
                    .distance(DistanceUtils.calculate(card.getLocation(), latitude, longitude))
                    .font(card.getFont())
                    .fontSize(card.getFontSize())
                    .isOwnCard(memberPk.equals(card.getWriter().getPk()))
                    .isFeedCard(true)
                    .member(memberInfoService.getDefaultMember(card.getWriter()))
                    .tags(tagUseCase.readTags(card))
                    .build();
        }
        if (card instanceof CommentCard commentCard) {
            Card parentCard = feedService.findParentCard(commentCard);

            Optional<FeedCard> optMasterCard = feedCardService.findOptFeedCard(commentCard.getMasterCard());
            boolean isMasterCardStory = optMasterCard.map(FeedCard::isStory).orElse(false);
            LocalDateTime masterCardStoryExpiredTime = optMasterCard.map(optCard -> optCard.getCreatedAt().plusDays(1)).orElse(null);

            boolean isParentCardDelete = isParentDelete(parentCard, commentCard.getParentCardPk());
            Link previousCardImgLink = parentCard != null ?
                    imgService.findCardImgUrl(parentCard.getImgType(),parentCard.getImgName()) : null;

            return CommentDetailCardDtoV2.builder()
                    .id(card.getPk().toString())
                    .backgroundImgUrl(imgService.findCardImgUrl(card.getImgType(), card.getImgName()))
                    .createdAt(card.getCreatedAt())
                    .content(card.getContent())
                    .distance(DistanceUtils.calculate(card.getLocation(), latitude, longitude))
                    .font(card.getFont())
                    .fontSize(card.getFontSize())
                    .isOwnCard(memberPk.equals(card.getWriter().getPk()))
                    .member(memberInfoService.getDefaultMember(card.getWriter()))
                    .tags(tagUseCase.readTags(card))
                    .isPreviousCardDelete(isParentCardDelete)
                    .previousCardId(commentCard.getParentCardPk().toString())
                    .previousCardImgLink(previousCardImgLink)
                    .isFeedCardStory(isMasterCardStory)
                    .storyExpirationTime(masterCardStoryExpiredTime)
                    .build();
        }
        throw new IllegalArgumentException(ExceptionMessage.UNHANDLED_OBJECT.getMessage());
    }

    private boolean isParentDelete(Card parentCard, Long parentPk) {
        if (parentCard == null && parentPk != null) {
            return true;
        }
        if (parentCard != null) {
            return false;
        }
        return false;
    }
}