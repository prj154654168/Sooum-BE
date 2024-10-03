package com.sooum.core.domain.tag.service;

import com.sooum.core.domain.tag.entity.FeedTag;
import com.sooum.core.domain.tag.repository.FeedTagRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class FeedTagService {
    private final FeedTagRepository feedTagRepository;

    public void saveAll(List<FeedTag> feedTagList) {
        feedTagRepository.saveAll(feedTagList);
    }
}