package com.sooum.api.tag.service;

import com.sooum.api.img.service.ImgService;
import com.sooum.api.tag.dto.TagDto;
import com.sooum.data.block.service.BlockMemberService;
import com.sooum.data.tag.entity.FeedTag;
import com.sooum.data.tag.entity.Tag;
import com.sooum.data.tag.repository.FavoriteTagRepository;
import com.sooum.data.tag.service.TagService;
import com.sooum.global.util.NextPageLinkGenerator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class FavoriteTagUseCase {
    private final BlockMemberService blockMemberService;
    private final ImgService imgService;
    private final TagService tagService;

    public List<TagDto.FavoriteTag> findMyFavoriteTags(Long memberPk, Long lastTagPk) {
        List<Long> favoriteTagPks = findMyFavoriteTagPks(memberPk, lastTagPk);

        List<Long> allBlockToPk = blockMemberService.findAllBlockToPk(memberPk);
        List<FeedTag> top5FeedCardsByMemberPk = tagService.findTop5FeedCardsByMemberPk(favoriteTagPks, allBlockToPk);

        List<TagDto.FavoriteTag> favoriteTagList = favoriteTagPks.stream()
                .map(tagPk -> createFavoriteTag(tagPk, top5FeedCardsByMemberPk))
                .filter(Objects::nonNull)
                .toList();

        return NextPageLinkGenerator.appendEachFavoriteTagDetailLink(favoriteTagList);
    }

    private TagDto.FavoriteTag createFavoriteTag(Long tagPk, List<FeedTag> top5FeedCards) {
        List<FeedTag> relatedFeedTags = top5FeedCards.stream()
                .filter(feedTag -> feedTag.getTag().getPk().equals(tagPk))
                .toList();

        if (relatedFeedTags.isEmpty()) {
            return null;
        }

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

    public List<Long> findMyFavoriteTagPks(Long memberPk, Long lastTagPk) {
        List<Long> resultTagPks = new ArrayList<>();
        while (resultTagPks.size() < 20) {

            List<Long> favoriteTagPks = tagService.findTagPksByLastPk(lastTagPk, memberPk);

            if (!favoriteTagPks.isEmpty()) {
                lastTagPk = favoriteTagPks.get(favoriteTagPks.size() - 1);
            }

            resultTagPks.addAll(favoriteTagPks);
            if (isEndOfPage(favoriteTagPks)) {
                break;
            }
        }
        return resultTagPks.size() > 20 ? resultTagPks.subList(0, 20) : resultTagPks;
    }
    private static boolean isEndOfPage(List<Long> tagPks) {
        return tagPks.size() < 20;
    }
}
