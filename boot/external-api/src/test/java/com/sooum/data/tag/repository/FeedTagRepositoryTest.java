package com.sooum.data.tag.repository;

import com.sooum.data.DataJpaTestSupport;
import com.sooum.data.card.entity.FeedCard;
import com.sooum.data.card.entity.font.Font;
import com.sooum.data.card.entity.fontsize.FontSize;
import com.sooum.data.card.entity.imgtype.CardImgType;
import com.sooum.data.card.repository.FeedCardRepository;
import com.sooum.data.member.entity.Member;
import com.sooum.data.member.entity.devicetype.DeviceType;
import com.sooum.data.member.repository.MemberRepository;
import com.sooum.data.tag.entity.FeedTag;
import com.sooum.data.tag.entity.Tag;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.*;

public class FeedTagRepositoryTest extends DataJpaTestSupport {
    @Autowired
    private MemberRepository memberRepository;
    @Autowired
    private TagRepository tagRepository;
    @Autowired
    private FeedTagRepository feedTagRepository;
    @Autowired
    private FeedCardRepository feedCardRepository;

    @DisplayName("즐겨찾기한 태그에 대해 상위 5개 FeedTag를 조회한다.")
    @Test
    void findTop5FeedTagsWithoutBlock(){
        //given
        final int TOTAL_TAG_COUNT = 4;
        final int TOTAL_FEED_COUNT = 10;
        Member member = saveMember();
        List<Tag> tagList = IntStream.rangeClosed(1, TOTAL_TAG_COUNT)
                .mapToObj(i -> saveTag("tag-content" + i)).toList();

        List<FeedCard> feedCardList = IntStream.range(0, TOTAL_FEED_COUNT)
                .mapToObj(i -> saveFeedCard(member)).toList();

        for (FeedCard feedCard : feedCardList){
            saveFeedTags(feedCard, tagList);
        }

        //1,2,3 태그 선택
        List<Long> favoriteTagPks = List.of(tagList.get(0).getPk(), tagList.get(1).getPk(), tagList.get(2).getPk());

        //when
        List<FeedTag> top5FeedTagsWithoutBlock = feedTagRepository.findTop5FeedTagsWithoutBlock(favoriteTagPks);

        //then
        assertThat(top5FeedTagsWithoutBlock)
                .hasSize(5 * favoriteTagPks.size())
                .extracting("tag")
                .containsOnly(tagList.get(0), tagList.get(1), tagList.get(2));

        //then
        Map<Tag, List<FeedTag>> feedTagByTag = top5FeedTagsWithoutBlock.stream()
                .collect(Collectors.groupingBy(FeedTag::getTag));

        feedTagByTag.forEach((tag, feedTagList) -> {
            assertThat(feedTagList).hasSize(5);
        });
    }

    @DisplayName("차단한 유저가 작성한 글이 있는 즐겨찾기 태그의 상위 5개 FeedTag를 조회한다.")
    @Test
    void findTop5FeedTagsWithBlock() {
        //given
        Member member1 = saveMember();
        Member member2 = saveMember();

        Tag tag = saveTag("tag-content1");

        FeedCard feedCard1 = saveFeedCard(member1);
        FeedCard feedCard2 = saveFeedCard(member2);

        saveFeedTags(feedCard1, List.of(tag));
        saveFeedTags(feedCard2, List.of(tag));

        List<Long> favoriteTagPks = List.of(tag.getPk());
        List<Long> blockedMemberPks = List.of(member2.getPk()); //member2 차단

        //when
        List<FeedTag> top5FeedTagsWithBlock = feedTagRepository.findTop5FeedTagsWithBlock(favoriteTagPks, blockedMemberPks);

        //then
        assertThat(top5FeedTagsWithBlock)
                .hasSize(1)
                .extracting("feedCard.writer")
                .doesNotContain(member2)
                .containsExactly(member1);

    }

    @DisplayName("FeedTag에 대해 FeedTag를 태그 정렬로 조회한다.")
    @Test
    void findLoadFeedTagsIn() {
        //given
        final int TOTAL_TAG_COUNT = 4;
        Member member = saveMember();
        FeedCard feedCard = saveFeedCard(member);
        List<Tag> tagList = IntStream.rangeClosed(1, TOTAL_TAG_COUNT)
                .mapToObj(i -> saveTag("tag-content" + i)).toList();
        List<FeedTag> feedTags = saveFeedTags(feedCard, tagList);

        //when
        List<FeedTag> loadFeedTags = feedTagRepository.findLoadFeedTagsIn(feedTags);

        //then
        assertThat(loadFeedTags)
                .hasSize(4)
                .extracting("tag.content")
                .containsExactly("tag-content4", "tag-content3", "tag-content2", "tag-content1");
    }

    private List<FeedTag> saveFeedTags(FeedCard feedCard, List<Tag> tags) {
        return tags.stream()
                .map(tag -> feedTagRepository.save(FeedTag.builder()
                        .feedCard(feedCard)
                        .tag(tag)
                        .build()))
                .collect(Collectors.toList());
    }

    private Tag saveTag(String content) {
        return tagRepository.save(Tag.ofFeed(content, true));
    }

    private FeedCard saveFeedCard(Member member) {
        return feedCardRepository.save(FeedCard.builder()
                .fontSize(FontSize.NONE)
                .font(Font.PRETENDARD)
                .imgType(CardImgType.DEFAULT)
                .writer(member)
                .isPublic(true)
                .isFeedActive(true)
                .writerIp(UUID.randomUUID().toString())
                .content("feed-content")
                .build());
    }

    private Member saveMember(){
        return memberRepository.save(Member.builder()
                .deviceId(UUID.randomUUID().toString())
                .deviceType(DeviceType.IOS)
                .nickname(UUID.randomUUID().toString())
                .build());
    }
}
