package com.sooum.data.card.repository;

import com.sooum.data.DataJpaTestSupport;
import com.sooum.data.card.entity.FeedCard;
import com.sooum.data.card.entity.FeedLike;
import com.sooum.data.card.entity.font.Font;
import com.sooum.data.card.entity.fontsize.FontSize;
import com.sooum.data.card.entity.imgtype.CardImgType;
import com.sooum.data.member.entity.Member;
import com.sooum.data.member.entity.devicetype.DeviceType;
import com.sooum.data.member.repository.MemberRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.UUID;

public class FeedLikeRepositoryTest extends DataJpaTestSupport {
    @Autowired
    private MemberRepository memberRepository;
    @Autowired
    private FeedCardRepository feedCardRepository;
    @Autowired
    private FeedLikeRepository feedLikeRepository;

    @DisplayName("피드 카드 리스트와 관련된 공감 리스트를 조회한다.")
    @Test
    void findByTargetList() {
        //given
        Member member = saveMember();

        FeedCard content1 = saveFeedCard();
        FeedCard content2 = saveFeedCard();
        FeedCard content3 = saveFeedCard();
        FeedCard content4 = saveFeedCard();
        FeedCard content5 = saveFeedCard();

        saveFeedLikeBy(member, content1);
        saveFeedLikeBy(member, content3);
        saveFeedLikeBy(member, content5);

        //when
        List<FeedLike> feedLikes = feedLikeRepository.findByTargetList(List.of(content1, content2, content3, content4, content5));

        //then
        Assertions.assertThat(feedLikes)
                .hasSize(3)
                .extracting(FeedLike::getTargetCard)
                .containsExactly(content1, content3, content5);

    }

    private FeedLike saveFeedLikeBy(Member member, FeedCard feedCard) {
        return feedLikeRepository.save(FeedLike.builder()
                .likedMember(member)
                .targetCard(feedCard)
                .build()
        );
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
                .content(UUID.randomUUID().toString())
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
