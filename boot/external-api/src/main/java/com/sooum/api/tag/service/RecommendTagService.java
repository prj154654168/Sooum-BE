package com.sooum.api.tag.service;

import com.sooum.api.tag.dto.TagDto;
import com.sooum.data.tag.entity.Tag;
import com.sooum.data.tag.service.FavoriteTagService;
import com.sooum.data.tag.service.TagService;
import com.sooum.global.util.NextPageLinkGenerator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RecommendTagService {
    private final FavoriteTagService favoriteTagService;
    private final TagService tagService;

    public List<TagDto.RecommendTag> findRecommendedTags(Long memberPk) {
        List<Tag> favoriteTags = favoriteTagService.findFavoriteTags(memberPk);
        List<TagDto.RecommendTag> recommendTagList = tagService.findRecommendTags(favoriteTags).stream()
                .map(tag -> TagDto.RecommendTag.builder()
                        .tagId(String.valueOf(tag.getPk()))
                        .tagContent(tag.getContent())
                        .tagUsageCnt(String.valueOf(tag.getCount()))
                        .build())
                .toList();
        return NextPageLinkGenerator.appendEachRecommendTagDetailLink(recommendTagList);
    }
}
