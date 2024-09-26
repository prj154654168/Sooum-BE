package com.sooum.core.domain.card.controller;

import com.sooum.core.domain.card.dto.CommentDto;
import com.sooum.core.domain.card.service.CommentCardService;
import com.sooum.core.domain.card.service.CommentInfoService;
import com.sooum.core.domain.card.service.CommentLikeService;
import com.sooum.core.domain.card.service.FeedService;
import com.sooum.core.global.auth.annotation.CurrentUser;
import com.sooum.core.global.responseform.ResponseCollectionModel;
import com.sooum.core.global.responseform.ResponseEntityModel;
import com.sooum.core.global.responseform.ResponseStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;
import java.util.Optional;

@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/comments")
public class CommentCardController {
    private final CommentLikeService commentLikeService;
    private final FeedService feedService;
    private final CommentCardService commentCardService;
    private final CommentInfoService commentInfoService;

    @PostMapping("/{cardPk}/like")
    public ResponseEntity<ResponseStatus> createCommentLike(@PathVariable(value = "cardPk") Long cardPk,
                                                            @CurrentUser Long memberPk) {
        commentLikeService.createCommentLike(cardPk, memberPk);

        return ResponseEntity.created(URI.create(""))
                .body(
                        ResponseStatus.builder()
                                .httpCode(HttpStatus.CREATED.value())
                                .httpStatus(HttpStatus.CREATED)
                                .responseMessage("Feed card like successfully")
                                .build()
                );
    }

    @DeleteMapping("/{cardPk}/like")
    public ResponseEntity<Void> deleteCommentLike(@PathVariable(value = "cardPk") Long cardPk,
                                                  @CurrentUser Long memberPk) {
        commentLikeService.deleteCommentLike(cardPk, memberPk);

        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{cardPk}")
    public ResponseEntity<Void> deleteCommentCardInfo(@PathVariable("cardPk") Long cardPk) {
        feedService.deleteCommentCard(cardPk);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/current/{parentCardPk}/count")
    public ResponseEntity<ResponseEntityModel<CommentDto.CommentCntRetrieve>> countCommentsByParentCard(@PathVariable Long parentCardPk) {
        return ResponseEntity.ok(
                        ResponseEntityModel.<CommentDto.CommentCntRetrieve>builder()
                                .status(ResponseStatus.builder()
                                        .httpCode(HttpStatus.OK.value())
                                        .httpStatus(HttpStatus.OK)
                                        .responseMessage("comment cnt retrieve successfully")
                                        .build())
                                .content(commentCardService.countCommentsByParentCard(parentCardPk))
                                .build()
        );
    }

    @GetMapping("/current/{currentCardPk}")
    public ResponseEntity<?> createCommentCardsInfo(@RequestParam(required = false) Optional<Long> lastId,
                                                    @RequestParam(required = false) Optional<Double> latitude,
                                                    @RequestParam(required = false) Optional<Double> longitude,
                                                    @PathVariable Long currentCardPk,
                                                    @CurrentUser Long memberPk) {
        List<CommentDto.CommentCardsInfo> commentsInfo = commentInfoService.createCommentsInfo(lastId, latitude, longitude, currentCardPk, memberPk);

        if (commentsInfo.isEmpty()) {
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.ok(
                ResponseCollectionModel.<CommentDto.CommentCardsInfo>builder()
                        .status(ResponseStatus.builder()
                                .httpCode(HttpStatus.OK.value())
                                .httpStatus(HttpStatus.OK)
                                .responseMessage("comments info retrieve successfully")
                                .build())
                        .content(commentsInfo)
                        .build()
                        .add(commentInfoService.createNextCommentsInfoUrl(commentsInfo, currentCardPk))
        );
    }
}
