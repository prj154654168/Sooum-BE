package com.sooum.core.domain.card.controller;

import com.sooum.core.domain.card.service.FeedLikeService;
import com.sooum.core.domain.card.service.FeedService;
import com.sooum.core.global.auth.annotation.CurrentUser;
import com.sooum.core.global.responseform.ResponseStatus;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/cards")
public class FeedCardController {
    private final FeedLikeService feedLikeService;
    private final FeedService feedService;

    @GetMapping("/{cardPk}")
    public EntityModel<?> findFeedCardInfo(@PathVariable("cardPk") Long cardPk) {
        //todo 추후에 글 상세보기 api 구현
        return null;
    }

    @PostMapping("/{cardPk}/like")
    public ResponseEntity<ResponseStatus> createFeedLike(@PathVariable(value = "cardPk") @NotNull Long cardPk,
                                                         @CurrentUser Long memberPk) {
        feedLikeService.createFeedLike(cardPk, memberPk);

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
    public ResponseEntity<?> deleteFeedLike(@PathVariable(value = "cardPk") @NotNull Long cardPk,
                                            @CurrentUser Long memberPk) {
        feedLikeService.deleteFeedLike(cardPk, memberPk);

        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/cards/{cardPk}")
    public ResponseEntity<Void> deleteFeedCardInfo(@PathVariable("cardPk") Long cardPk) {
        feedService.deleteFeedCard(cardPk);
        return ResponseEntity.noContent().build();
    }
}
