package com.sooum.core.domain.tag.service;

import com.sooum.core.domain.card.entity.Card;
import com.sooum.core.domain.card.entity.CommentCard;
import com.sooum.core.domain.card.entity.FeedCard;
import com.sooum.core.domain.tag.dto.TagDto;
import com.sooum.core.domain.tag.entity.Tag;
import com.sooum.core.domain.tag.repository.CommentTagRepository;
import com.sooum.core.domain.tag.repository.FeedTagRepository;
import com.sooum.core.domain.tag.repository.TagRepository;
import com.sooum.core.global.exceptionmessage.ExceptionMessage;
import com.sooum.core.global.util.NextPageLinkGenerator;
import io.redisearch.Client;
import io.redisearch.Document;
import io.redisearch.Query;
import io.redisearch.SearchResult;
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
    private final Client redisearchClient;

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
            Query query = new Query("*" + keyword + "*")
                    .setSortBy("count", false)
                    .limit(0, 5);

            SearchResult searchResult = redisearchClient.search(query);

            return searchResult.docs.stream()
                    .map(doc -> new TagDto.RelatedTag(
                            Integer.parseInt(doc.getString("count")),
                            doc.getString("content")))
                    .toList();
        } catch (Exception ignore) {
            return tagRepository.findByKeyword(keyword, PageRequest.of(0, 5))
                    .stream()
                    .map(tag -> new TagDto.RelatedTag(tag.getCount(), tag.getContent()))
                    .toList();
        }
    }

    public void upsert() {
        tagRepository.findAllByIsActiveIsTrue().forEach(tag -> {
            Document doc = new Document(tag.getContent())
                    .set("content", tag.getContent())
                    .set("count", tag.getCount());

            redisearchClient.addDocument(doc);
        });
    }
}
