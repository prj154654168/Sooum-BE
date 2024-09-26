package com.sooum.core.domain.card.service;

import com.sooum.core.domain.card.dto.DetailCardDto;
import com.sooum.core.domain.card.entity.FeedCard;
import com.sooum.core.domain.img.service.ImgService;
import com.sooum.core.domain.member.entity.Member;
import com.sooum.core.domain.member.service.MemberInfoService;
import com.sooum.core.domain.member.service.MemberService;
import com.sooum.core.domain.tag.service.FeedTagService;
import com.sooum.core.global.util.DistanceUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.hateoas.Link;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class DetailFeedService {
    private final FeedCardService feedCardService;
    private final FeedLikeService feedLikeService;
    private final ImgService imgService;
    private final MemberService memberService;
    private final FeedTagService feedTagService;
    private final MemberInfoService memberInfoService;

    public DetailCardDto.DetailCardRetrieve findDetailFeedCard (Long cardPk, Long memberPk, Optional<Double> latitude, Optional<Double> longitude) {
        FeedCard feedCard = feedCardService.findFeedCard(cardPk);
        Member member = memberService.findByPk(memberPk);

        DetailCardDto.DetailCard detailCard = createDetailCardDto(feedCard, memberPk, latitude, longitude);

        return DetailCardDto.DetailCardRetrieve.builder()
                .detailCard(detailCard)
                .tags(feedTagService.readTags(feedCard))
                .member(memberInfoService.getDefaultMember(member)).build();
    }

    private DetailCardDto.DetailCard createDetailCardDto(FeedCard feedCard, Long memberPk, Optional<Double> latitude, Optional<Double> longitude) {
        return DetailCardDto.DetailCard.builder()
                .id(feedCard.getPk())
                .font(feedCard.getFont())
                .fontSize(feedCard.getFontSize())
                .content(feedCard.getContent())
                .isStory(feedCard.isStory())
                .storyExpiredTime(feedCard.getCreatedAt().plusDays(1L))
                .distance(DistanceUtils.calculate(feedCard.getLocation(), latitude, longitude))
                .backgroundImgUrl(Link.of(imgService.findImgUrl(feedCard.getImgType(),feedCard.getImgName())))
                .createdAt(feedCard.getCreatedAt())
                .isLiked(feedLikeService.hasLiked(feedCard.getPk(), memberPk))
                .likeCnt(feedLikeService.getLikeCount(feedCard.getPk()))
                .isOwnCard(memberPk.equals(feedCard.getWriter().getPk()))
                .build();
    }
}