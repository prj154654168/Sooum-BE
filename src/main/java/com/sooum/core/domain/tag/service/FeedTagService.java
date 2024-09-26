package com.sooum.core.domain.tag.service;

import com.sooum.core.domain.card.controller.FeedCardController;
import com.sooum.core.domain.card.entity.FeedCard;
import com.sooum.core.domain.tag.dto.TagDto;
import com.sooum.core.domain.tag.entity.Tag;
import com.sooum.core.domain.tag.repository.FeedTagRepository;
import com.sooum.core.global.util.NextPageLinkGenerator;
import lombok.RequiredArgsConstructor;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Service
@RequiredArgsConstructor
public class FeedTagService {
    private final FeedTagRepository feedTagRepository;

    public List<TagDto.ReadTagResponse> readTags(FeedCard feedCard) {
        List<Tag> tagsByFeedCard = getTagsByFeedCard(feedCard);
//        return NextPageLinkGenerator.generateTagFeedLink(tagsByFeedCard.stream()
//                .map(tag -> TagDto.ReadTagResponse.builder()
//                        .id(tag.getPk())
//                        .content(tag.getContent())
//                        .build()
//                )
//                .toList());
        return tagsByFeedCard.stream()
                .map(tag -> {
                    TagDto.ReadTagResponse tagResponse = TagDto.ReadTagResponse.builder()
                            .id(tag.getPk())
                            .content(tag.getContent())
                            .build();
                    tagResponse.add(
                            linkTo(FeedCardController.class).slash("tags/" + tag.getPk()).withRel("tag-feed")
                    );
                    return tagResponse;
                })
                .collect(Collectors.toList());


    }

    public List<Tag> getTagsByFeedCard(FeedCard feedCard) {
        return feedTagRepository.findByFeedCard(feedCard);
    }
}