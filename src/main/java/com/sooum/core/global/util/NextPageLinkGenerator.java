package com.sooum.core.global.util;

import com.sooum.core.domain.card.controller.DistanceCardController;
import com.sooum.core.domain.card.controller.LatestFeedController;
import com.sooum.core.domain.card.dto.DistanceCardDto;
import com.sooum.core.domain.card.dto.FeedCardDto;
import com.sooum.core.domain.card.dto.LatestFeedCardDto;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;

import java.util.List;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

public abstract class NextPageLinkGenerator {
    public static <E extends FeedCardDto> Link generateNextPageLink  (List<E> feedCardInfoList) {
        if (feedCardInfoList.isEmpty()) {
            return Link.of("Not found");
        }
        int lastIdx = feedCardInfoList.size() - 1;
        long lastCardIdx = feedCardInfoList.get(lastIdx).getId();
        if (feedCardInfoList.get(0) instanceof LatestFeedCardDto) {
            return WebMvcLinkBuilder.linkTo(
                    methodOn(LatestFeedController.class).getClass()
            ).slash("/latest/"+lastCardIdx).withRel("next");
        }
        if (feedCardInfoList.get(0) instanceof DistanceCardDto) {
            return WebMvcLinkBuilder.linkTo(methodOn(DistanceCardController.class).getClass()
            ).slash("/"+lastCardIdx).withRel("next");
        }
        return Link.of("Not found");
    }

    public static <E extends FeedCardDto> List<E> appendEachCardDetailLink(List<E> feedCardInfoList) {
        if (feedCardInfoList.isEmpty()) {
            return feedCardInfoList;
        }
        if (feedCardInfoList.get(0) instanceof LatestFeedCardDto) {
            return feedCardInfoList.stream()
                    .peek(feedCard -> feedCard.add(WebMvcLinkBuilder.linkTo(LatestFeedController.class)
                            .slash("/detail/" + feedCard.getId())
                            .withRel("detail"))).toList();
        }
        if (feedCardInfoList.get(0) instanceof DistanceCardDto) {
            return feedCardInfoList.stream()
                    .peek(feedCard -> feedCard.add(WebMvcLinkBuilder.linkTo(DistanceCardController.class)
                            .slash("/" + feedCard.getId())
                            .withRel("detail"))).toList();
        }
        return feedCardInfoList;
    }
}