package com.sooum.api.report.service;

import com.sooum.api.IntegrationTestSupport;
import com.sooum.data.card.entity.FeedCard;
import com.sooum.data.card.entity.font.Font;
import com.sooum.data.card.entity.fontsize.FontSize;
import com.sooum.data.card.entity.imgtype.CardImgType;
import com.sooum.data.card.repository.FeedCardRepository;
import com.sooum.data.member.entity.Member;
import com.sooum.data.member.entity.devicetype.DeviceType;
import com.sooum.data.member.repository.MemberRepository;
import com.sooum.data.report.entity.reporttype.ReportType;
import com.sooum.data.report.repository.FeedReportRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.UUID;

import static org.assertj.core.api.Assertions.*;
class FeedReportUseCaseTest extends IntegrationTestSupport {

    @Autowired
    private MemberRepository memberRepository;
    @Autowired
    private FeedCardRepository feedCardRepository;
    @Autowired
    private FeedReportUseCase feedReportUseCase;
    @Autowired
    private FeedReportRepository feedReportRepository;
    @AfterEach
    void tearDown() {
        feedReportRepository.deleteAll();
        feedCardRepository.deleteAll();
        memberRepository.deleteAll();
    }

    @DisplayName("각기 다른 사용자로 부터 7번의 신고가 접수되면 밴 횟수가 1 증가한다.")
    @Test
    void reportFeed() {
        //given
        Member member1 = saveMember();
        Member member2 = saveMember();
        Member member3 = saveMember();
        Member member4 = saveMember();
        Member member5 = saveMember();
        Member member6 = saveMember();
        Member member7 = saveMember();

        FeedCard content = saveFeedCardBy("content");

        //when
        feedReportUseCase.reportFeed(content.getPk(), member1, ReportType.OTHER);
        feedReportUseCase.reportFeed(content.getPk(), member2, ReportType.OTHER);
        feedReportUseCase.reportFeed(content.getPk(), member3, ReportType.OTHER);
        feedReportUseCase.reportFeed(content.getPk(), member4, ReportType.OTHER);
        feedReportUseCase.reportFeed(content.getPk(), member5, ReportType.OTHER);
        feedReportUseCase.reportFeed(content.getPk(), member6, ReportType.OTHER);
        feedReportUseCase.reportFeed(content.getPk(), member7, ReportType.OTHER);

        //then
        Member contentWriter = memberRepository.findById(content.getWriter().getPk()).orElseThrow();
        assertThat(contentWriter.getBanCount()).isEqualTo(1);

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
                .isAllowNotify(false)
                .deviceType(DeviceType.IOS)
                .nickname(UUID.randomUUID().toString())
                .build()
        );
    }
}