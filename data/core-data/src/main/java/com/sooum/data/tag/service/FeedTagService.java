package com.sooum.data.tag.service;

import com.sooum.data.tag.entity.FeedTag;
import com.sooum.data.tag.entity.Tag;
import com.sooum.data.tag.repository.FeedTagRepository;
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

    public int getCountTagFeeds(Long tagPk) {
        return feedTagRepository.countTagFeeds(tagPk);
    }

    public List<FeedTag> findTop5FeedTags(List<Long> favoriteTagPks, List<Long> blockedMemberPks) {
        return blockedMemberPks.isEmpty()
                ? feedTagRepository.findTop5FeedTagsWithoutBlock(favoriteTagPks)
                : feedTagRepository.findTop5FeedTagsWithBlock(favoriteTagPks, blockedMemberPks);
    }

    public List<FeedTag> findLoadFeedTagsIn(List<FeedTag> feedTags) {
        return feedTagRepository.findLoadFeedTagsIn(feedTags);
    }

    public void saveAll(List<FeedTag> feedTagList) {
        feedTagRepository.saveAll(feedTagList);
    }

    public void deleteFeedTag(Long memberPk) {
        feedTagRepository.deleteFeedTag(memberPk);
    }
}