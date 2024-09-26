package com.sooum.core.global.util;

import com.sooum.core.domain.card.controller.DistanceCardController;
import com.sooum.core.domain.card.controller.FeedCardController;
import com.sooum.core.domain.card.controller.LatestFeedController;
import com.sooum.core.domain.card.dto.CardDto;
import com.sooum.core.domain.card.dto.DistanceCardDto;
import com.sooum.core.domain.card.dto.LatestFeedCardDto;
import com.sooum.core.domain.tag.controller.TagController;
import com.sooum.core.domain.tag.dto.TagDto;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.RepresentationModel;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;

import java.util.List;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

public abstract class NextPageLinkGenerator {
    public static <E extends CardDto> Link generateNextPageLink  (List<E> feedCardInfoList) {
        if (feedCardInfoList.isEmpty()) {
            return Link.of("Not found");
        }
        int lastIdx = feedCardInfoList.size() - 1;
        long lastCardIdx = feedCardInfoList.get(lastIdx).getId();
        E cardDto = feedCardInfoList.get(0);
        if (cardDto instanceof LatestFeedCardDto) {
            return WebMvcLinkBuilder.linkTo(
                    methodOn(LatestFeedController.class).getClass()
            ).slash("/latest/"+lastCardIdx).withRel("next");
        }
        if (cardDto instanceof DistanceCardDto) {
            return WebMvcLinkBuilder.linkTo(methodOn(DistanceCardController.class).getClass()
            ).slash("/"+lastCardIdx).withRel("next");
        }
        return Link.of("Not found");
    }

    public static <E extends CardDto> List<E> appendEachCardDetailLink(List<E> feedCardInfoList) {
        if (feedCardInfoList.isEmpty()) {
            return feedCardInfoList;
        }

        return feedCardInfoList.stream()
                .peek(feedCard -> feedCard.add(WebMvcLinkBuilder.linkTo(FeedCardController.class)
                        .slash("/detail/" + feedCard.getId())
                        .withRel("detail"))).toList();
    }

    public static <T extends RepresentationModel<T>> List<T> appendEachTagDetailLink(List<T> tagDtoList) {
        if (tagDtoList.isEmpty()) {
            return tagDtoList;
        }
        return tagDtoList.stream()
                .peek(tag -> tag.add(WebMvcLinkBuilder.linkTo(TagController.class)
                        .slash("/"+  ((TagDto.ReadTagResponse) tag).getId())
                        .withRel("tag-feed"))).toList();
    }
}