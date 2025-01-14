package com.sooum.data.tag.service;

import com.sooum.data.card.entity.Card;
import com.sooum.data.card.entity.CommentCard;
import com.sooum.data.card.entity.FeedCard;
import com.sooum.data.common.deactivatewords.DeactivateWords;
import com.sooum.data.tag.entity.FavoriteTag;
import com.sooum.data.tag.entity.Tag;
import com.sooum.data.tag.repository.CommentTagRepository;
import com.sooum.data.tag.repository.FavoriteTagRepository;
import com.sooum.data.tag.repository.FeedTagRepository;
import com.sooum.data.tag.repository.TagRepository;
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
    }

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
        return tagRepository.findRecommendTagList(excludeTags, DeactivateWords.deactivateWordsList, PageRequest.ofSize(10));
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
                .orElseThrow(() -> new EntityNotFoundException("태그를 찾을 수 없습니다."));
    }

    public FavoriteTag findFavoriteTag(Long tagPk, Long memberPk) {
        return favoriteTagRepository.findByTag_PkAndMember_Pk(tagPk, memberPk)
                .orElseThrow(() -> new EntityNotFoundException("태그를 찾을 수 없습니다."));
    }

    @Transactional
    public void deleteFavoriteTag(FavoriteTag favoriteTag) {
        favoriteTagRepository.delete(favoriteTag);
    }
}
