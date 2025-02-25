package com.sooum.api.card.service;

import com.sooum.api.IntegrationTestSupport;
import com.sooum.api.card.dto.LatestFeedCardDto;
import com.sooum.data.block.entity.Block;
import com.sooum.data.block.repository.BlockRepository;
import com.sooum.data.card.entity.Card;
import com.sooum.data.card.entity.CommentCard;
import com.sooum.data.card.entity.FeedCard;
import com.sooum.data.card.entity.FeedLike;
import com.sooum.data.card.entity.font.Font;
import com.sooum.data.card.entity.fontsize.FontSize;
import com.sooum.data.card.entity.imgtype.CardImgType;
import com.sooum.data.card.repository.CommentCardRepository;
import com.sooum.data.card.repository.FeedCardRepository;
import com.sooum.data.card.repository.FeedLikeRepository;
import com.sooum.data.member.entity.Member;
import com.sooum.data.member.entity.devicetype.DeviceType;
import com.sooum.data.member.repository.MemberRepository;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;

@Slf4j
class LatestFeedUseCaseTest extends IntegrationTestSupport {

    @Autowired
    private BlockRepository blockRepository;
    @Autowired
    private MemberRepository memberRepository;
    @Autowired
    private FeedCardRepository feedCardRepository;
    @Autowired
    private CommentCardRepository commentCardRepository;
    @Autowired
    private FeedLikeRepository feedLikeRepository;

    @Autowired
    private LatestFeedUseCase latestFeedUseCase;

    @AfterEach
    void tearDown() {
        feedLikeRepository.deleteAllInBatch();
        commentCardRepository.deleteAllInBatch();
        feedCardRepository.deleteAllInBatch();
        blockRepository.deleteAllInBatch();
        memberRepository.deleteAllInBatch();

    }

    @DisplayName("최신글 피드의 첫번째 페이지를 조회한다.")
    @Test
    void createLatestFeedInfo() {
        //given
        Member requester = saveMember();
        Member feedCardWriter1 = saveMember();
        Member feedCardWriter2 = saveMember();

        saveBlockBy(requester, feedCardWriter2); //feedCardWriter2가 작성한 카드는 조회되면 안됨.

        FeedCard feed1ByWriter1 = saveFeedCardBy(feedCardWriter1, "content1 by feedCardWriter1");
        FeedCard feed2ByWriter2 = saveFeedCardBy(feedCardWriter2,"content2 by feedCardWriter2");
        FeedCard feed3ByWriter1 = saveFeedCardBy(feedCardWriter1,"content3 by feedCardWriter1");
        FeedCard feed4ByWriter2 = saveFeedCardBy(feedCardWriter2,"content4 by feedCardWriter2");
        FeedCard feed5ByWriter1 = saveFeedCardBy(feedCardWriter1,"content5 by feedCardWriter1");

        //requester가 feed1ByWriter1 에 공감
        saveFeedLikeBy(requester, feed1ByWriter1);

        //requester가 feed3ByWriter1 에 답카드 작성
        saveCommentCardBy(feed3ByWriter1, feed3ByWriter1, requester);
        //다른 유저가 feed5ByWriter1 에 답카드 작성
        CommentCard feed5ByWriter1_comment1BySomeone = saveCommentCardBy(feed5ByWriter1, feed5ByWriter1);
        //Requester가 feed5ByWriter1_comment1BySomeone 답카드에 답카드 작성
        saveCommentCardBy(feed5ByWriter1, feed5ByWriter1_comment1BySomeone, requester);

        //when
        List<LatestFeedCardDto> latestFeedInfo = latestFeedUseCase.createLatestFeedInfo(Optional.empty(), requester.getPk(), Optional.empty(), Optional.empty());

        //then
        assertThat(latestFeedInfo)
                .hasSize(3)
                .extracting(
                        LatestFeedCardDto::getContent,
                        LatestFeedCardDto::getCommentCnt,
                        LatestFeedCardDto::isCommentWritten,
                        LatestFeedCardDto::getLikeCnt,
                        LatestFeedCardDto::isLiked
                )
                .containsExactly(
                        //답카드의 답카드에 작성하면 피드에서 답카드 작성 여부 false
                        //답카드의 답카드까지 답글 개수에 포함되지 않음
                        tuple("content5 by feedCardWriter1",1,false,0,false),
                        //피드카드에 답카드 작성하면 답카드 작성 여부 true
                        tuple("content3 by feedCardWriter1",1,true,0,false),
                        //피드카드에 공감하면 공감 개수 1증가되며, 공감 여부 true
                        tuple("content1 by feedCardWriter1",0,false,1,true)
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

    private Block saveBlockBy(Member fromMember, Member toMember) {
        return blockRepository.save(Block.builder()
                .fromMember(fromMember)
                .toMember(toMember)
                .build()
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

    private CommentCard saveCommentCardBy(Card masterCard, Card parentCard, Member writer) {
        return commentCardRepository.save(
                CommentCard.builder()
                        .fontSize(FontSize.NONE)
                        .font(Font.PRETENDARD)
                        .imgType(CardImgType.DEFAULT)
                        .writer(writer)
                        .writerIp(UUID.randomUUID().toString())
                        .content(UUID.randomUUID().toString())
                        .parentCardPk(parentCard.getPk())
                        .masterCard(masterCard.getPk())
                        .build()
        );
    }

    private FeedCard saveFeedCardBy(Member writer, String content) {
        return feedCardRepository.save(FeedCard.builder()
                .fontSize(FontSize.NONE)
                .font(Font.PRETENDARD)
                .imgType(CardImgType.DEFAULT)
                .writer(writer)
                .isPublic(true)
                .isFeedActive(true)
                .writerIp(UUID.randomUUID().toString())
                .content(content)
                .build()
        );
    }

    private FeedLike saveFeedLikeBy(Member member, FeedCard feedCard) {
        return feedLikeRepository.save(FeedLike.builder()
                .likedMember(member)
                .targetCard(feedCard)
                .build()
        );
    }
}