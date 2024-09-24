package com.sooum.core.domain.card.repository;

import com.sooum.core.domain.card.dto.popularitytype.PopularityType;
import com.sooum.core.domain.card.entity.FeedCard;
import com.sooum.core.domain.card.entity.PopularFeed;
import com.sooum.core.domain.card.entity.font.Font;
import com.sooum.core.domain.card.entity.fontsize.FontSize;
import com.sooum.core.domain.card.entity.imgtype.ImgType;
import com.sooum.core.domain.member.entity.Member;
import com.sooum.core.domain.member.entity.devicetype.DeviceType;
import com.sooum.core.domain.member.repository.MemberRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@DataJpaTest
class PopularFeedRepositoryTest {
    @Autowired
    PopularFeedRepository popularFeedRepository;
    @Autowired
    FeedCardRepository feedCardRepository;
    @Autowired
    MemberRepository memberRepository;
    private static final int MEMBER_SIZE = 10;
    private static final int CARD_SIZE = 100;
    private static final int MAX_SIZE = 200;
    @Test
    void findPopularFeeds() {
        // given
        List<Member> members = memberRepository.saveAll(createMembers());
        List<FeedCard> feedCards = feedCardRepository.saveAll(createFeedCards(members));
        updateCreatedAt(feedCards);
        List<FeedCard> popularFeeds = popularFeedRepository.saveAll(createPopularFeedCards(feedCards))
                .stream().map(PopularFeed::getPopularCard).toList();

        // when
        List<FeedCard> findPopularFeeds = popularFeedRepository.findPopularFeeds(PageRequest.of(0, MAX_SIZE));

        // then
        int isNotStoryCardCnt = popularFeeds.stream()
                .filter(feedCard -> !feedCard.isStory())
                .toList().size();
        int isNotExpiredStoryCardCnt = popularFeeds.stream()
                .filter(feedCard -> feedCard.getCreatedAt().isAfter(LocalDateTime.now().minusDays(1)) && feedCard.isStory())
                .toList().size();

        Assertions.assertThat(findPopularFeeds.size()).isEqualTo(isNotStoryCardCnt + isNotExpiredStoryCardCnt);
    }

    private void updateCreatedAt(List<FeedCard> feedCards) {
        for (int i = 0; i < CARD_SIZE; i++) {
            feedCards.get(i).updateCreatedAt(i <= CARD_SIZE / 2 ? LocalDateTime.now().minusHours(i) : LocalDateTime.now().plusHours(i));
        }
    }

    private List<PopularFeed> createPopularFeedCards(List<FeedCard> feedCards) {
        ArrayList<PopularFeed> popularFeedCards = new ArrayList<>();
        for (int i = 0; i < CARD_SIZE; i++) {
            PopularFeed popularFeed = PopularFeed.builder()
                    .popularCard(feedCards.get(i))
                    .popularityType(i % 2 == 1 ? PopularityType.LIKE : PopularityType.COMMENT)
                    .build();
            popularFeedCards.add(popularFeed);
        }
        return popularFeedCards;
    }

    private List<FeedCard> createFeedCards(List<Member> members) {
        ArrayList<FeedCard> feedCards = new ArrayList<>();
        for (int i = 0; i < CARD_SIZE; i++) {
            FeedCard feedCard = FeedCard.builder()
                    .content("카드 내용 " + i)
                    .fontSize(FontSize.BIG)
                    .font(Font.DEFAULT)
                    .location(null)
                    .imgType(ImgType.DEFAULT)
                    .imgName(i + ".jpg")
                    .isPublic(true)
                    .isStory(i % 2 != 0)
                    .writer(members.get(i % MEMBER_SIZE))
                    .build();

            feedCards.add(feedCard);
        }
        return feedCards;
    }

    private List<Member> createMembers() {
        ArrayList<Member> members = new ArrayList<>();
        for (int i = 0; i < MEMBER_SIZE; i++) {
            Member member = Member.builder()
                    .deviceId("test" + i)
                    .deviceType(DeviceType.IOS)
                    .firebaseToken("firbaseDummy")
                    .nickname("nickname" + i)
                    .isAllowNotify(true)
                    .build();
            members.add(member);
        }
        return members;
    }
}