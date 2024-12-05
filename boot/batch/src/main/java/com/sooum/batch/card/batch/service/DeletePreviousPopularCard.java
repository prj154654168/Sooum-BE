package com.sooum.batch.card.batch.service;

import com.sooum.batch.card.batch.repository.PopularCardBatchRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
public class DeletePreviousPopularCard {
    private final PopularCardBatchRepository popularCardBatchRepository;
    private static final int DEFAULT_VERSION = 1;

    public void deletePreviousPopularFeedsByLike() {
        int latestVersion = popularCardBatchRepository.findLatestVersionByLike().orElse(DEFAULT_VERSION);

        if (isExistPreviousPopularCards(latestVersion)) {
            int previousVersion = latestVersion - 1;
            popularCardBatchRepository.deletePreviousPopularFeedsByLike(previousVersion);
        }
    }

    public void deletePreviousPopularFeedsByComment() {
        int latestVersion = popularCardBatchRepository.findLatestVersionByComment().orElse(DEFAULT_VERSION);

        if (isExistPreviousPopularCards(latestVersion)) {
            int previousVersion = latestVersion - 1;
            popularCardBatchRepository.deletePreviousPopularFeedsByComment(previousVersion);
        }
    }

    private static boolean isExistPreviousPopularCards(int latestVersion) {
        return latestVersion != DEFAULT_VERSION;
    }
}
