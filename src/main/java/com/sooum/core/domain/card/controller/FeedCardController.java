package com.sooum.core.domain.card.controller;

import com.sooum.core.domain.card.dto.CardDetailDto;
import com.sooum.core.domain.card.service.DetailFeedService;
import com.sooum.core.domain.card.service.FeedLikeService;
import com.sooum.core.domain.card.service.FeedService;
import com.sooum.core.global.auth.annotation.CurrentUser;
import com.sooum.core.global.responseform.ResponseEntityModel;
import com.sooum.core.global.responseform.ResponseStatus;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.Optional;

@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/cards")
public class FeedCardController {
    private final FeedLikeService feedLikeService;
    private final FeedService feedService;
    private final DetailFeedService detailFeedService;

    @GetMapping("/{cardId}/detail")
    public ResponseEntity<?> findFeedCardInfo(
            @RequestParam(required = false, value = "latitude") Optional<Double> latitude,
            @RequestParam(required = false, value = "longitude") Optional<Double> longitude,
            @PathVariable("cardId") @NotNull Long cardId, @CurrentUser Long memberPk) {
        CardDetailDto detailFeedCard = detailFeedService.findDetailFeedCard(cardId, memberPk, latitude, longitude);
        return ResponseEntity.ok(ResponseEntityModel.<CardDetailDto>builder()
                .status(ResponseStatus.builder()
                        .httpStatus(HttpStatus.OK)
                        .httpCode(HttpStatus.OK.value())
                        .responseMessage("Card details retrieved successfully.")
                        .build()
                ).content(detailFeedCard)
                .build()
        );
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

    @DeleteMapping("/{cardPk}")
    public ResponseEntity<Void> deleteFeedCardInfo(@PathVariable("cardPk") Long cardPk, @CurrentUser Long memberPk) {
        feedService.deleteFeedCard(cardPk, memberPk);
        return ResponseEntity.noContent().build();
    }
}
