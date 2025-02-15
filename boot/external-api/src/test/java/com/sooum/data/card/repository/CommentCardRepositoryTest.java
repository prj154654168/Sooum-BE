package com.sooum.data.card.repository;

import com.sooum.data.DataJpaTestSupport;
import com.sooum.data.card.entity.Card;
import com.sooum.data.card.entity.CommentCard;
import com.sooum.data.card.entity.FeedCard;
import com.sooum.data.card.entity.font.Font;
import com.sooum.data.card.entity.fontsize.FontSize;
import com.sooum.data.card.entity.imgtype.CardImgType;
import com.sooum.data.member.entity.Member;
import com.sooum.data.member.entity.devicetype.DeviceType;
import com.sooum.data.member.repository.MemberRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

public class CommentCardRepositoryTest extends DataJpaTestSupport {

    @Autowired
    private MemberRepository memberRepository;
    @Autowired
    private FeedCardRepository feedCardRepository;
    @Autowired
    private CommentCardRepository commentCardRepository;

    @DisplayName("피드카드 pk로 그 하위에 작성된 모든 답카드를 조회한다.")
    @Test
    void name() {
        //given
        FeedCard feedCard1 = saveFeedCard();

        CommentCard commentCard1_1 = saveCommentCardBy(feedCard1, feedCard1);
        CommentCard commentCard1_1_1 = saveCommentCardBy(feedCard1, commentCard1_1);
        CommentCard commentCard1_2 = saveCommentCardBy(feedCard1, feedCard1);
        CommentCard commentCard1_2_1 = saveCommentCardBy(feedCard1, commentCard1_2);

        //when
        List<CommentCard> commentCards = commentCardRepository.findCommentCardsIn(List.of(feedCard1.getPk()));

        //then
        assertThat(commentCards)
                .hasSize(4)
                .extracting(Card::getPk)
                .containsExactly(commentCard1_1.getPk(),
                        commentCard1_1_1.getPk(),
                        commentCard1_2.getPk(),
                        commentCard1_2_1.getPk()
                );
    }

    private CommentCard saveCommentCardBy(Card masterCard, Card parentCard) {
        return commentCardRepository.save(
                CommentCard.builder()
                        .fontSize(FontSize.NONE)
                        .font(Font.PRETENDARD)
                        .imgType(CardImgType.DEFAULT)
                        .writer(saveMember())
                        .writerIp(UUID.randomUUID().toString())
                        .content(UUID.randomUUID().toString())
                        .parentCardPk(parentCard.getPk())
                        .masterCard(masterCard.getPk())
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
