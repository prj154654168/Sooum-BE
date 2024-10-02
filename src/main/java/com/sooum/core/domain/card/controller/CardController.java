package com.sooum.core.domain.card.controller;

import com.sooum.core.domain.card.dto.CardSummary;
import com.sooum.core.domain.card.service.CardService;
import com.sooum.core.global.auth.annotation.CurrentUser;
import com.sooum.core.global.responseform.ResponseEntityModel;
import com.sooum.core.global.responseform.ResponseStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/cards")
public class CardController {
    private final CardService cardService;

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
}
