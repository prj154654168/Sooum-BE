package com.sooum.api.card.service;

import com.sooum.api.card.dto.CreateCardDto;
import com.sooum.api.card.dto.CreateCommentDto;
import com.sooum.api.card.dto.CreateFeedCardDto;
import com.sooum.api.img.service.ImgService;
import com.sooum.api.member.exception.BannedUserException;
import com.sooum.api.member.service.BlackListUseCase;
import com.sooum.api.notification.dto.FCMDto;
import com.sooum.api.notification.service.NotificationUseCase;
import com.sooum.data.card.entity.*;
import com.sooum.data.card.entity.imgtype.CardImgType;
import com.sooum.data.card.entity.parenttype.CardType;
import com.sooum.data.card.service.*;
import com.sooum.data.img.service.CardImgService;
import com.sooum.data.member.entity.Member;
import com.sooum.data.member.entity.Role;
import com.sooum.data.member.service.MemberService;
import com.sooum.data.notification.entity.notificationtype.NotificationType;
import com.sooum.data.notification.service.NotificationHistoryService;
import com.sooum.data.report.service.CommentReportService;
import com.sooum.data.report.service.FeedReportService;
import com.sooum.data.tag.entity.CommentTag;
import com.sooum.data.tag.entity.FeedTag;
import com.sooum.data.tag.entity.Tag;
import com.sooum.data.tag.service.CommentTagService;
import com.sooum.data.tag.service.FeedTagService;
import com.sooum.data.tag.service.TagService;
import com.sooum.global.config.jwt.InvalidTokenException;
import com.sooum.global.config.jwt.TokenProvider;
import com.sooum.global.exceptionmessage.ExceptionMessage;
import com.sooum.global.regex.BadWordFiltering;
import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class FeedService {
    private final MemberService memberService;
    private final CommentCardService commentCardService;
    private final FeedCardService feedCardService;
    private final FeedLikeService feedLikeService;
    private final TagService tagService;
    private final FeedTagService feedTagService;
    private final ImgService imgService;
    private final CommentTagService commentTagService;
    private final CardImgService cardImgService;
    private final BadWordFiltering badWordFiltering;
    private final CommentLikeService commentLikeService;
    private final CommentReportService commentReportService;
    private final FeedReportService feedReportService;
    private final TokenProvider tokenProvider;
    private final BlackListUseCase blackListUseCase;
    private final PopularFeedService popularFeedService;
    private final NotificationHistoryService notificationHistoryService;
    private final NotificationUseCase notificationUseCase;
    private final ApplicationEventPublisher sendFCMEventPublisher;

    @Transactional
    public void createFeedCard(Long memberPk, CreateFeedCardDto cardDto, HttpServletRequest request) {
        if (checkForTagsInStory(cardDto)) {
            throw new RuntimeException(ExceptionMessage.TAGS_NOT_ALLOWED_FOR_STORY.getMessage());
        }

        Member member = memberService.findMember(memberPk);

        if(member.getRole() == Role.BANNED) {
            String token = tokenProvider.getToken(request)
                    .filter(tokenProvider::isAccessToken)
                    .orElseThrow(InvalidTokenException::new);
            blackListUseCase.save(token, member.getUntilBan());

            throw new BannedUserException(ExceptionMessage.BANNED_USER.getMessage());
        }

        if (isUserImage(cardDto)) {
            validateUserImage(cardDto);
        }

        FeedCard feedCard = cardDto.of(member);
        feedCardService.saveFeedCard(feedCard);

        if (isUserImage(cardDto)){
            cardImgService.updateCardImg(feedCard, cardDto.getImgName());
        }

        List<Tag> tagContents = processTags(cardDto);
        List<FeedTag> feedTagList = tagContents.stream()
                .map(tag -> FeedTag.builder().feedCard(feedCard).tag(tag).build())
                .toList();
        feedTagService.saveAll(feedTagList);
    }

    @Transactional
    public void createCommentCard(Long memberPk, Long cardPk, CreateCommentDto cardDto, HttpServletRequest request) {
        if (isUserImage(cardDto)) {
            validateUserImage(cardDto);
        }

        Member member = memberService.findMember(memberPk);

        if(member.getRole() == Role.BANNED) {
            String token = tokenProvider.getToken(request)
                    .filter(tokenProvider::isAccessToken)
                    .orElseThrow(InvalidTokenException::new);
            blackListUseCase.save(token, member.getUntilBan());

            throw new BannedUserException(ExceptionMessage.BANNED_USER.getMessage());
        }

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
            cardImgService.updateCardImg(commentCard, cardDto.getImgName());
        }

        List<Tag> tagContents = processTags(cardDto);
        List<CommentTag> commentTagList = tagContents.stream()
                .map(tag -> CommentTag.builder().commentCard(commentCard).tag(tag).build())
                .toList();
        commentTagService.saveAll(commentTagList);

        if (!card.isWriter(memberPk)) {
            Long notificationId = notificationUseCase.saveCommentWriteHistory(memberPk, commentCard.getPk(), card);

            if (card.getWriter().isAllowNotify()) {
                sendFCMEventPublisher.publishEvent(
                        FCMDto.GeneralFcmSendEvent.builder()
                                .notificationId(notificationId)
                                .notificationType(NotificationType.COMMENT_WRITE)
                                .targetDeviceType(card.getWriter().getDeviceType())
                                .targetFcmToken(card.getWriter().getFirebaseToken())
                                .targetCardPk(commentCard.getPk())
                                .requesterNickname(member.getNickname())
                                .source(this)
                                .build());
            }
        }
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
        if(!imgService.isCardImgSaved(cardDto.getImgName())) {
            throw new EntityNotFoundException(ExceptionMessage.IMAGE_NOT_FOUND.getMessage());
        }
        if (imgService.isModeratingCardImg(cardDto.getImgName())) {
            throw new EntityNotFoundException(ExceptionMessage.IMAGE_REJECTED_BY_MODERATION.getMessage());
        }
    }

    private static boolean checkForTagsInStory(CreateFeedCardDto cardDto) {
        return cardDto.isStory() && hasTags(cardDto);
    }

    private static boolean hasTags(CreateCardDto cardDto) {
        return cardDto.getTags() != null && !cardDto.getTags().isEmpty();
    }

    private static boolean isUserImage(CreateCardDto cardDto) {
        return cardDto.getImgType().equals(CardImgType.USER);
    }


    public Card findParentCard(CommentCard commentCard) {
        if(commentCard.getParentCardType().equals(CardType.COMMENT_CARD)){
            return commentCardService.findOptCommentCard(commentCard.getParentCardPk())
                    .orElse(null);
        }
        if (commentCard.getParentCardType().equals(CardType.FEED_CARD)){
            return feedCardService.findOptFeedCard(commentCard.getParentCardPk())
                    .orElse(null);
        }
        throw new IllegalArgumentException(ExceptionMessage.UNHANDLED_TYPE.getMessage());
    }

    @Transactional
    public void deleteCard(Long cardPk, Long writerPk) {
        CardType cardType = commentCardService.findCardType(cardPk);
        switch (cardType) {
            case FEED_CARD -> deleteFeedCard(cardPk, writerPk);
            case COMMENT_CARD -> deleteCommentCard(cardPk, writerPk);
        }
    }

    private void deleteCommentCard(Long commentCardPk, Long writerPk) {
        validateCommentCardOwner(commentCardPk, writerPk);
        CommentCard commentCard = commentCardService.findCommentCard(commentCardPk);
        deleteCommentCardDependencies(commentCard);
        commentCardService.deleteCommentCard(commentCardPk);
    }

    private void deleteCommentCardDependencies(CommentCard commentCard) {
        commentTagService.deleteByCommentCardPk(commentCard.getPk());
        cardImgService.deleteUserUploadPic(commentCard.getImgName());
        commentLikeService.deleteAllFeedLikes(commentCard.getPk());
        commentReportService.deleteReport(commentCard);
        notificationHistoryService.deleteNotification(commentCard.getPk());
    }

    private void validateCommentCardOwner(Long commentCardPk, Long writerPk) {
        if (isNotCommentCardOwner(commentCardPk, writerPk)) {
            throw new IllegalArgumentException("카드 작성자만 카드 삭제가 가능합니다.");
        }
    }

    private boolean isNotCommentCardOwner(Long commentCardPk, Long writerPk) {
        return !commentCardService.findCommentCard(commentCardPk).getWriter().getPk().equals(writerPk);
    }

    void deleteFeedCard(Long feedCardPk, Long writerPk) {
        validateFeedCardOwner(feedCardPk, writerPk);
        FeedCard feedCard = feedCardService.findFeedCard(feedCardPk);
        deleteFeedCardDependencies(feedCard);
        feedCardService.deleteFeedCard(feedCardPk);
    }

    private void deleteFeedCardDependencies(FeedCard feedCard) {
        feedTagService.deleteByFeedCardPk(feedCard.getPk());
        cardImgService.deleteUserUploadPic(feedCard.getImgName());
        feedLikeService.deleteAllFeedLikes(feedCard.getPk());
        feedReportService.deleteReport(feedCard.getPk());
        popularFeedService.deletePopularCard(feedCard.getPk());
        notificationHistoryService.deleteNotification(feedCard.getPk());
    }

    private void validateFeedCardOwner(Long commentCardPk, Long writerPk) {
        if (isNotFeedCardOwner(commentCardPk, writerPk)) {
            throw new IllegalArgumentException("카드 작성자만 카드 삭제가 가능합니다.");
        }
    }

    private boolean isNotFeedCardOwner(Long feedCardPk, Long writerPk) {
        return !feedCardService.findFeedCard(feedCardPk).getWriter().getPk().equals(writerPk);
    }

    public static boolean isWrittenCommentCard(Card card, List<CommentCard> commentCardList, Long memberPk) {
        return commentCardList.stream()
                .filter(commentCard -> commentCard.getParentCardPk().equals(card.getPk()))
                .anyMatch(commentCard -> commentCard.getWriter().getPk().equals(memberPk));
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
