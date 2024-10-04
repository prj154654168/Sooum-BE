package com.sooum.core.domain.card.service;

import com.sooum.core.domain.block.service.BlockMemberService;
import com.sooum.core.domain.card.dto.PopularCardRetrieve;
import com.sooum.core.domain.card.entity.CommentCard;
import com.sooum.core.domain.card.entity.FeedCard;
import com.sooum.core.domain.card.entity.FeedLike;
import com.sooum.core.domain.card.entity.font.Font;
import com.sooum.core.domain.card.entity.fontsize.FontSize;
import com.sooum.core.domain.card.entity.imgtype.ImgType;
import com.sooum.core.domain.card.entity.parenttype.CardType;
import com.sooum.core.domain.card.repository.PopularFeedRepository;
import com.sooum.core.domain.img.service.LocalImgService;
import com.sooum.core.domain.member.entity.Member;
import com.sooum.core.domain.member.entity.devicetype.DeviceType;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.hateoas.Link;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.any;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class PopularFeedServiceTest {
    @Mock
    PopularFeedRepository popularFeedRepository;
    @Mock
    LocalImgService localImgService;
    @Mock
    FeedLikeService feedLikeService;
    @Mock
    CommentCardService commentCardService;
    @Mock
    BlockMemberService blockMemberService;
    @InjectMocks
    PopularFeedService popularFeedService;
    private static final int MEMBER_SIZE = 2;
    private static final int CARD_SIZE = 4;

    @Test
    void findHomePopularFeeds() {
        /// given
        List<Member> members = createMembers();
        List<FeedCard> feedCards = createFeedCards(members);
        given(popularFeedRepository.findPopularFeeds(any())).willReturn(feedCards);
        given(blockMemberService.filterBlockedMembers(eq(feedCards), any())).willReturn(feedCards);
        given(feedLikeService.findByTargetCards(any())).willReturn(createFeedLikes(feedCards, members));
        given(commentCardService.findByTargetList(any())).willReturn(createCommentCards(feedCards, members));
        given(localImgService.findImgUrl(any(), any())).willReturn(Link.of("dummyUrl"));

        // when
        List<PopularCardRetrieve> popularFeeds = popularFeedService
                .findHomePopularFeeds(Optional.empty(), Optional.empty(), 1L);

        // then
        for (int i = 0; i < CARD_SIZE; i++) {
            Assertions.assertThat(popularFeeds.get(i).getContent()).isEqualTo(feedCards.get(i).getContent());
            Assertions.assertThat(popularFeeds.get(i).getFont()).isEqualTo(feedCards.get(i).getFont());
            Assertions.assertThat(popularFeeds.get(i).getFontSize()).isEqualTo(feedCards.get(i).getFontSize());
            Assertions.assertThat(popularFeeds.get(i).isStory()).isEqualTo(feedCards.get(i).isStory());
        }
    }

    private List<FeedLike> createFeedLikes(List<FeedCard> targetCards, List<Member> likedMembers) {
        ArrayList<FeedLike> feedLikes = new ArrayList<>();
        for (int i = 0; i < CARD_SIZE; i++) {
            FeedLike feedLike = FeedLike.builder()
                    .targetCard(targetCards.get(i))
                    .likedMember(likedMembers.get(i % MEMBER_SIZE))
                    .build();
            feedLikes.add(feedLike);
        }
        return feedLikes;
    }

    private List<CommentCard> createCommentCards(List<FeedCard> feedCards, List<Member> members) {
        ArrayList<CommentCard> commentCards = new ArrayList<>();
        for (int i = 0; i < CARD_SIZE; i++) {
            CommentCard commentCard = CommentCard.builder()
                    .content("카드 내용 " + i)
                    .fontSize(FontSize.BIG)
                    .font(Font.PRETENDARD)
                    .location(null)
                    .imgType(ImgType.DEFAULT)
                    .imgName(i + ".jpg")
                    .writer(members.get(i % MEMBER_SIZE))
                    .masterCard(feedCards.get(i).getPk())
                    .parentCardType(CardType.FEED_CARD)
                    .parentCardPk(feedCards.get(i % (CARD_SIZE / 2)).getPk())
                    .build();
            commentCards.add(commentCard);
        }
        return commentCards;
    }

    private List<FeedCard> createFeedCards(List<Member> members) {
        ArrayList<FeedCard> feedCards = new ArrayList<>();
        for (int i = 0; i < CARD_SIZE; i++) {
            FeedCard feedCard = FeedCard.builder()
                    .content("카드 내용 " + i)
                    .fontSize(FontSize.BIG)
                    .font(Font.PRETENDARD)
                    .location(null)
                    .imgType(ImgType.DEFAULT)
                    .imgName(i + ".jpg")
                    .isPublic(true)
                    .isStory(false)
                    .writer(members.get(i % MEMBER_SIZE))
                    .build();
            ReflectionTestUtils.setField(feedCard, "pk", (long) i + 1);

            feedCards.add(feedCard);
        }
        return feedCards;
    }

    private List<Member> createMembers() {
        ArrayList<Member> members = new ArrayList<>();
        for (int i = 0; i < MEMBER_SIZE; i++) {
            Member member = Member.builder()
                    .deviceId("dev" + i)
                    .deviceType(DeviceType.IOS)
                    .firebaseToken("firbaseDummy")
                    .nickname("nickname" + i)
                    .isAllowNotify(true)
                    .build();
            ReflectionTestUtils.setField(member, "pk", (long) i + 1);
            members.add(member);
        }
        return members;
    }
}