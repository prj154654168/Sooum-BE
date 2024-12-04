package com.sooum.batch.card.batch.service;

import com.sooum.batch.card.batch.repository.PopularCardBatchRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DeletePreviousPopularCard {
    private final PopularCardBatchRepository popularCardBatchRepository;

    public void deletePreviousPopularFeedsByLike() {
        popularCardBatchRepository.deletePreviousPopularFeedsByLike();
    }

    public void deletePreviousPopularFeedsByComment() {
        popularCardBatchRepository.deletePreviousPopularFeedsByComment();
    }
}
