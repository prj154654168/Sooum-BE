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
import com.sooum.data.tag.entity.FavoriteTag;
import com.sooum.data.tag.entity.FeedTag;
import com.sooum.data.tag.entity.Tag;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;

import java.util.Collection;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class FavoriteTagRepositoryTest extends DataJpaTestSupport {
    @Autowired
    private MemberRepository memberRepository;
    @Autowired
    private FeedCardRepository feedCardRepository;
    @Autowired
    private TagRepository tagRepository;
    @Autowired
    private FavoriteTagRepository favoriteTagRepository;
    @Autowired
    private FeedTagRepository feedTagRepository;

    @DisplayName("즐겨찾기 태그 PK 리스트의 첫 페이지를 조회한다.")
    @Test
    void findMyFavoriteTags() {
        //given
        final int TOTAL_TAG_COUNT = 20;
        final int PAGE_SIZE = 5;

        Member member = saveMember();
        FeedCard feedCard = saveFeedCard();

        List<Tag> tagList = IntStream.rangeClosed(1, TOTAL_TAG_COUNT)
                .mapToObj(i -> saveTag("tag-content" + i)).toList();

        saveFeedTags(feedCard, tagList);
        saveFavoriteTags(member, tagList);

        //when
        List<Long> firstFavoriteTagPk = favoriteTagRepository.findMyFavoriteTags(member.getPk(), null, PageRequest.ofSize(PAGE_SIZE));

        //then
        Assertions.assertThat(firstFavoriteTagPk).hasSize(PAGE_SIZE)
                .containsExactly(
                        tagList.get(TOTAL_TAG_COUNT-1).getPk(),
                        tagList.get(TOTAL_TAG_COUNT-2).getPk(),
                        tagList.get(TOTAL_TAG_COUNT-3).getPk(),
                        tagList.get(TOTAL_TAG_COUNT-4).getPk(),
                        tagList.get(TOTAL_TAG_COUNT-5).getPk()
                );
    }

    @DisplayName("즐겨찾기 태그 PK 리스트 조회 시나리오")
    @TestFactory
    Collection<DynamicTest> favoriteTagsTest() {
        //given
        final int TOTAL_TAG_COUNT = 13;
        final int PAGE_SIZE = 5;
        Member member = saveMember();
        FeedCard feedCard = saveFeedCard();
        List<Tag> tagList = IntStream.rangeClosed(1, TOTAL_TAG_COUNT) //1~13
                .mapToObj(i -> saveTag("tag-content" + i)).toList();

        saveFeedTags(feedCard, tagList);
        saveFavoriteTags(member, tagList);

        return List.of(
                DynamicTest.dynamicTest("첫 페이지를 조회한다.", () -> {
                    //when
                    List<Long> firstFavoriteTagPk = favoriteTagRepository.findMyFavoriteTags(member.getPk(), null, PageRequest.ofSize(PAGE_SIZE));

                    //then
                    Assertions.assertThat(firstFavoriteTagPk)
                            .hasSize(PAGE_SIZE)
                            .contains(
                                    tagList.get(12).getPk(),
                                    tagList.get(8).getPk()
                            );
                }),
                DynamicTest.dynamicTest("두번째 페이지를 조회한다.", () -> {
                    //when
                    List<Long> firstFavoriteTagPk = favoriteTagRepository.findMyFavoriteTags(member.getPk(), tagList.get(8).getPk(), PageRequest.ofSize(PAGE_SIZE));

                    //then
                    Assertions.assertThat(firstFavoriteTagPk)
                            .hasSize(PAGE_SIZE)
                            .contains(
                                    tagList.get(7).getPk(),
                                    tagList.get(3).getPk()
                            );
                }),
                DynamicTest.dynamicTest("마지막 페이지를 조회한다.", () -> {
                    //when
                    List<Long> firstFavoriteTagPk = favoriteTagRepository.findMyFavoriteTags(member.getPk(), tagList.get(3).getPk(), PageRequest.ofSize(PAGE_SIZE));

                    //then
                    Assertions.assertThat(firstFavoriteTagPk)
                            .hasSize(3)
                            .contains(
                                    tagList.get(2).getPk(),
                                    tagList.get(0).getPk()
                            );
                })
        );
    }

    private List<FavoriteTag> saveFavoriteTags(Member member, List<Tag> tags) {
        return tags.stream()
                .map(tag -> favoriteTagRepository.save(FavoriteTag.builder()
                        .tag(tag)
                        .member(member).build()))
                .collect(Collectors.toList());
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

    private FeedCard saveFeedCard() {
        return feedCardRepository.save(FeedCard.builder()
                .fontSize(FontSize.NONE)
                .font(Font.PRETENDARD)
                .imgType(CardImgType.DEFAULT)
                .writer(saveMember())
                .isPublic(true)
                .isFeedActive(true)
                .writerIp(UUID.randomUUID().toString())
                .content("feed content")
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
