package com.sooum.core.domain.tag.repository;

import com.sooum.core.domain.card.entity.FeedCard;
import com.sooum.core.domain.card.entity.font.Font;
import com.sooum.core.domain.card.entity.fontsize.FontSize;
import com.sooum.core.domain.card.entity.imgtype.ImgType;
import com.sooum.core.domain.card.repository.FeedCardRepository;
import com.sooum.core.domain.member.entity.Member;
import com.sooum.core.domain.member.entity.devicetype.DeviceType;
import com.sooum.core.domain.member.repository.MemberRepository;
import com.sooum.core.domain.tag.entity.FeedTag;
import com.sooum.core.domain.tag.entity.Tag;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;

import java.util.ArrayList;
import java.util.List;


@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class FeedTagRepositoryTest {
    private static final List<String> tagContents = List.of(
            "우정", "사랑", "고민"
    );
    private static final int FEED_SIZE = 10;

    @Autowired
    MemberRepository memberRepository;
    @Autowired
    FeedTagRepository feedTagRepository;
    @Autowired
    FeedCardRepository feedCardRepository;
    @Autowired
    TagRepository tagRepository;

    @Test
    @DisplayName("태그로 피드 카드 조회하기")
    void findTagFeeds() throws Exception{
        // given
        List<FeedCard> feedCards = createFeedCard(createMember());
        List<Tag> tags = createTags();
        createFeedTags(tags, feedCards);
        PageRequest pageRequest = PageRequest.of(0, FEED_SIZE);
        // when
        List<FeedCard> findTagFeeds = feedTagRepository.findFeeds("고민", pageRequest);

        // then
        Assertions.assertThat(isSatisfyTagFeedCond(findTagFeeds)).isTrue();
    }

    private boolean isSatisfyTagFeedCond(List<FeedCard> feedCards) {
        boolean isStoryCond = feedCards.stream().noneMatch(FeedCard::isStory);
        boolean isPublicCond = feedCards.stream().noneMatch(FeedCard::isPublic);
        boolean isDeletedCond = feedCards.stream().noneMatch(FeedCard::isDeleted);

        return isStoryCond && isPublicCond && isDeletedCond;
    }

    @Test
    @DisplayName("태그가 포함된 피드 카드 개수 세기")
    void countTagFeed() throws Exception{
        // given
        List<FeedCard> feedCards = createFeedCard(createMember());
        List<Tag> tags = createTags();
        createFeedTags(tags, feedCards);
        // when
        Integer tagFeedCnt = feedTagRepository.countTagFeeds("고민");

        // then
        Assertions.assertThat(tagFeedCnt).isEqualTo(FEED_SIZE / tagContents.size());
    }

    private List<FeedCard> createFeedCard(Member member) {
        List<FeedCard> feedCards = new ArrayList<>();
        for (int i = 0; i < FEED_SIZE; i++) {
            FeedCard build = FeedCard.builder()
                    .content("content" + i)
                    .font(Font.PRETENDARD)
                    .fontSize(FontSize.BIG)
                    .imgType(ImgType.DEFAULT)
                    .imgName((i % 50 + 1) + ".png")
                    .isPublic(false)
                    .isStory(false)
                    .writer(member)
                    .location(null)
                    .build();
            feedCards.add(build);
        }
        return feedCardRepository.saveAll(feedCards);
    }

    private Member createMember() {
        Member build1 = Member.builder().deviceId("dev")
                .deviceType(DeviceType.IOS)
                .firebaseToken("fire")
                .nickname("nick")
                .isAllowNotify(true)
                .build();
        return memberRepository.save(build1);
    }

    private List<Tag> createTags() {
        ArrayList<Tag> tags = new ArrayList<>();
        for (String tagContent : tagContents) {
            Tag build = Tag.builder()
                    .content(tagContent)
                    .build();
            tags.add(build);
        }
        return tagRepository.saveAll(tags);
    }

    private void createFeedTags(List<Tag> tags, List<FeedCard> feedCards) {
        ArrayList<FeedTag> feedTags = new ArrayList<>();
        for (int i = 0; i < FEED_SIZE; i++) {
            FeedTag build = FeedTag.builder()
                    .feedCard(feedCards.get(i))
                    .tag(tags.get(i % tagContents.size()))
                    .build();
            feedTags.add(build);
        }
        feedTagRepository.saveAll(feedTags);
    }
}