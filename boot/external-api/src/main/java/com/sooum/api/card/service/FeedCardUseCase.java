package com.sooum.api.card.service;

import com.sooum.api.card.dto.MyFeedCardDto;
import com.sooum.api.img.service.ImgService;
import com.sooum.data.card.entity.FeedCard;
import com.sooum.data.card.service.FeedCardService;
import com.sooum.global.util.NextPageLinkGenerator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class FeedCardUseCase {

    private final ImgService imgService;
    private final FeedCardService feedCardService;

    public List<MyFeedCardDto> findByMemberPk(Long memberPk, Optional<Long> lastId) {
        List<FeedCard> feedList = feedCardService.findFeedList(memberPk, lastId);

        return NextPageLinkGenerator.appendEachMyCardDetailLink(feedList.stream()
                .map(feed -> MyFeedCardDto.builder()
                        .id(feed.getPk().toString())
                        .content(feed.getContent())
                        .backgroundImgUrl(imgService.findImgUrl(feed.getImgType(), feed.getImgName()))
                        .font(feed.getFont())
                        .fontSize(feed.getFontSize())
                        .build())
                .toList());
    }
}
