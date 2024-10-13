package com.sooum.core.domain.tag.service;

import com.sooum.core.domain.block.service.BlockMemberService;
import com.sooum.core.domain.card.service.FeedCardService;
import com.sooum.core.domain.img.service.ImgService;
import com.sooum.core.domain.member.service.MemberService;
import com.sooum.core.domain.tag.dto.TagDto;
import com.sooum.core.domain.tag.entity.FavoriteTag;
import com.sooum.core.domain.tag.entity.FeedTag;
import com.sooum.core.domain.tag.entity.Tag;
import com.sooum.core.domain.tag.repository.FavoriteTagRepository;
import com.sooum.core.global.exceptionmessage.ExceptionMessage;
import com.sooum.core.global.util.NextPageLinkGenerator;
import jakarta.persistence.EntityExistsException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class FavoriteTagService {
    private final FavoriteTagRepository favoriteTagRepository;
    private final TagService tagService;
    private final MemberService memberService;
    private final ImgService imgService;
    private final FeedCardService feedCardService;
    private final BlockMemberService blockMemberService;

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

    public List<TagDto.FavoriteTag> findMyFavoriteTags(Long memberPk, Long lastTagPk) {
        List<Long> favoriteTagIds = findMyFavoriteTagIds(memberPk, lastTagPk);

        List<Long> allBlockToPk = blockMemberService.findAllBlockToPk(memberPk);
        List<Long> blockedFeedCardIds = feedCardService.findFeedCardIdsByMemberPk(allBlockToPk);
        List<FeedTag> top5FeedCardsByMemberPk = tagService.findTop5FeedCardsByMemberPk(favoriteTagIds, blockedFeedCardIds);

        List<TagDto.FavoriteTag> favoriteTagList = favoriteTagIds.stream()
                .map(tagId -> createFavoriteTag(tagId, top5FeedCardsByMemberPk))
                .toList();

        return NextPageLinkGenerator.appendEachFavoriteTagDetailLink(favoriteTagList);
    }

    private TagDto.FavoriteTag createFavoriteTag(Long tagId, List<FeedTag> top5FeedCards) {
        List<FeedTag> relatedFeedTags = top5FeedCards.stream()
                .filter(feedTag -> feedTag.getTag().getPk().equals(tagId))
                .toList();

        List<TagDto.FavoriteTag.PreviewCard> previewCards = relatedFeedTags.stream()
                .map(this::createPreviewCard)
                .toList();

        Tag tag = relatedFeedTags.get(0).getTag();
        return TagDto.FavoriteTag.builder()
                .id(tag.getPk().toString())
                .tagContent(tag.getContent())
                .tagUsageCnt(String.valueOf(tag.getCount()))
                .previewCards(NextPageLinkGenerator.appendEachPreviewCardDetailLink(previewCards))
                .build();
    }

    private TagDto.FavoriteTag.PreviewCard createPreviewCard(FeedTag feedTag) {
        return TagDto.FavoriteTag.PreviewCard.builder()
                .id(feedTag.getFeedCard().getPk().toString())
                .content(feedTag.getFeedCard().getContent())
                .backgroundImgUrl(imgService.findImgUrl(
                        feedTag.getFeedCard().getImgType(),
                        feedTag.getFeedCard().getImgName()))
                .build();
    }

    public List<Long> findMyFavoriteTagIds(Long memberPk, Long lastTagPk) {
        List<Long> resultTagIds = new ArrayList<>();

        while (resultTagIds.size() < 20) {
            List<Long> favoriteTagIds = tagService.findTagIdsByLastId(lastTagPk, memberPk);

            if (!favoriteTagIds.isEmpty()) {
                lastTagPk = favoriteTagIds.get(favoriteTagIds.size() - 1);
            }

            resultTagIds.addAll(favoriteTagIds);
            if (isEndOfPage(favoriteTagIds)) {
                break;
            }
        }
        return resultTagIds.size() > 20 ? resultTagIds.subList(0, 20) : resultTagIds;
    }
    private static boolean isEndOfPage(List<Long> byLastId) {
        return byLastId.size() < 20;
    }
}