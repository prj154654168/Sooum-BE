package com.sooum.core.domain.card.service;

import com.sooum.core.domain.card.dto.PopularCardDto;
import com.sooum.core.domain.card.entity.PopularFeed;
import com.sooum.core.domain.card.repository.PopularFeedRepository;
import com.sooum.core.domain.img.service.ImgService;
import com.sooum.core.domain.member.entity.Member;
import com.sooum.core.domain.member.service.MemberService;
import com.sooum.core.global.util.DistanceUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.hateoas.Link;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PopularFeedService {
    private final MemberService memberService;
    private final PopularFeedRepository popularFeedRepository;
    private final ImgService imgService;

    public List<PopularCardDto.PopularCardRetrieve> findHomePopularFeeds (final Long last,
                                                                          final Long memberPk,
                                                                          Double latitude,
                                                                          Double longitude) {
        Member request = memberService.findByPk(memberPk);
        PageRequest pageRequest = PageRequest.of(0, 100);
        List<PopularFeed> cards = popularFeedRepository.findPopularFeeds(last, pageRequest);

//        List<PopularCardDto.PopularCardRetrieve> responseData = new ArrayList<>();
//        cards.stream()
//                .forEach(card -> responseData.add(PopularCardDto.PopularCardRetrieve.builder()
//                        .id(card.getPopularCard().getPk())
//                        .contents(card.getPopularCard().getContent())
//                        .isStory(card.getPopularCard().isStory())
//                        .backgroundImgUrl(Link.of(imgService.findImgUrl(card.getPopularCard().getImgType(), card.getPopularCard().getImgName())))
//                        .font(card.getPopularCard().getFont())
//                        .fontSize(card.getPopularCard().getFontSize())
//                        .distance(DistanceUtils.calculate(card.getPopularCard().getLocation(), latitude, longitude))
//                        .createdAt(card.getPopularCard().getCreatedAt())
//                        .isLiked()
//                        .likeCnt()
//                        .isCommentWritten()
//                        .commentCnt()
//                        .popularityType(card.getPopularityType())
//                        .build()));
//
//        return responseData;
        return null;
    }
}
