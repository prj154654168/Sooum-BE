package com.sooum.core.domain.card.service;

import com.sooum.core.domain.card.dto.CreateCardDto;
import com.sooum.core.domain.card.dto.CreateCommentDto;
import com.sooum.core.domain.card.dto.CreateFeedCardDto;
import com.sooum.core.domain.card.entity.*;
import com.sooum.core.domain.card.entity.imgtype.ImgType;
import com.sooum.core.domain.card.entity.parenttype.CardType;
import com.sooum.core.domain.img.service.ImgService;
import com.sooum.core.domain.img.service.UserImgService;
import com.sooum.core.domain.member.entity.Member;
import com.sooum.core.domain.member.service.MemberService;
import com.sooum.core.domain.report.service.CommentReportService;
import com.sooum.core.domain.report.service.FeedReportService;
import com.sooum.core.domain.report.service.ReportService;
import com.sooum.core.domain.tag.entity.CommentTag;
import com.sooum.core.domain.tag.entity.FeedTag;
import com.sooum.core.domain.tag.entity.Tag;
import com.sooum.core.domain.tag.service.CommentTagService;
import com.sooum.core.domain.tag.service.FeedTagService;
import com.sooum.core.domain.tag.service.TagService;
import com.sooum.core.global.exceptionmessage.ExceptionMessage;
import com.sooum.core.global.regex.BadWordFiltering;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class FeedService {
    private final MemberService memberService;
    private final CommentCardService commentCardService;
    private final FeedCardService feedCardService;
    private final PopularFeedService popularFeedService;
    private final FeedLikeService feedLikeService;
    private final TagService tagService;
    private final FeedTagService feedTagService;
    private final ImgService imgService;
    private final CommentTagService commentTagService;
    private final UserImgService userImgService;
    private final BadWordFiltering badWordFiltering;
    private final CommentLikeService commentLikeService;
    private final ReportService reportService;
    private final CommentReportService commentReportService;
    private final FeedReportService feedReportService;

    @Transactional
    @Retryable(
            retryFor = {ObjectOptimisticLockingFailureException.class},
        maxAttempts = 5,
        backoff = @Backoff(delay = 25, multiplier = 2))
    public void createFeedCard(Long memberPk, CreateFeedCardDto cardDto) {
        if (checkForTagsInStory(cardDto)) {
            throw new RuntimeException(ExceptionMessage.TAGS_NOT_ALLOWED_FOR_STORY.getMessage());
        }

        if (isUserImage(cardDto)) {
            validateUserImage(cardDto);
        }

        Member member = memberService.findByPk(memberPk);

        FeedCard feedCard = cardDto.of(member);
        feedCardService.saveFeedCard(feedCard);

        if (isUserImage(cardDto)){
            userImgService.saveUserUploadPic(feedCard, cardDto.getImgName());
        }

        List<Tag> tagContents = processTags(cardDto);
        List<FeedTag> feedTagList = tagContents.stream()
                .map(tag -> FeedTag.builder().feedCard(feedCard).tag(tag).build())
                .toList();
        feedTagService.saveAll(feedTagList);
    }

    @Transactional
    public void createCommentCard(Long memberPk, Long cardPk, CreateCommentDto cardDto) {
        if (isUserImage(cardDto)) {
            validateUserImage(cardDto);
        }

        Member member = memberService.findByPk(memberPk);

        Card card = feedCardService.isExistFeedCard(cardPk)
                ? feedCardService.findFeedCard(cardPk)
                : commentCardService.findCommentCard(cardPk);

        CommentCard commentCard;
        if (card instanceof FeedCard) {
            commentCard = cardDto.of(CardType.FEED_CARD, cardPk, cardPk, member);
        } else if (card instanceof CommentCard commentParentCard) {
            commentCard = cardDto.of(CardType.COMMENT_CARD, commentParentCard.getPk(), commentParentCard.getParentCardPk(), member);
        } else throw new IllegalArgumentException(ExceptionMessage.UNHANDLED_OBJECT.getMessage());

        commentCardService.saveComment(commentCard);

        if (isUserImage(cardDto)){
            userImgService.saveUserUploadPic(commentCard, cardDto.getImgName());
        }

        List<Tag> tagContents = processTags(cardDto);
        List<CommentTag> commentTagList = tagContents.stream()
                .map(tag -> CommentTag.builder().commentCard(commentCard).tag(tag).build())
                .toList();

        commentTagService.saveAll(commentTagList);
    }

    private List<Tag> processTags(CreateCardDto cardDto){
        List<Tag> tagContents = tagService.findTagList(cardDto.getTags());
        tagService.incrementTagCount(tagContents);

        if(hasTags(cardDto)) {
            List<String> list = tagContents.stream().map(Tag::getContent).toList();
            cardDto.getTags().removeAll(list);

            List<Tag> tagList = cardDto.getTags().stream()
                    .map(tagContent -> Tag.builder().content(tagContent).isActive(badWordFiltering.checkBadWord(tagContent)).build())
                    .toList();

            tagService.saveAll(tagList);
            tagContents.addAll(tagList);
        }

        return tagContents;
    }

    private void validateUserImage(CreateCardDto cardDto) {
        if (imgService.isModeratingImg(cardDto.getImgName())) {
            throw new EntityNotFoundException(ExceptionMessage.IMAGE_REJECTED_BY_MODERATION.getMessage());
        }

        if(!imgService.verifyImgSaved(cardDto.getImgName())) {
            throw new EntityNotFoundException(ExceptionMessage.IMAGE_NOT_FOUND.getMessage());
        }
    }

    private static boolean checkForTagsInStory(CreateFeedCardDto cardDto) {
        return cardDto.isStory() && hasTags(cardDto);
    }

    private static boolean hasTags(CreateCardDto cardDto) {
        return cardDto.getTags() != null && !cardDto.getTags().isEmpty();
    }

    private static boolean isUserImage(CreateCardDto cardDto) {
        return cardDto.getImgType().equals(ImgType.USER);
    }


    public Card findParentCard(CommentCard commentCard) {
        if(commentCard.getParentCardType().equals(CardType.COMMENT_CARD)){
            return commentCardService.findByPk(commentCard.getParentCardPk());
        }
        if (commentCard.getParentCardType().equals(CardType.FEED_CARD)){
            return feedCardService.findByPk(commentCard.getParentCardPk());
        }
        throw new IllegalArgumentException(ExceptionMessage.UNHANDLED_TYPE.getMessage());
    }

    @Transactional
    public void deleteCard(Long cardPk, Long writerPk) {
        CardType cardType = commentCardService.findCardType(cardPk);
        switch (cardType) {
            case FEED_CARD -> deleteFeedCard(cardPk, writerPk);
            case COMMENT_CARD -> deleteCommentCard(cardPk, writerPk);
            default -> throw new EntityNotFoundException(ExceptionMessage.CARD_NOT_FOUND.getMessage());
        }
    }

    private void deleteCommentCard(Long commentCardPk, Long writerPk) {
        if (isNotCommentCardOwner(commentCardPk, writerPk)) return;
        CommentCard commentCard = commentCardService.findCommentCard(commentCardPk);
        commentCard.setDeleted(true);
        commentTagService.deleteByCommentCardPk(commentCardPk);
        userImgService.deleteUserUploadPic(commentCard.getImgName());
        commentLikeService.deleteAllFeedLikes(commentCardPk);
        commentReportService.deleteReport(commentCard);
        //todo notification delete
    }

    private boolean isNotCommentCardOwner(Long commentCardPk, Long writerPk) {
        return !commentCardService.findCommentCard(commentCardPk).getWriter().getPk().equals(writerPk);
    }


    void deleteFeedCard(Long feedCardPk, Long writerPk) {
        if (isNotFeedCardOwner(feedCardPk, writerPk)) return;
        FeedCard feedCard = feedCardService.findFeedCard(feedCardPk);
        feedCard.setDeleted(true);
        feedTagService.deleteByFeedCardPk(feedCardPk);
        userImgService.deleteUserUploadPic(feedCard.getImgName());
        feedLikeService.deleteAllFeedLikes(feedCardPk);
        feedReportService.deleteReport(feedCardPk);
        //todo notification delete
    }

    private boolean isNotFeedCardOwner(Long feedCardPk, Long writerPk) {
        return !feedCardService.findFeedCard(feedCardPk).getWriter().getPk().equals(writerPk);
    }

    public static boolean isWrittenCommentCard(List<CommentCard> commentCardList, Long memberPk) {
        return commentCardList.stream().anyMatch(commentCard -> commentCard.getWriter().getPk().equals(memberPk));
    }

    public static boolean isLiked(FeedCard feed, List<FeedLike> feedLikes, Long memberPk) {
        return feedLikes.stream().anyMatch(feedLike ->
                feedLike.getTargetCard().getPk().equals(feed.getPk()) && feedLike.getLikedMember().getPk().equals(memberPk)
        );
    }

    public static boolean isLiked(CommentCard comment, List<CommentLike> commentLikes, Long memberPk) {
        return commentLikes.stream().anyMatch(commentLike ->
                commentLike.getTargetCard().getPk().equals(comment.getPk())&& commentLike.getLikedMember().getPk().equals(memberPk)
        );
    }

    public static int countLikes(FeedCard feed, List<FeedLike> feedLikes) {
        return (int) feedLikes.stream().filter(feedLike -> feedLike.getTargetCard().getPk().equals(feed.getPk())).count();
    }

    public static int countLikes(CommentCard comment, List<CommentLike> commentLikes) {
        return (int) commentLikes.stream().filter(CommentLike -> CommentLike.getTargetCard().getPk().equals(comment.getPk())).count();
    }

    public static int countComments(FeedCard feed, List<CommentCard> comments) {
        return (int) comments.stream().filter(comment -> comment.getParentCardPk().equals(feed.getPk())).count();
    }

    public static int countComments(CommentCard parentComment, List<CommentCard> comments) {
        return (int) comments.stream().filter(comment -> comment.getParentCardPk().equals(parentComment.getPk())).count();
    }
}
