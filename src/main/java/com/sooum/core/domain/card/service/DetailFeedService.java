package com.sooum.core.domain.card.service;

import com.sooum.core.domain.card.dto.FeedDetailCardDto;
import com.sooum.core.domain.card.entity.FeedCard;
import com.sooum.core.domain.img.service.ImgService;
import com.sooum.core.domain.member.service.MemberInfoService;
import com.sooum.core.domain.tag.service.FeedTagService;
import com.sooum.core.global.util.DistanceUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.hateoas.Link;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class DetailFeedService {
    private final FeedCardService feedCardService;
    private final FeedLikeService feedLikeService;
    private final ImgService imgService;
    private final FeedTagService feedTagService;
    private final MemberInfoService memberInfoService;

    @Transactional
    public FeedDetailCardDto findDetailFeedCard (Long cardPk, Long memberPk, Optional<Double> latitude, Optional<Double> longitude) {
        FeedCard feedCard = feedCardService.findFeedCard(cardPk);

        FeedDetailCardDto.DetailFeedCard detailCard = createDetailCardDto(feedCard, memberPk, latitude, longitude);

        return FeedDetailCardDto.builder()
                .detailFeedCard(detailCard)
                .tags(feedTagService.readTags(feedCard))
                .member(memberInfoService.getDefaultMember(feedCard.getWriter())).build();
    }

    private FeedDetailCardDto.DetailFeedCard createDetailCardDto(FeedCard feedCard, Long memberPk, Optional<Double> latitude, Optional<Double> longitude) {
        return FeedDetailCardDto.DetailFeedCard.builder()
                .id(feedCard.getPk())
                .font(feedCard.getFont())
                .content(feedCard.getContent())
                .isStory(feedCard.isStory())
                .distance(DistanceUtils.calculate(feedCard.getLocation(), latitude, longitude))
                .backgroundImgUrl(Link.of(imgService.findImgUrl(feedCard.getImgType(),feedCard.getImgName())))
                .createdAt(feedCard.getCreatedAt())
                .isLiked(feedLikeService.hasLiked(feedCard.getPk(), memberPk))
                .likeCnt(feedLikeService.getLikeCount(feedCard.getPk()))
                .isOwnCard(memberPk.equals(feedCard.getWriter().getPk()))
                .build();
    }
}