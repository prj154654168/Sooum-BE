package com.sooum.data.tag.service;

import com.sooum.data.card.entity.Card;
import com.sooum.data.card.entity.CommentCard;
import com.sooum.data.card.entity.FeedCard;
import com.sooum.data.tag.entity.FavoriteTag;
import com.sooum.data.tag.entity.FeedTag;
import com.sooum.data.tag.entity.Tag;
import com.sooum.data.tag.repository.CommentTagRepository;
import com.sooum.data.tag.repository.FavoriteTagRepository;
import com.sooum.data.tag.repository.FeedTagRepository;
import com.sooum.data.tag.repository.TagRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
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
//    private final CachedTagRepository cachedTagRepository;
    private final FavoriteTagRepository favoriteTagRepository;


    public List<Tag> getTagsByCard(Card card) {
        if(card instanceof FeedCard feedCard){
            return feedTagRepository.findTagsByFeedCard(feedCard);
        }
        if(card instanceof CommentCard commentCard){
            return commentTagRepository.findTagsByCommentCard(commentCard);
        }
        throw new IllegalArgumentException();
    }

    public List<Tag> findRelatedTags(String keyword, Integer size) {
        return tagRepository.findByKeyword(keyword, PageRequest.of(0, size));

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

//    public void upsert(List<? extends CachedTag> cachedTags) {
//        cachedTagRepository.saveAll(cachedTags);
//    }

    public List<Tag> findTagList(List<String> tagContents) {
        return tagRepository.findTagList(tagContents);
    }

    public void incrementTagCount(List<Tag> tags){
        tagRepository.incrementTagCount(tags);
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
                .orElseThrow(EntityNotFoundException::new);
    }

    public FavoriteTag findFavoriteTag(Long tagPk, Long memberPk) {
        return favoriteTagRepository.findByTag_PkAndMember_Pk(tagPk, memberPk)
                .orElseThrow(EntityNotFoundException::new);
    }

    @Transactional
    public void deleteFavoriteTag(FavoriteTag favoriteTag) {
        favoriteTagRepository.delete(favoriteTag);
    }

    public List<FeedTag> findTop5FeedCardsByMemberPk(List<Long> favoriteTagIds, List<Long> blockedMemberPks) {
        return feedTagRepository.findTop5FeedCardsByMemberPk(favoriteTagIds, blockedMemberPks);
    }

    public List<Long> findTagPksByLastPk(Long lastTagPk, Long memberPk) {
        Pageable pageRequest = PageRequest.ofSize(20);
        if (isFirstPageRequest(lastTagPk)) {
            return favoriteTagRepository.findFirstPageTagPks(memberPk, pageRequest);
        }
        return favoriteTagRepository.findNextPageTagPks(memberPk, lastTagPk, pageRequest);
    }

    private static boolean isFirstPageRequest(Long lastTagPk) {
        return lastTagPk.equals(0L);
    }
}
