package com.sooum.api.tag.service;

import com.sooum.api.img.service.ImgService;
import com.sooum.api.tag.dto.TagDto;
import com.sooum.data.block.service.BlockMemberService;
import com.sooum.data.card.entity.FeedCard;
import com.sooum.data.tag.entity.FeedTag;
import com.sooum.data.tag.entity.Tag;
import com.sooum.data.tag.service.FavoriteTagService;
import com.sooum.data.tag.service.FeedTagService;
import com.sooum.global.util.NextPageLinkGenerator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class FavoriteTagUseCase {
    private final BlockMemberService blockMemberService;
    private final ImgService imgService;
    private final FeedTagService feedTagService;
    private final FavoriteTagService favoriteTagService;
    private static final int MAX_PAGE_SIZE = 40;
    private static final int DEFAULT_PAGE_SIZE = 20;

    public List<TagDto.FavoriteTag> findTop5FeedByFavoriteTags(Long memberPk, Optional<Long> lastTagPk) {
        List<Long> blockedMemberPks = blockMemberService.findAllBlockMemberPks(memberPk);
        List<Long> myFavoriteTagPks = favoriteTagService.findMyFavoriteTags(memberPk, lastTagPk, PageRequest.ofSize(MAX_PAGE_SIZE));

        List<FeedTag> fullyLoadedFeedTags = findAndLoadTopFeedTags(myFavoriteTagPks,blockedMemberPks);
        Map<Tag, List<FeedTag>> feedTagsGroupedByTag = fullyLoadedFeedTags.stream().collect(Collectors.groupingBy(FeedTag::getTag, LinkedHashMap::new, Collectors.toList()));

        List<TagDto.FavoriteTag> favoriteTagDtoList = feedTagsGroupedByTag.entrySet().stream()
                .limit(DEFAULT_PAGE_SIZE)
                .map(entry -> generateFavoriteTagDto(entry.getKey(), entry.getValue()))
                .toList();

        return NextPageLinkGenerator.appendEachFavoriteTagDetailLink(favoriteTagDtoList);
    }

    private List<FeedTag> findAndLoadTopFeedTags(List<Long> favoriteTagPks, List<Long> blockedMemberPks) {
        List<FeedTag> proxyFeedTags = feedTagService.findTop5FeedTags(favoriteTagPks, blockedMemberPks);
        return feedTagService.findLoadFeedTagsIn(proxyFeedTags);
    }

    private TagDto.FavoriteTag generateFavoriteTagDto(Tag tag, List<FeedTag> feedTagList) {
        List<FeedCard> feedCardList = feedTagList.stream()
                .map(FeedTag::getFeedCard)
                .toList();

        List<TagDto.FavoriteTag.PreviewCard> previewCardDtoList = feedCardList.stream()
                .map(this::generatePreviewCardDto)
                .toList();

        return TagDto.FavoriteTag.builder()
                .id(tag.getPk().toString())
                .tagContent(tag.getContent())
                .tagUsageCnt(String.valueOf(tag.getCount()))
                .previewCards(NextPageLinkGenerator.appendEachPreviewCardDetailLink(previewCardDtoList))
                .build();
    }

    private TagDto.FavoriteTag.PreviewCard generatePreviewCardDto(FeedCard feedCard) {
        return TagDto.FavoriteTag.PreviewCard.builder()
                .id(feedCard.getPk().toString())
                .content(feedCard.getContent())
                .backgroundImgUrl(imgService.findCardImgUrl(
                        feedCard.getImgType(),
                        feedCard.getImgName()))
                .build();
    }
}