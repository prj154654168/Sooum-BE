package com.sooum.api.tag.service;

import com.sooum.api.IntegrationTestSupport;
import com.sooum.api.tag.dto.TagDto;
import com.sooum.data.block.entity.Block;
import com.sooum.data.block.repository.BlockRepository;
import com.sooum.data.card.entity.CommentCard;
import com.sooum.data.card.entity.FeedCard;
import com.sooum.data.card.entity.font.Font;
import com.sooum.data.card.entity.fontsize.FontSize;
import com.sooum.data.card.entity.imgtype.CardImgType;
import com.sooum.data.card.entity.parenttype.CardType;
import com.sooum.data.card.repository.CommentCardRepository;
import com.sooum.data.card.repository.FeedCardRepository;
import com.sooum.data.member.entity.Member;
import com.sooum.data.member.entity.devicetype.DeviceType;
import com.sooum.data.member.repository.MemberRepository;
import com.sooum.data.tag.entity.CommentTag;
import com.sooum.data.tag.entity.FavoriteTag;
import com.sooum.data.tag.entity.FeedTag;
import com.sooum.data.tag.entity.Tag;
import com.sooum.data.tag.repository.CommentTagRepository;
import com.sooum.data.tag.repository.FavoriteTagRepository;
import com.sooum.data.tag.repository.FeedTagRepository;
import com.sooum.data.tag.repository.TagRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.*;

public class FavoriteTagUseCaseTest extends IntegrationTestSupport {

    @Autowired
    private MemberRepository memberRepository;
    @Autowired
    private FeedCardRepository feedCardRepository;
    @Autowired
    private CommentCardRepository commentCardRepository;
    @Autowired
    private TagRepository tagRepository;
    @Autowired
    private FavoriteTagRepository favoriteTagRepository;
    @Autowired
    private FeedTagRepository feedTagRepository;
    @Autowired
    private CommentTagRepository commentTagRepository;
    @Autowired
    private BlockRepository blockRepository;
    @Autowired
    private FavoriteTagUseCase favoriteTagUseCase;

    @DisplayName("즐겨찾기 태그 리스트의 첫번째 페이지를 조회한다.")
    @Test
    void findTop5FeedByFavoriteTags() {
        Member requester = saveMember();
        Member feedCardWriter1 = saveMember();
        Member feedCardWriter2 = saveMember();

        //태그 생성 : tag-content1 ... 5
        List<Tag> tags = IntStream.rangeClosed(1,5)
                .mapToObj(i -> saveTag("tag-content" + i))
                .toList();

        saveFavoriteTags(requester, tags);

        FeedCard feedCard1 = saveFeedCard(feedCardWriter1, "feedcard1 content");
        FeedCard feedCard2 = saveFeedCard(feedCardWriter2, "feedcard2 content");
        FeedCard feedCard3 = saveFeedCard(feedCardWriter2, "feedcard3 content");
        FeedCard feedCard4 = saveFeedCard(feedCardWriter2, "feedcard4 content");
        FeedCard feedCard5 = saveFeedCard(feedCardWriter2, "feedcard5 content");
        saveFeedTags(feedCard1, tags.subList(0, 2));
        saveFeedTags(feedCard2, tags.subList(0, 2));
        saveFeedTags(feedCard3, tags.subList(2, 5));
        saveFeedTags(feedCard4, tags.subList(2, 5));
        saveFeedTags(feedCard5, tags.subList(2, 5));

        //when
        List<TagDto.FavoriteTag> favoriteTagList = favoriteTagUseCase.findTop5FeedByFavoriteTags(requester.getPk(), Optional.empty());

        //then
        assertThat(favoriteTagList.get(0).getPreviewCards())
                .hasSize(3)
                .extracting(TagDto.FavoriteTag.PreviewCard::getContent)
                .containsExactly("feedcard3 content","feedcard4 content", "feedcard5 content");

        assertThat(favoriteTagList.get(3))
                .extracting(TagDto.FavoriteTag::getTagContent)
                .isEqualTo("tag-content2");
    }

    @DisplayName("미리보기 카드가 차단한 유저만 있는 경우 제외하고 태그 20개를 채워서 조회한다.")
    @Test
    void findTop5FeedByFavoriteTags2() {
        //given
        Member requester = saveMember();
        Member blockedWriter = saveMember();

        //태그 생성 : tag-content1 ... 25
        List<Tag> tags = IntStream.rangeClosed(1, 25)
                .mapToObj(i -> saveTag("tag-content" + i))
                .toList();

        saveFavoriteTags(requester, tags);

        //차단하지 않은 유저가 작성한 피드카드 저장
        FeedCard notBlockWriterFeedCard = saveFeedCard(requester, "notBlockWriterFeedCard");
        saveFeedTags(notBlockWriterFeedCard, tags.subList(0, 20)); //0~19(20개)

        //차단된 유저가 작성한 피드카드 저장
        saveBlockBy(requester, blockedWriter);
        FeedCard blockWriterFeedCard = saveFeedCard(blockedWriter, "blockWriterFeedCard");
        saveFeedTags(blockWriterFeedCard, tags.subList(20, 25)); //20~24(5개)

        //when
        List<TagDto.FavoriteTag> favoriteTagList = favoriteTagUseCase.findTop5FeedByFavoriteTags(requester.getPk(), Optional.empty());

        //then
        assertThat(favoriteTagList).hasSize(20);
        assertThat(favoriteTagList.get(0).getPreviewCards())
                .hasSize(1)
                .extracting(TagDto.FavoriteTag.PreviewCard::getContent)
                .containsExactly("notBlockWriterFeedCard");
    }

