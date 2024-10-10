package com.sooum.core.domain.tag.service;

import com.sooum.core.domain.card.entity.Card;
import com.sooum.core.domain.card.entity.CommentCard;
import com.sooum.core.domain.card.entity.FeedCard;
import com.sooum.core.domain.tag.dto.TagDto;
import com.sooum.core.domain.tag.entity.CachedTag;
import com.sooum.core.domain.tag.entity.FavoriteTag;
import com.sooum.core.domain.tag.entity.Tag;
import com.sooum.core.domain.tag.repository.*;
import com.sooum.core.global.exceptionmessage.ExceptionMessage;
import com.sooum.core.global.util.NextPageLinkGenerator;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TagService {
    private final FeedTagRepository feedTagRepository;
    private final CommentTagRepository commentTagRepository;
    private final TagRepository tagRepository;
    private final CachedTagRepository cachedTagRepository;
    private final FavoriteTagRepository favoriteTagRepository;

    public List<TagDto.ReadTagResponse> readTags(Card card) {
        List<Tag> tagsByFeedCard = getTagsByCard(card);
        return NextPageLinkGenerator.appendEachTagDetailLink(
                tagsByFeedCard.stream()
                        .map(tag -> TagDto.ReadTagResponse.builder()
                                .id(tag.getPk().toString())
                                .content(tag.getContent())
                                .build())
                        .toList());
    }

    public List<Tag> getTagsByCard(Card card) {
        if(card instanceof FeedCard feedCard){
            return feedTagRepository.findTagsByFeedCard(feedCard);
        }
        if(card instanceof CommentCard commentCard){
            return commentTagRepository.findTagsByCommentCard(commentCard);
        }
        throw new IllegalArgumentException(ExceptionMessage.UNHANDLED_OBJECT.getMessage());
    }

    public List<TagDto.RelatedTag> findRelatedTags(String keyword, Integer size) {
        return tagRepository.findByKeyword(keyword, PageRequest.of(0, size))
                .stream()
                .map(tag -> new TagDto.RelatedTag(tag.getCount(), tag.getContent()))
                .toList();

//        try {
//            return cachedTagRepository.findTop5ByContentLikeIgnoreCaseOrderByCountDesc(keyword)
//                    .stream()
//                    .map(tag -> new TagDto.RelatedTag(tag.getCount(), tag.getContent()))
//                    .toList();
//        } catch (Exception ignore) {
//            return tagRepository.findByKeyword(keyword, PageRequest.of(0, 5))
//                    .stream()
//                    .map(tag -> new TagDto.RelatedTag(tag.getCount(), tag.getContent()))
//                    .toList();
//        }
    }

    public void upsert(List<? extends CachedTag> cachedTags) {
        cachedTagRepository.saveAll(cachedTags);
    }

    public List<Tag> findTagList(List<String> tagContents) {
        return tagRepository.findTagList(tagContents);
    }

    @Transactional
    public void saveAll(List<Tag> tags) {
        tagRepository.saveAll(tags);
    }

    public List<Tag> findRecommendTags(List<Tag> excludeTags) {
        return tagRepository.findRecommendTagList(excludeTags, PageRequest.ofSize(10));
    }
    public boolean isExistFavoriteTag(Long tagPk, Long memberPk) {
        return favoriteTagRepository.existsByTag_PkAndMember_Pk(tagPk, memberPk);
    }

    @Transactional
    public void saveFavoriteTag(FavoriteTag favoriteTag) {
        favoriteTagRepository.save(favoriteTag);
    }

    public Tag findTag(Long tagPk) {
        return tagRepository.findById(tagPk)
                .orElseThrow(() -> new EntityNotFoundException(ExceptionMessage.TAG_NOT_FOUND.getMessage()));
    }

    public FavoriteTag findFavoriteTag(Long tagPk, Long memberPk) {
        return favoriteTagRepository.findByTag_PkAndMember_Pk(tagPk, memberPk)
                .orElseThrow(() -> new EntityNotFoundException(ExceptionMessage.FAVORITE_TAG_NOT_FOUND.getMessage()));
    }

    @Transactional
    public void deleteFavoriteTag(FavoriteTag favoriteTag) {
        favoriteTagRepository.delete(favoriteTag);
    }

    public TagDto.TagSummary createTagSummary(Long tagPk, Long memberPk) {
        return TagDto.TagSummary.builder()
                .content(findTag(tagPk).getContent())
                .cardCnt(feedTagRepository.countTagFeeds(tagPk))
                .isFavorite(favoriteTagRepository.existsByTag_PkAndMember_Pk(tagPk, memberPk))
                .build();
    }
}
