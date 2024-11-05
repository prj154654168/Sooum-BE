package com.sooum.data.tag.service;

import com.sooum.data.card.entity.CommentCard;
import com.sooum.data.tag.entity.CommentTag;
import com.sooum.data.tag.entity.Tag;
import com.sooum.data.tag.repository.CommentTagRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CommentTagService {

    private final CommentTagRepository commentTagRepository;

    public void deleteByCommentCardPk(Long cardPk) {
        List<CommentTag> tags = commentTagRepository.findAllByCommentCardPk(cardPk);

        if(!tags.isEmpty()) {
            updateCount(tags);
            commentTagRepository.deleteAllInBatch(tags);
        }
    }

    public void deleteByCommentCards(List<CommentCard> commentCards) {
        List<CommentTag> tags = commentTagRepository.findAllByCommentCards(commentCards);

        if(!tags.isEmpty()) {
            updateCount(tags);
            commentTagRepository.deleteAllInBatch(tags);
        }
    }

    private void updateCount(List<CommentTag> tags) {
        tags.stream()
                .map(CommentTag::getTag)
                .forEach(Tag::minusCount);
    }

    public void saveAll(List<CommentTag> commentTagList) {
        commentTagRepository.saveAll(commentTagList);
    }
}