    @DisplayName("미리보기 카드가 답카드만 있는 경우 제외하고 태그 20개를 채워서 조회한다.")
    @Test
    void findTop5FeedByFavoriteTags3() {
        //given
        Member requester = saveMember();
        Member commentCardWriter = saveMember();

        //태그 생성 : tag-content1 ... 30
        List<Tag> tags = IntStream.rangeClosed(1, 30)
                .mapToObj(i -> saveTag("tag-content" + i))
                .toList();

        saveFavoriteTags(requester, tags);

        //피드카드 저장
        FeedCard feedCard1 = saveFeedCard(requester, "feedCard1");
        saveFeedTags(feedCard1, tags.subList(0, 14));

        //답카드 저장
        CommentCard commentCard = saveCommentCard(commentCardWriter, feedCard1, "commentCard");
        saveCommentTags(commentCard, tags.subList(14, 20));

        //이후 피드카드 저장
        FeedCard feedCard2 = saveFeedCard(requester, "feedCard2");
        saveFeedTags(feedCard2, tags.subList(20, 29));

        //when
        List<TagDto.FavoriteTag> favoriteTagList = favoriteTagUseCase.findTop5FeedByFavoriteTags(requester.getPk(), Optional.empty());

        //then
        assertThat(favoriteTagList).hasSize(20);
        assertThat(favoriteTagList.get(8).getPreviewCards())
                .hasSize(1)
                .extracting(TagDto.FavoriteTag.PreviewCard::getContent)
                .containsExactly("feedCard2");
        assertThat(favoriteTagList.get(9).getPreviewCards())
                .hasSize(1)
                .extracting(TagDto.FavoriteTag.PreviewCard::getContent)
                .containsExactly("feedCard1");
    }

    @DisplayName("미리보기 카드가 삭제된 카드만 있는 경우 제외하고 태그 20개를 채워서 조회한다.")
    @Test
    void findTop5FeedByFavoriteTags4() {
        //given
        Member member1 = saveMember();
        Member member2 = saveMember();

        //태그 생성 : tag-content1 ... 30
        List<Tag> tags = IntStream.rangeClosed(1, 30)
                .mapToObj(i -> saveTag("tag-content" + i))
                .toList();

        saveFavoriteTags(member1, tags);

        //member1의 피드카드 저장
        FeedCard feedCard1 = saveFeedCard(member1, "feedCard1");
        saveFeedTags(feedCard1, tags.subList(0, 14));

        //member2의 피드카드 저장
        FeedCard feedCard2 = saveFeedCard(member2, "feedCard2");
        List<FeedTag> feedTagsByFeedCard2 = saveFeedTags(feedCard2, tags.subList(14, 20));

        //member1의 피드카드 저장
        FeedCard feedCard3 = saveFeedCard(member1, "feedCard3");
        saveFeedTags(feedCard3, tags.subList(20, 29));

        //member2의 피드카드 삭제
        deleteFeedCard(feedCard2.getPk(), feedTagsByFeedCard2);

        //when
        List<TagDto.FavoriteTag> favoriteTagList = favoriteTagUseCase.findTop5FeedByFavoriteTags(member1.getPk(), Optional.empty());

        //then
        assertThat(favoriteTagList).hasSize(20);
        assertThat(favoriteTagList.get(8).getPreviewCards())
                .hasSize(1)
                .extracting(TagDto.FavoriteTag.PreviewCard::getContent)
                .containsExactly("feedCard3");
        assertThat(favoriteTagList.get(9).getPreviewCards())
                .hasSize(1)
                .extracting(TagDto.FavoriteTag.PreviewCard::getContent)
                .containsExactly("feedCard1");

    }

    private Member saveMember(){
        return memberRepository.save(Member.builder()
                .deviceId(UUID.randomUUID().toString())
                .deviceType(DeviceType.IOS)
                .nickname(UUID.randomUUID().toString())
                .build());
    }

    private FeedCard saveFeedCard(Member member, String content) {
        return feedCardRepository.save(FeedCard.builder()
                .fontSize(FontSize.NONE)
                .font(Font.PRETENDARD)
                .imgType(CardImgType.DEFAULT)
                .writer(member)
                .isPublic(true)
                .isFeedActive(true)
                .writerIp(UUID.randomUUID().toString())
                .content(content)
                .build());
    }

    private CommentCard saveCommentCard(Member member, FeedCard feedCard, String content) {
        return commentCardRepository.save(CommentCard.builder()
                .fontSize(FontSize.NONE)
                .font(Font.PRETENDARD)
                .masterCard(feedCard.getPk())
                .parentCardPk(feedCard.getPk())
                .content(content)
                .imgType(CardImgType.DEFAULT)
                .writerIp(UUID.randomUUID().toString())
                .writer(member)
                .parentCardType(CardType.FEED_CARD)
                .build());
    }

    private Tag saveTag(String content) {
        return tagRepository.save(Tag.ofFeed(content, true));
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

    private List<CommentTag> saveCommentTags(CommentCard commentCard, List<Tag> tags) {
        return tags.stream()
                .map(tag -> commentTagRepository.save(CommentTag.builder()
                        .tag(tag)
                        .commentCard(commentCard)
                        .build()))
                .collect(Collectors.toList());
    }

    private Block saveBlockBy(Member fromMember, Member toMember) {
        return blockRepository.save(Block.builder()
                .fromMember(fromMember)
                .toMember(toMember)
                .build()
        );
    }
    private void deleteFeedCard(Long feedCardPk, List<FeedTag> feedTags) {
        feedTagRepository.deleteAll(feedTags);
        feedCardRepository.deleteById(feedCardPk);
    }
}
