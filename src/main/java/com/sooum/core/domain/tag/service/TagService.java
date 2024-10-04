package com.sooum.core.domain.tag.service;

import com.sooum.core.domain.card.entity.Card;
import com.sooum.core.domain.card.entity.CommentCard;
import com.sooum.core.domain.card.entity.FeedCard;
import com.sooum.core.domain.tag.dto.TagDto;
import com.sooum.core.domain.tag.entity.CachedTag;
import com.sooum.core.domain.tag.entity.Tag;
import com.sooum.core.domain.tag.repository.CachedTagRepository;
import com.sooum.core.domain.tag.repository.CommentTagRepository;
import com.sooum.core.domain.tag.repository.FeedTagRepository;
import com.sooum.core.domain.tag.repository.TagRepository;
import com.sooum.core.global.exceptionmessage.ExceptionMessage;
import com.sooum.core.global.util.NextPageLinkGenerator;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TagService {
    private final FeedTagRepository feedTagRepository;
    private final CommentTagRepository commentTagRepository;
    private final TagRepository tagRepository;
    private final CachedTagRepository cachedTagRepository;

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

    public List<TagDto.RelatedTag> findRelatedTags(String keyword) {
        try {
            return cachedTagRepository.findTop5ByContentLikeIgnoreCaseOrderByCountDesc(keyword)
                    .stream()
                    .map(tag -> new TagDto.RelatedTag(tag.getCount(), tag.getContent()))
                    .toList();
        } catch (Exception ignore) {
            return tagRepository.findByKeyword(keyword, PageRequest.of(0, 5))
                    .stream()
                    .map(tag -> new TagDto.RelatedTag(tag.getCount(), tag.getContent()))
                    .toList();
        }
    }

    public void upsert() {
        List<CachedTag> cachedTags = tagRepository.findAllByIsActiveIsTrue().stream()
                .map(tag -> new CachedTag(tag.getContent(), tag.getCount()))
                .toList();

        cachedTagRepository.saveAll(cachedTags);
    }
}
