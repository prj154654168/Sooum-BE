package com.sooum.core.domain.tag.service;

import com.sooum.core.domain.tag.repository.CommentTagRepository;
import com.sooum.core.domain.tag.repository.FeedTagRepository;
import org.springframework.stereotype.Service;

@Service
public class FeedTagService extends TagService {
    public FeedTagService(FeedTagRepository feedTagRepository, CommentTagRepository commentTagRepository) {
        super(feedTagRepository, commentTagRepository);
    }
}