package com.sooum.core.global.util;

import com.sooum.core.domain.card.controller.LatestFeedController;
import com.sooum.core.domain.card.dto.FeedCardDto;
import com.sooum.core.domain.card.dto.LatestFeedCardDto;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;

import java.util.List;

public class NextPageLinkGenerator<E extends FeedCardDto> {
    public Link generateNextPageLink  (List<E> feedCardInfoList) {
        if (feedCardInfoList.isEmpty()) {
            return Link.of("Not found");
        }
        int lastIdx = feedCardInfoList.size() - 1;
        long lastCardIdx = feedCardInfoList.get(lastIdx).getId();
        if (feedCardInfoList.get(0) instanceof LatestFeedCardDto) {
            return WebMvcLinkBuilder.linkTo(
                    WebMvcLinkBuilder.methodOn(LatestFeedController.class).getClass()
            ).slash("/latest/"+lastCardIdx).withRel("next");
        }
        return Link.of("Not found");
    }

    public List<E> appendEachCardDetailLink(List<E> feedCardInfoList) {
        if (feedCardInfoList.isEmpty()) {
            return feedCardInfoList;
        }
        if (feedCardInfoList.get(0) instanceof LatestFeedCardDto) {
            return feedCardInfoList.stream()
                    .peek(feedCard -> feedCard.add(WebMvcLinkBuilder.linkTo(LatestFeedController.class)
                            .slash("/detail/" + feedCard.getId())
                            .withRel("detail"))).toList();
        }
        return feedCardInfoList;
    }
}
