package com.sooum.core.domain.tag.service;


import com.sooum.core.domain.tag.entity.CommentTag;
import com.sooum.core.domain.tag.repository.CommentTagRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CommentTagService {
    private final CommentTagRepository commentTagRepository;

    public void saveAll(List<CommentTag> commentTagList) {
        commentTagRepository.saveAll(commentTagList);
    }
}
