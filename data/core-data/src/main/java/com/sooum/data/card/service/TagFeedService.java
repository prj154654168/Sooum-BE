package com.sooum.data.card.service;

import com.sooum.data.block.service.BlockMemberService;
import com.sooum.data.card.entity.FeedCard;
import com.sooum.data.tag.repository.FeedTagRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TagFeedService {
    private final FeedTagRepository feedTagRepository;
    private final BlockMemberService blockMemberService;
    private static final int MAX_PAGE_SIZE = 100;
    private final static int DEFAULT_PAGE_SIZE = 50;

    public List<FeedCard> findTagFeeds(Long tagPk, Optional<Long> lastPk) {
        PageRequest pageRequest = PageRequest.of(0, MAX_PAGE_SIZE);
        return lastPk.isEmpty()
                ? feedTagRepository.findFeeds(tagPk, pageRequest)
                : feedTagRepository.findFeeds(tagPk, lastPk.get(), pageRequest);
    }

    public List<FeedCard> findFilteredTagFeeds(Long tagPk, Optional<Long> lastPk, Long memberId) {
        List<FeedCard> resultFeedCards = new ArrayList<>();

        while (resultFeedCards.size() < DEFAULT_PAGE_SIZE) {
            List<FeedCard> findTagFeeds = findTagFeeds(tagPk, lastPk);

            if (!findTagFeeds.isEmpty()) {
                lastPk = Optional.of(findTagFeeds.get(findTagFeeds.size() - 1).getPk());
            }

            List<FeedCard> filteredFeedCards = blockMemberService.filterBlockedMembers(findTagFeeds, memberId);
            resultFeedCards.addAll(filteredFeedCards);

            if (isEndOfPage(findTagFeeds)) {
                break;
            }
        }
        return resultFeedCards.size() > DEFAULT_PAGE_SIZE ? resultFeedCards.subList(0, DEFAULT_PAGE_SIZE) : resultFeedCards;
    }

    private static boolean isEndOfPage(List<FeedCard> findTagFeeds) {
        return findTagFeeds.size() < MAX_PAGE_SIZE;
    }
}
