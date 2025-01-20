package com.sooum.api.tag.service;

import com.sooum.api.tag.dto.TagDto;
import com.sooum.data.card.entity.Card;
import com.sooum.data.tag.entity.Tag;
import com.sooum.data.tag.service.FavoriteTagService;
import com.sooum.data.tag.service.TagService;
import com.sooum.global.util.NextPageLinkGenerator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TagUseCase {
    private final TagService tagService;
    private final FavoriteTagService favoriteTagService;

    public List<TagDto.RelatedTag> findRelatedTags(String keyword, Integer size) {
        return tagService.findRelatedTags(keyword, size)
                .stream()
                .map(tag -> new TagDto.RelatedTag(tag.getPk().toString(), tag.getCount(), tag.getContent()))
                .toList();
    }

    public TagDto.TagSummary createTagSummary(Long tagPk, Long memberPk) {
        return TagDto.TagSummary.builder()
                .content(tagService.findTag(tagPk).getContent())
                .cardCnt(tagService.findTag(tagPk).getCount())
                .isFavorite(favoriteTagService.isExistsByTagPkAndMemberPk(tagPk, memberPk))
                .build();
    }

    public List<TagDto.ReadTagResponse> readTags(Card card) {
        List<Tag> tagsByFeedCard = tagService.getTagsByCard(card);
        return NextPageLinkGenerator.appendEachTagDetailLink(
                tagsByFeedCard.stream()
                        .map(tag -> TagDto.ReadTagResponse.builder()
                                .id(tag.getPk().toString())
                                .content(tag.getContent())
                                .build())
                        .toList());
    }
}
