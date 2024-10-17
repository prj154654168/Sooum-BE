package com.sooum.core.domain.card.controller;

import com.sooum.core.domain.card.dto.CommentDto;
import com.sooum.core.domain.card.dto.MyCommentCardDto;
import com.sooum.core.domain.card.service.CommentInfoService;
import com.sooum.core.global.auth.annotation.CurrentUser;
import com.sooum.core.global.responseform.ResponseCollectionModel;
import com.sooum.core.global.responseform.ResponseStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/comments")
public class CommentCardController {
    private final CommentInfoService commentInfoService;

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

    @GetMapping("/mine")
    public ResponseEntity<?> getMyCommentsCards(@CurrentUser Long memberPk,
                                                @RequestParam(required = false) Optional<Long> lastId) {
        List<MyCommentCardDto> myCommentCards = commentInfoService.getMyCommentCards(memberPk, lastId);

        if (myCommentCards.isEmpty()) {
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.ok(ResponseCollectionModel.<MyCommentCardDto>builder()
                .status(ResponseStatus.builder()
                        .httpCode(HttpStatus.OK.value())
                        .httpStatus(HttpStatus.OK)
                        .responseMessage("Successfully retrieved all your comment cards.")
                        .build()
                )
                .content(myCommentCards)
                .build()
        );
    }
}
