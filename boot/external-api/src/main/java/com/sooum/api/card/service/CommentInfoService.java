package com.sooum.api.card.service;

import com.sooum.api.card.controller.CommentCardController;
import com.sooum.api.card.dto.CommentDto;
import com.sooum.api.card.dto.MyCommentCardDto;
import com.sooum.api.img.service.ImgService;
import com.sooum.data.block.service.BlockMemberService;
import com.sooum.data.card.entity.CommentCard;
import com.sooum.data.card.entity.CommentLike;
import com.sooum.data.card.service.CommentCardService;
import com.sooum.data.card.service.CommentLikeService;
import com.sooum.global.util.CardUtils;
import com.sooum.global.util.DistanceUtils;
import com.sooum.global.util.NextPageLinkGenerator;
import lombok.RequiredArgsConstructor;
import org.springframework.hateoas.Link;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CommentInfoService {
    private final BlockMemberService blockMemberService;
    private final ImgService imgService;
    private final CommentLikeService commentLikeService;
    private final CommentCardService commentCardService;

    public List<CommentDto.CommentCardsInfo> createCommentsInfo(Optional<Long> lastPk,
                                                                Optional<Double> latitude,
                                                                Optional<Double> longitude,
                                                                Long currentCardPk,
                                                                Long memberPk) {
        List<Long> blockMemberPks = blockMemberService.findAllBlockMemberPks(memberPk);
        List<CommentCard> comments = commentCardService.findCommentsByLastPk(currentCardPk, lastPk, blockMemberPks);
        List<CommentLike> commentLikes = commentLikeService.findByTargetCards(comments);
        List<CommentCard> childComments = commentCardService.findChildCommentsByParents(comments);

        return NextPageLinkGenerator.appendEachCardDetailLink(comments.stream()
                .map(comment -> CommentDto.CommentCardsInfo.builder()
                        .id(comment.getPk().toString())
                        .content(comment.getContent())
                        .backgroundImgUrl(imgService.findCardImgUrl(comment.getImgType(), comment.getImgName()))
                        .font(comment.getFont())
                        .fontSize(comment.getFontSize())
                        .distance(DistanceUtils.calculate(comment.getLocation(), latitude, longitude))
                        .createdAt(comment.getCreatedAt())
                        .isLiked(CardUtils.isLiked(comment, commentLikes, memberPk))
                        .likeCnt(CardUtils.countLikes(comment, commentLikes))
                        .isCommentWritten(CardUtils.isWrittenCommentCard(comment, childComments, memberPk))
                        .commentCnt(CardUtils.countComments(comment, childComments))
                        .build())
                .toList());
    }

    public Link createNextCommentsInfoUrl(List<CommentDto.CommentCardsInfo> commentsInfoDto, Long currentCardPk) {
        String lastPk = commentsInfoDto.get(commentsInfoDto.size() - 1).getId();
        return linkTo(methodOn(CommentCardController.class).getClass())
                .slash("/current/" + currentCardPk + "?lastId=" + lastPk).withRel("next");
    }

    public List<MyCommentCardDto> getMyCommentCards(Long memberPk, Optional<Long> lastPk) {
        List<CommentCard> commentList = commentCardService.findCommentList(memberPk, lastPk);

        return NextPageLinkGenerator.appendEachMyCardDetailLink(commentList.stream()
                .map(comment -> MyCommentCardDto.builder()
                        .id(comment.getPk().toString())
                        .content(comment.getContent())
                        .backgroundImgUrl(imgService.findCardImgUrl(comment.getImgType(), comment.getImgName()))
                        .font(comment.getFont())
                        .fontSize(comment.getFontSize()).build())
                .toList());
    }
}
