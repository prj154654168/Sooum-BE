package com.sooum.core.domain.card.controller;

import com.sooum.core.domain.card.service.CommentLikeService;
import com.sooum.core.global.auth.annotation.CurrentUser;
import com.sooum.core.global.responseform.ResponseStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/comments")
public class CommentCardController {
    private final CommentLikeService commentLikeService;

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
    public ResponseEntity<?> deleteCommentLike(@PathVariable(value = "cardPk") Long cardPk,
                                               @CurrentUser Long memberPk) {
        commentLikeService.deleteCommentLike(cardPk, memberPk);

        return ResponseEntity.noContent().build();
    }
}
