package com.sooum.core.domain.tag.service;

import com.sooum.core.domain.tag.entity.FeedTag;
import com.sooum.core.domain.tag.entity.Tag;
import com.sooum.core.domain.tag.repository.FeedTagRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class FeedTagService {

    private final FeedTagRepository feedTagRepository;

    @Transactional
    public void deleteByFeedCardPk(Long cardPk) {
        List<FeedTag> tags = feedTagRepository.findAllByFeedCardPk(cardPk);

        if(!tags.isEmpty()) {
            tags.stream()
                    .map(FeedTag::getTag)
                    .forEach(Tag::minusCount);

            feedTagRepository.deleteAllInBatch(tags);
        }
    }
}
