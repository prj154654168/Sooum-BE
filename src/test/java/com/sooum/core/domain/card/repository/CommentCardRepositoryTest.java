package com.sooum.core.domain.card.repository;

import com.sooum.core.domain.card.entity.CommentCard;
import com.sooum.core.domain.card.entity.FeedCard;
import com.sooum.core.domain.card.entity.font.Font;
import com.sooum.core.domain.card.entity.fontsize.FontSize;
import com.sooum.core.domain.card.entity.imgtype.ImgType;
import com.sooum.core.domain.card.entity.parenttype.CardType;
import com.sooum.core.domain.member.entity.Member;
import com.sooum.core.domain.member.entity.devicetype.DeviceType;
import com.sooum.core.domain.member.repository.MemberRepository;
import com.sooum.core.domain.tag.repository.CachedTagRepository;
import com.sooum.core.global.config.redis.RedisConfig;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.*;
import java.util.stream.IntStream;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import(RedisConfig.class)
@MockBean(CachedTagRepository.class)
class CommentCardRepositoryTest {
    @Autowired
    MemberRepository memberRepository;
    @Autowired
    FeedCardRepository feedCardRepository;
    @Autowired
    CommentCardRepository commentCardRepository;
    private static final int CARD_SIZE = 25;
    private static final int TEST_PAGE_SIZE = 5;

    @BeforeEach
    private void init() {
        Member member = createMember();
        FeedCard feedCard = createFeedCard(member);
        createCommentCards(feedCard, member);
    }

    @Test
    @DisplayName("댓글 개수 조회 성공")
    void countCommentSuccess() throws Exception{
        // given
        FeedCard feedCard = feedCardRepository.findAll().get(0);
        List<CommentCard> commentCards = commentCardRepository.findChildCards(feedCard.getPk());

        // when
        Integer result = commentCardRepository.countCommentsByParentCard(feedCard.getPk());

        // then
        long isNotDeletedCommentsCount = commentCards.stream().
                filter(comment -> !comment.isDeleted())
                .count();
        Assertions.assertThat(result).isEqualTo(isNotDeletedCommentsCount);
    }

    @Test
    @DisplayName("lastPk 없을 때 comment 조회")
    void findCommentWithoutLastPk() throws Exception{
        // given
        FeedCard feedCard = feedCardRepository.findAll().get(0);

        // when
        List<CommentCard> result = commentCardRepository.findCommentsInfo(feedCard.getPk(), PageRequest.of(0, TEST_PAGE_SIZE));

        // then
        boolean isPassCond = result.stream().allMatch(
                comment -> !comment.isDeleted() && comment.getParentCardPk().equals(feedCard.getPk()));
        Assertions.assertThat(isPassCond).isTrue();
    }

    @Test
    @DisplayName("lastPk 있을 때 comment 조회")
    void findCommentByLastPk() throws Exception{
        // given
        FeedCard feedCard = feedCardRepository.findAll().get(0);
        List<CommentCard> allComments = commentCardRepository.findAll().stream().sorted(Comparator.comparing(CommentCard::getParentCardPk).reversed()).toList();
        // when
        long lastPk = allComments.get(allComments.size() - 5).getPk();
        List<CommentCard> result = commentCardRepository.findCommentsInfoByLastPk(feedCard.getPk(), lastPk, PageRequest.of(0, TEST_PAGE_SIZE));

        // then
        boolean isPassCond = result.stream().allMatch(
                comment -> !comment.isDeleted() && comment.getParentCardPk().equals(feedCard.getPk()));
        Assertions.assertThat(isPassCond).isTrue();
        boolean isNotExistedLastPkComment = result.stream().noneMatch(comment -> comment.getPk().equals(lastPk));
        Assertions.assertThat(isNotExistedLastPkComment).isTrue();
    }

    @Test
    @DisplayName("사용자가 작성한 댓글 카드 첫 번째 페이지 조회")
    void findMyComments() throws Exception{
        //given
        Member member = createMemberForMyComments();
        FeedCard feedCard = createFeedCardForMyComments(member);
        List<CommentCard> comments = createCommentCardsForMyComments(feedCard, member);
        Collections.reverse(comments);

        //when
        List<CommentCard> result = commentCardRepository.findCommentCardsFirstPage(member.getPk(), PageRequest.ofSize(30));

        //then
        long count = IntStream.range(0, result.size()).filter(idx -> {
            Long resultPk = result.get(idx).getPk();
            Long commentPk = comments.get(idx).getPk();
            return resultPk.equals(commentPk);
        }).count();
        Assertions.assertThat(count).isEqualTo(30);
    }

