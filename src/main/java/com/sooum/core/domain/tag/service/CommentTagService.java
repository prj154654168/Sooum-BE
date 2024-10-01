package com.sooum.core.domain.tag.service;

import com.sooum.core.domain.tag.entity.CommentTag;
import com.sooum.core.domain.tag.repository.CommentTagRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CommentTagService {

    private final CommentTagRepository commentTagRepository;

    @Transactional
    public void deleteByCommentCardPk(Long cardPk) {
        List<CommentTag> tags = commentTagRepository.findAllByCommentCardPk(cardPk);

        if(!tags.isEmpty()) {
            tags.get(0).getTag().minusCount(tags.size());
            commentTagRepository.deleteAllInBatch(tags);
        }
    }
}
