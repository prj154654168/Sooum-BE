package com.sooum.core.domain.card.service;

import com.sooum.core.domain.block.service.BlockMemberService;
import com.sooum.core.domain.card.dto.CommentDto;
import com.sooum.core.domain.card.entity.CommentCard;
import com.sooum.core.domain.card.entity.CommentLike;
import com.sooum.core.domain.card.entity.parenttype.CardType;
import com.sooum.core.domain.img.service.ImgService;
import com.sooum.core.global.util.DistanceUtils;
import com.sooum.core.global.util.NextPageLinkGenerator;
import lombok.RequiredArgsConstructor;
import org.springframework.hateoas.Link;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CommentInfoService {
    private final BlockMemberService blockMemberService;
    private final ImgService imgService;
    private final CommentLikeService commentLikeService;
    private final CommentCardService commentCardService;
    private static final int MAX_PAGE_SIZE = 100;
    private final static int DEFAULT_PAGE_SIZE = 50;

    public List<CommentDto.CommentCardsInfo> createCommentsInfo(Optional<Long> lastPk,
                                                                Optional<Double> latitude,
                                                                Optional<Double> longitude,
                                                                CardType parentCardType,
                                                                Long currentCardPk,
                                                                Long memberPk) {
        List<CommentCard> comments = findDefaultPageSizeComments(lastPk, parentCardType, currentCardPk, memberPk);
        List<CommentLike> commentLikes = commentLikeService.findByTargetCards(comments);
        List<CommentCard> childComments = commentCardService.findChildCommentsByParents(comments);

        return NextPageLinkGenerator.appendEachCardDetailLink(comments.stream()
                .map(comment -> CommentDto.CommentCardsInfo.builder()
                        .id(comment.getPk())
                        .content(comment.getContent())
                        .backgroundImgUrl(Link.of(imgService.findImgUrl(comment.getImgType(), comment.getImgName())))
                        .font(comment.getFont())
                        .fontSize(comment.getFontSize())
                        .distance(DistanceUtils.calculate(comment.getLocation(), latitude, longitude))
                        .createdAt(comment.getCreatedAt())
                        .isLiked(FeedService.isLiked(comment, commentLikes))
                        .likeCnt(FeedService.countLikes(comment, commentLikes))
                        .isCommentWritten(FeedService.isWrittenCommentCard(childComments, memberPk))
                        .commentCnt(FeedService.countComments(comment, childComments))
                        .cardType(CardType.COMMENT_CARD)
                        .build())
                .toList());
    }

    protected List<CommentCard> findDefaultPageSizeComments(Optional<Long> lastPk, CardType parentCardType, Long currentCardPk, Long memberPk) {
        ArrayList<CommentCard> resultCards = new ArrayList<>();
        while (resultCards.size() < DEFAULT_PAGE_SIZE) {
            List<CommentCard> findComments = commentCardService.findCommentsByLastPk(currentCardPk, lastPk, parentCardType);
            List<CommentCard> filteredComments = blockMemberService.filterBlockedMembers(findComments, memberPk);
            resultCards.addAll(filteredComments);

            if (!findComments.isEmpty()) {
                lastPk = Optional.of(findComments.get(findComments.size() - 1).getPk());
            }

            if (isEndOfPage(findComments)) {
                break;
            }
        }
        return resultCards.size() > DEFAULT_PAGE_SIZE ? resultCards.subList(0, DEFAULT_PAGE_SIZE) : resultCards;
    }

    private static boolean isEndOfPage(List<CommentCard> byLastId) {
        return byLastId.size() < MAX_PAGE_SIZE;
    }
}