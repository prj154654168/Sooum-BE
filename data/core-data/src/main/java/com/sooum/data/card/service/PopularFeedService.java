package com.sooum.data.card.service;

import com.sooum.data.card.entity.FeedCard;
import com.sooum.data.card.entity.PopularFeed;
import com.sooum.data.card.repository.PopularFeedRepository;
import com.sooum.data.member.entity.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class PopularFeedService {
    private final PopularFeedRepository popularFeedRepository;
    private static final int DEFAULT_VERSION = 1;

    public void deletePopularCard(Long cardId) {
        popularFeedRepository.deletePopularCard(cardId);
    }

    public List<FeedCard> getPopularFeeds(List<Long> blockedMembers) {
        PageRequest pageRequest = PageRequest.ofSize(200);
        int latestVersionByLike = popularFeedRepository.findLatestVersionByLike().orElse(DEFAULT_VERSION);
        int latestVersionByComment = popularFeedRepository.findLatestVersionByComment().orElse(DEFAULT_VERSION);

        List<PopularFeed> popularFeeds = popularFeedRepository.findPopularFeeds(blockedMembers, latestVersionByLike, latestVersionByComment, pageRequest);
        return popularFeeds.stream()
                .map(PopularFeed::getPopularCard)
                .distinct()
                .toList();
    }

    public void deletePopularCardByMemberPk(Long memberPk) {
        popularFeedRepository.deletePopularCardByMemberPk(memberPk);
    }
}
