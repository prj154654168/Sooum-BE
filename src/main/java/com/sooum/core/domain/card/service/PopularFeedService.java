package com.sooum.core.domain.card.service;

import com.sooum.core.domain.block.service.BlockMemberService;
import com.sooum.core.domain.card.dto.PopularCardRetrieve;
import com.sooum.core.domain.card.entity.CommentCard;
import com.sooum.core.domain.card.entity.FeedCard;
import com.sooum.core.domain.card.entity.FeedLike;
import com.sooum.core.domain.card.repository.PopularFeedRepository;
import com.sooum.core.domain.img.service.ImgService;
import com.sooum.core.global.util.DistanceUtils;
import com.sooum.core.global.util.NextPageLinkGenerator;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.hateoas.Link;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static com.sooum.core.domain.card.service.FeedService.*;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class PopularFeedService {
    private final PopularFeedRepository popularFeedRepository;
    private final ImgService imgService;
    private final FeedLikeService feedLikeService;
    private final CommentCardService commentCardService;
    private final BlockMemberService blockMemberService;
    private static final int MAX_SIZE = 200;

    public void deletePopularCard(Long cardId) {
        popularFeedRepository.deletePopularCard(cardId);
    }

    public List<PopularCardRetrieve> findHomePopularFeeds(final Optional<Double> latitude,
                                                          final Optional<Double> longitude,
                                                          final Long memberPk) {
        PageRequest pageRequest = PageRequest.of(0, MAX_SIZE);
        LocalDateTime storyExpiredTime = LocalDateTime.now().minusDays(1L);
        List<FeedCard> feeds = popularFeedRepository.findPopularFeeds(storyExpiredTime, pageRequest);
        List<FeedCard> filteredFeeds = blockMemberService.filterBlockedMembers(feeds, memberPk);

        List<FeedLike> feedLikes = feedLikeService.findByTargetCards(filteredFeeds);
        List<CommentCard> comments = commentCardService.findByMasterCards(filteredFeeds);

        return NextPageLinkGenerator.appendEachCardDetailLink(filteredFeeds.stream()
                .map(feed -> PopularCardRetrieve.builder()
                        .id(feed.getPk())
                        .content(feed.getContent())
                        .isStory(feed.isStory())
                        .backgroundImgUrl(Link.of(imgService.findImgUrl(feed.getImgType(), feed.getImgName())))
                        .font(feed.getFont())
                        .fontSize(feed.getFontSize())
                        .distance(DistanceUtils.calculate(feed.getLocation(), latitude, longitude))
                        .createdAt(feed.getCreatedAt())
                        .isLiked(isLiked(feed, feedLikes))
                        .likeCnt(countLikes(feed, feedLikes))
                        .isCommentWritten(isWrittenCommentCard(comments, memberPk))
                        .commentCnt(countComments(feed, comments))
                        .build()
                )
                .toList());
    }
}
