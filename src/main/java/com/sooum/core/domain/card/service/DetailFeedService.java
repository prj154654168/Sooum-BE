package com.sooum.core.domain.card.service;

import com.sooum.core.domain.card.dto.CardDto;
import com.sooum.core.domain.card.dto.CommentDetailCardDto;
import com.sooum.core.domain.card.dto.FeedDetailCardDto;
import com.sooum.core.domain.card.entity.Card;
import com.sooum.core.domain.card.entity.CommentCard;
import com.sooum.core.domain.card.entity.FeedCard;
import com.sooum.core.domain.img.service.ImgService;
import com.sooum.core.domain.member.service.MemberInfoService;
import com.sooum.core.domain.tag.service.TagService;
import com.sooum.core.global.exceptionmessage.ExceptionMessage;
import com.sooum.core.global.util.DistanceUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class DetailFeedService {
    private final FeedCardService feedCardService;
    private final ImgService imgService;
    private final TagService tagService;
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
        if (card instanceof FeedCard) {
            return FeedDetailCardDto.builder()
                    .id(card.getPk().toString())
                    .backgroundImgUrl(imgService.findImgUrl(card.getImgType(), card.getImgName()))
                    .isStory(((FeedCard) card).isStory())
                    .createdAt(card.getCreatedAt())
                    .content(card.getContent())
                    .distance(DistanceUtils.calculate(card.getLocation(), latitude, longitude))
                    .font(card.getFont())
                    .fontSize(card.getFontSize())
                    .isOwnCard(memberPk.equals(card.getWriter().getPk()))
                    .member(memberInfoService.getDefaultMember(card.getWriter()))
                    .tags(tagService.readTags(card))
                    .build();
        }
        if (card instanceof CommentCard commentCard) {
            return CommentDetailCardDto.builder()
                    .id(card.getPk().toString())
                    .backgroundImgUrl(imgService.findImgUrl(card.getImgType(), card.getImgName()))
                    .createdAt(card.getCreatedAt())
                    .content(card.getContent())
                    .distance(DistanceUtils.calculate(card.getLocation(), latitude, longitude))
                    .font(card.getFont())
                    .fontSize(card.getFontSize())
                    .isOwnCard(memberPk.equals(card.getWriter().getPk()))
                    .member(memberInfoService.getDefaultMember(card.getWriter()))
                    .tags(tagService.readTags(card))
                    .previousCardId(feedService.findParentCard(commentCard).getPk().toString())
                    .previousCardImgLink(imgService.findImgUrl(feedService.findParentCard(commentCard).getImgType(),feedService.findParentCard(commentCard).getImgName()))
                    .build();
        }
        throw new IllegalArgumentException(ExceptionMessage.UNHANDLED_OBJECT.getMessage());
    }
}