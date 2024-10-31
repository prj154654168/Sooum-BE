package com.sooum.core.global.util;

import com.sooum.core.domain.card.controller.DistanceCardController;
import com.sooum.core.domain.card.controller.FeedCardController;
import com.sooum.core.domain.card.controller.LatestFeedController;
import com.sooum.core.domain.card.dto.*;
import com.sooum.core.domain.follow.dto.FollowDto;
import com.sooum.core.domain.follow.dto.FollowInfoDto;
import com.sooum.core.domain.member.controller.MemberController;
import com.sooum.core.domain.member.controller.ProfileController;
import com.sooum.core.domain.card.dto.MyCardDto;
import com.sooum.core.domain.tag.dto.TagDto;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;

import java.util.List;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

public abstract class NextPageLinkGenerator {
    public static <E extends CardDto> Link generateNextPageLink  (List<E> feedCardInfoList) {
        if (feedCardInfoList.isEmpty()) {
            return Link.of("Not found");
        }
        int lastIdx = feedCardInfoList.size() - 1;
        String lastCardIdx = feedCardInfoList.get(lastIdx).getId();
        E cardDto = feedCardInfoList.get(0);
        if (cardDto instanceof LatestFeedCardDto) {
            return linkTo(
                    methodOn(LatestFeedController.class).getClass()
            ).slash("/latest/"+lastCardIdx).withRel("next");
        }
        if (cardDto instanceof DistanceCardDto) {
            return linkTo(methodOn(DistanceCardController.class).getClass()
            ).slash("/"+lastCardIdx).withRel("next");
        }
        return Link.of("Not found");
    }

    public static <E extends FollowInfoDto> Link generateFollowInfoNextPageLink (List<E> followInfos, Long profileOwnerPk) {
        int lastIdx = followInfos.size() - 1;
        E lastFollowDto = followInfos.get(lastIdx);
        String lastFollowPk = lastFollowDto.getId();
        if (lastFollowDto instanceof FollowDto.FollowerInfo) {
            return linkTo(methodOn(ProfileController.class).getClass())
                    .slash("/ " + profileOwnerPk + "/follower?followerLastId=" + lastFollowPk)
                    .withRel("next");
        }
        if (lastFollowDto instanceof FollowDto.FollowingInfo) {
            return linkTo(methodOn(ProfileController.class).getClass())
                    .slash("/ " + profileOwnerPk + "/following?followingLastId=" + lastFollowPk)
                    .withRel("next");
        }

        return Link.of("Not found");
    }

    public static <E extends CardDto> List<E> appendEachCardDetailLink(List<E> feedCardInfoList) {
        if (feedCardInfoList.isEmpty()) {
            return feedCardInfoList;
        }

        return feedCardInfoList.stream()
                .peek(feedCard -> feedCard.add(linkTo(FeedCardController.class)
                        .slash("/" + feedCard.getId() + "/detail")
                        .withRel("detail"))).toList();
    }

    public static <T extends TagDto.ReadTagResponse> List<T> appendEachTagDetailLink(List<T> tagDtoList) {
        if (tagDtoList.isEmpty()) {
            return tagDtoList;
        }
        return tagDtoList.stream()
                .peek(tag -> tag.add(linkTo(FeedCardController.class)
                        .slash("/tags/"+  tag.getId())
                        .withRel("tag-feed"))).toList();
    }

    public static <T extends TagDto.RecommendTag> List<T> appendEachRecommendTagDetailLink(List<T> tagDtoList) {
        if (tagDtoList.isEmpty()) {
            return tagDtoList;
        }
        return tagDtoList.stream()
                .peek(tag -> tag.add(linkTo(FeedCardController.class)
                        .slash("/tags/"+ tag.getTagId())
                        .withRel("tag-feed"))).toList();
    }

    public static <T extends TagDto.FavoriteTag> List<T> appendEachFavoriteTagDetailLink(List<T> tagDtoList) {
        if (tagDtoList.isEmpty()) {
            return tagDtoList;
        }
        return tagDtoList.stream()
                .peek(tag -> tag.add(linkTo(FeedCardController.class)
                        .slash("/tags/"+ tag.getId())
                        .withRel("tag-feed"))).toList();
    }

    public static <E extends TagDto.FavoriteTag.PreviewCard> List<E> appendEachPreviewCardDetailLink(List<E> cardDtoList) {
        if (cardDtoList.isEmpty()) {
            return cardDtoList;
        }

        return cardDtoList.stream()
                .peek(previewCard -> previewCard.add(linkTo(FeedCardController.class)
                        .slash("/" + previewCard.getId() + "/detail")
                        .withRel("detail"))).toList();
    }

    public static <E extends FollowInfoDto> List<E> appendEachProfileLink(List<E> followInfo) {
        if (followInfo.isEmpty()) {
            return followInfo;
        }

        return followInfo.stream()
                .peek(follow -> follow.add(linkTo(ProfileController.class)
                        .slash("/" + follow.getId())
                        .withRel("profile"))).toList();
    }

    public static <E extends MyCardDto> List<E> appendEachMyCardDetailLink(List<E> feedCardInfoList) {
        if (feedCardInfoList.isEmpty()) {
            return feedCardInfoList;
        }

        return feedCardInfoList.stream()
                .peek(feedCard -> feedCard.add(WebMvcLinkBuilder.linkTo(FeedCardController.class)
                        .slash("/" + feedCard.getId() + "/detail")
                        .withRel("detail"))).toList();
    }

    public static <E extends MyCardDto> Link generateMyCardNextPageLink (List<E> myCardDtoList) {
        if (myCardDtoList.isEmpty()) {
            return Link.of("Not found");
        }

        int lastIdx = myCardDtoList.size() - 1;
        String lastCardIdx = myCardDtoList.get(lastIdx).getId();
        E myCardDto = myCardDtoList.get(0);

        if (myCardDto instanceof MyCommentCardDto) {
            return linkTo(methodOn(MemberController.class).getClass())
                    .slash("/comment-cards?lastId=" +lastCardIdx)
                    .withRel("next");
        }
        return Link.of("Not found");
    }
}