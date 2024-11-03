package com.sooum.api.card.controller;

import com.sooum.api.card.dto.CardSummary;
import com.sooum.api.card.service.CardService;
import com.sooum.api.card.service.FeedService;
import com.sooum.global.auth.annotation.CurrentUser;
import com.sooum.global.responseform.ResponseEntityModel;
import com.sooum.global.responseform.ResponseStatus;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@RestController
@RequiredArgsConstructor
@RequestMapping("/cards")
public class CardController {
    private final CardService cardService;
    private final FeedService feedService;

    @GetMapping("/current/{parentCardPk}/summary")
    public ResponseEntity<ResponseEntityModel<CardSummary>> countCommentsByParentCard(@PathVariable Long parentCardPk,
                                                                                      @CurrentUser Long memberPk) {
        return ResponseEntity.ok(
                ResponseEntityModel.<CardSummary>builder()
                        .status(ResponseStatus.builder()
                                .httpCode(HttpStatus.OK.value())
                                .httpStatus(HttpStatus.OK)
                                .responseMessage("Card summary retrieve successfully")
                                .build())
                        .content(cardService.findCardSummary(parentCardPk, memberPk))
                        .build()
        );
    }

    @DeleteMapping("/{cardPk}")
    public ResponseEntity<Void> deleteCard(@PathVariable("cardPk") Long cardPk, @CurrentUser Long memberPk) {
        feedService.deleteCard(cardPk, memberPk);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{cardPk}/like")
    public ResponseEntity<ResponseStatus> createFeedLike(@PathVariable(value = "cardPk") @NotNull Long cardPk,
                                                         @CurrentUser Long memberPk) {
        cardService.createCardLike(cardPk, memberPk);

        return ResponseEntity.created(URI.create(""))
                .body(
                        ResponseStatus.builder()
                                .httpCode(HttpStatus.CREATED.value())
                                .httpStatus(HttpStatus.CREATED)
                                .responseMessage("Card like successfully")
                                .build()
                );
    }

    @DeleteMapping("/{cardPk}/like")
    public ResponseEntity<?> deleteFeedLike(@PathVariable(value = "cardPk") @NotNull Long cardPk,
                                            @CurrentUser Long memberPk) {
        cardService.deleteCardLike(cardPk, memberPk);

        return ResponseEntity.noContent().build();
    }
}
