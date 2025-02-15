package com.sooum.data.card.repository;

import com.sooum.data.DataJpaTestSupport;
import com.sooum.data.card.entity.FeedCard;
import com.sooum.data.card.entity.font.Font;
import com.sooum.data.card.entity.fontsize.FontSize;
import com.sooum.data.card.entity.imgtype.CardImgType;
import com.sooum.data.member.entity.Member;
import com.sooum.data.member.entity.devicetype.DeviceType;
import com.sooum.data.member.repository.MemberRepository;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.*;

@Slf4j
class FeedCardRepositoryTest extends DataJpaTestSupport {

    @Autowired
    private FeedCardRepository feedCardRepository;
    @Autowired
    private MemberRepository memberRepository;

    @AfterEach
    void tearDown() {
        feedCardRepository.deleteAllInBatch();
        memberRepository.deleteAllInBatch();
    }

    @DisplayName("최신글 피드의 첫 페이지를 조회한다.")
    @Test
    void findByNextPage() {
        //given
        final int TOTAL_CARD_CNT = 15;
        final int PAGE_SIZE = 5;

        //글 생성 (content1 ... content15)
        IntStream.rangeClosed(1, TOTAL_CARD_CNT)
                .forEach(i -> saveFeedCardBy("content" + i));

        //when
        //lastId == null -> 최초 조회
        List<FeedCard> firstPage = feedCardRepository.findByNextPage(
                null,
                Collections.emptyList(),
                PageRequest.ofSize(PAGE_SIZE)
        );

        //then
        assertThat(firstPage)
                .hasSize(PAGE_SIZE)
                .extracting("content")
                .containsExactly("content"+TOTAL_CARD_CNT,
                        "content"+(TOTAL_CARD_CNT-1),
                        "content"+(TOTAL_CARD_CNT-2),
                        "content"+(TOTAL_CARD_CNT-3),
                        "content"+(TOTAL_CARD_CNT-4)
                );
    }

    @DisplayName("최신글 피드 카드 조회 시나리오")
    @TestFactory
    Collection<DynamicTest> dynamicTests() {

        //given
        final int TOTAL_CARD_CNT = 65;
        final int PAGE_SIZE = 30;
        List<FeedCard> feedCards = IntStream.rangeClosed(1, TOTAL_CARD_CNT)
                .mapToObj(i -> saveFeedCardBy("content" + i)).toList();

        return List.of(
                DynamicTest.dynamicTest("첫 페이지를 조회한다.", () -> {

                    //when
                    List<FeedCard> firstPage = feedCardRepository.findByNextPage(
                            null,
                            Collections.emptyList(),
                            PageRequest.ofSize(PAGE_SIZE)
                    );

                    //then
                    assertThat(firstPage)
                            .hasSize(PAGE_SIZE)
                            .extracting("content")
                            .contains("content" + 65,
                                    "content" + 36
                            );
                }),
                DynamicTest.dynamicTest("두번째 페이지를 조회한다", () -> {

                    //when
                    List<FeedCard> secondPage = feedCardRepository.findByNextPage(
                            feedCards.get(35).getPk(),
                            Collections.emptyList(),
                            PageRequest.ofSize(PAGE_SIZE)
                    );

                    //then
                    assertThat(secondPage)
                            .hasSize(PAGE_SIZE)
                            .extracting("content")
                            .contains("content" + 35,
                                    "content" + 6
                            );
                }),
                DynamicTest.dynamicTest("마지막 페이지를 조회한다", () -> {

                    //when
                    List<FeedCard> lastPage = feedCardRepository.findByNextPage(
                            feedCards.get(5).getPk(),
                            Collections.emptyList(),
                            PageRequest.ofSize(PAGE_SIZE)
                    );

                    //then
                    assertThat(lastPage)
                            .hasSize(5)
                            .extracting("content")
                            .contains("content" + 5,
                                    "content" + 1
                            );
                })
        );
    }

    @DisplayName("차단된 유저가 작성한 글이 존재하는 경우의 최신글 피드 조회 시나리오")
    @TestFactory
    List<DynamicTest> dynamicTests2() {
        //given
        final int TOTAL_CARD_CNT = 65;
        final int PAGE_SIZE = 30;
        List<FeedCard> feedCards = IntStream.rangeClosed(1, TOTAL_CARD_CNT)
                .mapToObj(i -> saveFeedCardBy("content" + i)).toList();

        List<Long> blockMemberPks = List.of(feedCards.get(38).getWriter().getPk(),
                feedCards.get(37).getWriter().getPk(),
                feedCards.get(36).getWriter().getPk(),
                feedCards.get(35).getWriter().getPk(),
                feedCards.get(34).getWriter().getPk()
        );

        return List.of(
                DynamicTest.dynamicTest("첫 페이지를 조회한다.",()->{
                    //when
                    List<FeedCard> firstPage = feedCardRepository.findByNextPage(
                            null,
                            blockMemberPks,
                            PageRequest.ofSize(PAGE_SIZE)
                    );

                    //then
                    assertThat(firstPage)
                            .hasSize(PAGE_SIZE)
                            .extracting("content")
                            .contains(
                                    "content"+65,
                                    "content"+31
                            );
                }),
                DynamicTest.dynamicTest("두번째 페이지를 조회한다.",()->{
                    //when
                    List<FeedCard> firstPage = feedCardRepository.findByNextPage(
                            feedCards.get(30).getPk(),
                            blockMemberPks,
                            PageRequest.ofSize(PAGE_SIZE)
                    );

                    //then
                    assertThat(firstPage)
                            .hasSize(PAGE_SIZE)
                            .extracting("content")
                            .contains(
                                    "content"+30,
                                    "content"+1
                            );
                })
        );

    }

    private FeedCard saveFeedCardBy(String content) {
        Member member = saveMember();

        return feedCardRepository.save(FeedCard.builder()
                .fontSize(FontSize.NONE)
                .font(Font.PRETENDARD)
                .imgType(CardImgType.DEFAULT)
                .writer(member)
                .isPublic(true)
                .isFeedActive(true)
                .writerIp(UUID.randomUUID().toString())
                .content(content)
                .build()
        );
    }
    private Member saveMember() {
        return memberRepository.save(Member.builder()
                .deviceId(UUID.randomUUID().toString())
                .deviceType(DeviceType.IOS)
                .nickname(UUID.randomUUID().toString())
                .build()
        );
    }


}