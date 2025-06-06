package com.sooum.api.card.service;

import com.sooum.api.card.dto.CreateCardDto;
import com.sooum.api.card.dto.CreateCommentDto;
import com.sooum.api.card.dto.CreateFeedCardDto;
import com.sooum.api.card.exception.ParentCardDeletedException;
import com.sooum.api.img.service.ImgService;
import com.sooum.api.notification.dto.FCMDto;
import com.sooum.api.notification.service.NotificationUseCase;
import com.sooum.data.card.entity.Card;
import com.sooum.data.card.entity.CommentCard;
import com.sooum.data.card.entity.FeedCard;
import com.sooum.data.card.entity.imgtype.CardImgType;
import com.sooum.data.card.entity.parenttype.CardType;
import com.sooum.data.card.service.CommentCardService;
import com.sooum.data.card.service.FeedCardService;
import com.sooum.data.common.deactivatewords.DeactivateWords;
import com.sooum.data.img.service.CardImgService;
import com.sooum.data.member.entity.Member;
import com.sooum.data.member.entity.Role;
import com.sooum.data.member.service.MemberService;
import com.sooum.data.notification.entity.notificationtype.NotificationType;
import com.sooum.data.tag.entity.CommentTag;
import com.sooum.data.tag.entity.FeedTag;
import com.sooum.data.tag.entity.Tag;
import com.sooum.data.tag.service.CommentTagService;
import com.sooum.data.tag.service.FeedTagService;
import com.sooum.data.tag.service.TagService;
import com.sooum.global.exceptionmessage.ExceptionMessage;
import com.sooum.global.regex.BadWordFiltering;
import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class FeedService {
    private final MemberService memberService;
    private final CommentCardService commentCardService;
    private final FeedCardService feedCardService;
    private final TagService tagService;
    private final FeedTagService feedTagService;
    private final ImgService imgService;
    private final CommentTagService commentTagService;
    private final CardImgService cardImgService;
    private final BadWordFiltering badWordFiltering;
    private final NotificationUseCase notificationUseCase;
    private final ApplicationEventPublisher sendFCMEventPublisher;

    @Transactional
    public void createFeedCard(Long memberPk, CreateFeedCardDto cardDto, HttpServletRequest request) {
        if (checkForFeedTagsInStory(cardDto)) {
            throw new EntityNotFoundException(ExceptionMessage.TAGS_NOT_ALLOWED_FOR_STORY.getMessage());
        }

        Member member = memberService.findMember(memberPk);

        if(isBanPeriodExpired(member)){
            member.unban();
        }

        if (isUserImage(cardDto)) {
            validateUserImage(cardDto);
        }

        FeedCard feedCard = cardDto.of(member, getWriterIp(request));
        feedCardService.saveFeedCard(feedCard);

        if (isUserImage(cardDto)){
            cardImgService.updateCardImg(feedCard, cardDto.getImgName());
        }

        List<Tag> tagContents = saveFeedTags(cardDto);
        List<FeedTag> feedTagList = tagContents.stream()
                .map(tag -> FeedTag.builder().feedCard(feedCard).tag(tag).build())
                .toList();
        feedTagService.saveAll(feedTagList);
    }

    private static boolean isBanPeriodExpired(Member member) {
        return member.getRole() == Role.BANNED && member.getUntilBan().isBefore(LocalDateTime.now());
    }

    @Transactional
    public void createCommentCard(Long memberPk, Long cardPk, CreateCommentDto cardDto, HttpServletRequest request) {
        if (isUserImage(cardDto)) {
            validateUserImage(cardDto);
        }

        Member member = memberService.findMember(memberPk);

        if(isBanPeriodExpired(member)){
            member.unban();
        }

        Card parentCard = getParentCard(cardPk);
        CardType parentCardType = getCardType(parentCard);

        Long masterCardPk = getMasterCardPk(parentCard);

        validateTagsNotAllowedForMasterCardStory(cardDto, masterCardPk);

        CommentCard commentCard = cardDto.of(parentCardType, parentCard.getPk(), masterCardPk, member, getWriterIp(request));
        commentCardService.saveComment(commentCard);

        if (isUserImage(cardDto)){
            cardImgService.updateCardImg(commentCard, cardDto.getImgName());
        }

        List<Tag> tagContents = saveCommentTags(cardDto);
        List<CommentTag> commentTagList = tagContents.stream()
                .map(tag -> CommentTag.builder().commentCard(commentCard).tag(tag).build())
                .toList();
        commentTagService.saveAll(commentTagList);

        if (!parentCard.isWriter(memberPk)) {
            Long notificationId = notificationUseCase.saveCommentWriteHistory(memberPk, commentCard.getPk(), parentCard);

            if (parentCard.getWriter().isAllowNotify()) {
                sendFCMEventPublisher.publishEvent(
                        FCMDto.GeneralFcmSendEvent.builder()
                                .notificationId(notificationId)
                                .notificationType(NotificationType.COMMENT_WRITE)
                                .targetDeviceType(parentCard.getWriter().getDeviceType())
                                .targetFcmToken(parentCard.getWriter().getFirebaseToken())
                                .targetCardPk(commentCard.getPk())
                                .requesterNickname(member.getNickname())
                                .source(this)
                                .build());
            }
        }
    }

    private static String getWriterIp(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            return ip = request.getRemoteAddr();
        }
        return ip;
    }

    private Card getParentCard(Long cardPk) {
        if (feedCardService.isExistFeedCard(cardPk)) {
            return feedCardService.findFeedCard(cardPk);
        } else if (commentCardService.isExistCommentCard(cardPk)) {
            return commentCardService.findCommentCard(cardPk);
        } else {
            throw new ParentCardDeletedException(ExceptionMessage.PARENT_CARD_DELETED_EXCEPTION.getMessage());
        }
    }

    private CardType getCardType(Card card) {
        if(card instanceof FeedCard) {
            return CardType.FEED_CARD;
        }else {
            return CardType.COMMENT_CARD;
        }
    }

    private void validateTagsNotAllowedForMasterCardStory(CreateCommentDto cardDto, Long masterCardPk) {
        feedCardService.findOptFeedCard(masterCardPk)
                .filter(FeedCard::isStory)
                .filter(masterCard -> hasCommentTags(cardDto))
                .ifPresent(masterCard -> {
                    throw new EntityNotFoundException(ExceptionMessage.TAGS_NOT_ALLOWED_FOR_MASTER_CARD_STORY.getMessage());
                });
    }

    private static Long getMasterCardPk(Card card) {
        if (card instanceof FeedCard) {
            return card.getPk();
        } else if (card instanceof CommentCard commentCard) {
            return commentCard.getMasterCard();
        } else {
            throw new IllegalArgumentException(ExceptionMessage.UNHANDLED_OBJECT.getMessage());
        }
    }

    private List<Tag> saveFeedTags(CreateCardDto cardDto){
        if (!hasTags(cardDto)) {
            return List.of();
        }

        List<Tag> alreadyExistsTag = tagService.findTagList(cardDto.getTags());
        tagService.incrementTagCount(alreadyExistsTag);

        List<String> alreadyExistTagContents = alreadyExistsTag.stream().map(Tag::getContent).toList();
        cardDto.getTags().removeAll(alreadyExistTagContents);

        List<Tag> newTagsToSave = cardDto.getTags().stream()
                .map(tagContent -> Tag.ofFeed(tagContent, isActiveWords(tagContent)))
                .toList();

        tagService.saveAll(newTagsToSave);
        alreadyExistsTag.addAll(newTagsToSave);

        return alreadyExistsTag;
   }

    private boolean isActiveWords(String tagContent) {
        return !(badWordFiltering.isBadWord(tagContent) || DeactivateWords.deactivateWordsList.contains(tagContent));
    }

    private List<Tag> saveCommentTags(CreateCardDto cardDto){
        if (!hasTags(cardDto)) {
            return List.of();
        }

        List<Tag> alreadyExistsTag = tagService.findTagList(cardDto.getTags());

        List<String> alreadyExistTagContents = alreadyExistsTag.stream().map(Tag::getContent).toList();
        cardDto.getTags().removeAll(alreadyExistTagContents);

        List<Tag> newTagsToSave = cardDto.getTags().stream()
                .map(tagContent -> Tag.ofComment(tagContent,isActiveWords(tagContent)))
                .toList();

        tagService.saveAll(newTagsToSave);
        alreadyExistsTag.addAll(newTagsToSave);

        return alreadyExistsTag;
    }

    private void validateUserImage(CreateCardDto cardDto) {
        if(!imgService.isCardImgSaved(cardDto.getImgName())) {
            throw new EntityNotFoundException(ExceptionMessage.IMAGE_NOT_FOUND.getMessage());
        }
        if (imgService.isModeratingCardImg(cardDto.getImgName())) {
            throw new EntityNotFoundException(ExceptionMessage.IMAGE_REJECTED_BY_MODERATION.getMessage());
        }
    }

    private static boolean checkForFeedTagsInStory(CreateFeedCardDto cardDto) {
        return cardDto.isStory() && hasTags(cardDto);
    }
    private static boolean hasCommentTags(CreateCommentDto cardDto) {
        return cardDto.getCommentTags() != null && !cardDto.getCommentTags().isEmpty();
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
}
