package com.sooum.core.domain.tag.service;

import com.sooum.core.domain.card.entity.FeedCard;
import com.sooum.core.domain.img.service.ImgService;
import com.sooum.core.domain.member.service.MemberService;
import com.sooum.core.domain.tag.dto.TagDto;
import com.sooum.core.domain.tag.entity.FavoriteTag;
import com.sooum.core.domain.tag.entity.Tag;
import com.sooum.core.domain.tag.repository.FavoriteTagRepository;
import com.sooum.core.global.exceptionmessage.ExceptionMessage;
import com.sooum.core.global.util.NextPageLinkGenerator;
import jakarta.persistence.EntityExistsException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class FavoriteTagService {
    private final FavoriteTagRepository favoriteTagRepository;
    private final TagService tagService;
    private final MemberService memberService;
    private final FeedTagService feedTagService;
    private final ImgService imgService;

    @Transactional
    public void saveFavoriteTag(Long tagPk, Long memberPk) {
        if (tagService.isExistFavoriteTag(tagPk, memberPk)) {
            throw new EntityExistsException(ExceptionMessage.ALREADY_TAG_FAVORITE.getMessage());
        }

        FavoriteTag favoriteTag = FavoriteTag.builder()
                .tag(tagService.findTag(tagPk))
                .member(memberService.findByPk(memberPk))
                .build();
        tagService.saveFavoriteTag(favoriteTag);
    }


    List<Tag> findFavoriteTags(Long memberPk) {
        return favoriteTagRepository.findFavoriteTag(memberPk);
    }

    @Transactional
    public void deleteFavoriteTag(Long tagPk, Long memberPk) {
        FavoriteTag findFavoriteTag = tagService.findFavoriteTag(tagPk, memberPk);
        tagService.deleteFavoriteTag(findFavoriteTag);
    }


    public List<TagDto.FavoriteTag> findMyFavoriteTags (Long memberPk) {
        List<Tag> favoriteTags = findFavoriteTags(memberPk);
        List<TagDto.FavoriteTag> favoriteTagList = favoriteTags.stream()
                .map(tag -> {
                    List<FeedCard> top5ByTagPk = feedTagService.findTop5ByTagPk(tag.getPk());
                    List<TagDto.FavoriteTag.PreviewCard> previewCards = top5ByTagPk.stream()
                            .map(card -> TagDto.FavoriteTag.PreviewCard.builder()
                                    .id(card.getPk().toString())
                                    .content(card.getContent())
                                    .backgroundImgUrl(imgService.findImgUrl(card.getImgType(), card.getImgName()))
                                    .build())
                            .toList();
                    List<TagDto.FavoriteTag.PreviewCard> previewCardsWithLinks = NextPageLinkGenerator.appendEachPreviewCardDetailLink(previewCards);

                    return TagDto.FavoriteTag.builder()
                            .id(tag.getPk().toString())
                            .tagContent(tag.getContent())
                            .tagUsageCnt(String.valueOf(tag.getCount()))
                            .previewCards(previewCardsWithLinks)
                            .build();
                }).toList();

        return NextPageLinkGenerator.appendEachFavoriteTagDetailLink(favoriteTagList);
    }
}