    @Test
    @DisplayName("사용자가 작성한 댓글 카드 두번째 페이지 조회 - 25번째 카드 이후 반환")
    void findMyCommentsByLastId() throws Exception{
        //given
        Member member = createMemberForMyComments();
        FeedCard feedCard = createFeedCardForMyComments(member);
        List<CommentCard> comments = createCommentCardsForMyComments(feedCard, member);
        Collections.reverse(comments);

        List<CommentCard> allComments = commentCardRepository.findCommentCardsFirstPage(member.getPk(), PageRequest.of(0, 30));
        long lastPk = allComments.get(24).getPk();

        //when
        List<CommentCard> result = commentCardRepository.findCommentCardsNextPage(member.getPk(), lastPk, PageRequest.of(0, 30));
        List<CommentCard> expectedComments = comments.subList(25, comments.size());

        //then

        long count = IntStream.range(0, result.size()).filter(idx -> {
            Long resultPk = result.get(idx).getPk();
            Long commentPk = expectedComments.get(idx).getPk();
            return resultPk.equals(commentPk);
        }).count();
        Assertions.assertThat(count).isEqualTo(25);
    }

    private Member createMemberForMyComments() {
        Member member = Member.builder()
                .deviceType(DeviceType.ANDROID)
                .deviceId("firstPageTestMember")
                .firebaseToken("token")
                .nickname("nickname")
                .isAllowNotify(true)
                .build();
        return memberRepository.save(member);
    }

    private FeedCard createFeedCardForMyComments(Member member) {
        FeedCard feedCard = FeedCard.builder()
                .content("content")
                .fontSize(FontSize.BIG)
                .font(Font.PRETENDARD)
                .location(null)
                .imgType(ImgType.DEFAULT)
                .imgName(1 + ".jpg")
                .isPublic(true)
                .isStory(false)
                .writer(member)
                .build();
        return feedCardRepository.save(feedCard);
    }


    private List<CommentCard> createCommentCardsForMyComments(FeedCard feedCard, Member member) {
        ArrayList<CommentCard> commentCards = new ArrayList<>();
        for (int i = 0; i < 50; i++) {
            CommentCard commentCard = CommentCard.builder()
                    .content("카드 내용 " + i)
                    .fontSize(FontSize.BIG)
                    .font(Font.PRETENDARD)
                    .location(null)
                    .imgType(ImgType.DEFAULT)
                    .imgName(i + ".jpg")
                    .writer(member)
                    .masterCard(feedCard.getPk())
                    .parentCardType(i % 2 == 0 ? CardType.FEED_CARD : CardType.COMMENT_CARD)
                    .parentCardPk(feedCard.getPk())
                    .build();
            commentCards.add(commentCard);
        }
        return commentCardRepository.saveAll(commentCards);
    }

    private void createCommentCards(FeedCard feedCard, Member member) {
        ArrayList<CommentCard> commentCards = new ArrayList<>();
        for (int i = 0; i < CARD_SIZE; i++) {
            CommentCard commentCard = CommentCard.builder()
                    .content("카드 내용 " + i)
                    .fontSize(FontSize.BIG)
                    .font(Font.PRETENDARD)
                    .location(null)
                    .imgType(ImgType.DEFAULT)
                    .imgName(i + ".jpg")
                    .writer(member)
                    .masterCard(feedCard.getPk())
                    .parentCardType(i % 2 == 0 ? CardType.FEED_CARD : CardType.COMMENT_CARD)
                    .parentCardPk(feedCard.getPk())
                    .build();
            if (i % 2 == 0) {
                ReflectionTestUtils.setField(commentCard, "isDeleted", false);
            }
            commentCards.add(commentCard);
        }
        commentCardRepository.saveAll(commentCards);
    }

    private FeedCard createFeedCard(Member member) {
            FeedCard feedCard = FeedCard.builder()
                    .content("카드 내용 ")
                    .fontSize(FontSize.BIG)
                    .font(Font.PRETENDARD)
                    .location(null)
                    .imgType(ImgType.DEFAULT)
                    .imgName(1 + ".jpg")
                    .isPublic(true)
                    .isStory(false)
                    .writer(member)
                    .build();
        return feedCardRepository.save(feedCard);
    }

    private Member createMember() {
        Member member = Member.builder()
                .deviceId("dev")
                .deviceType(DeviceType.IOS)
                .firebaseToken("firbaseDummy")
                .nickname("nickname")
                .isAllowNotify(true)
                .build();
        return memberRepository.save(member);
    }
}