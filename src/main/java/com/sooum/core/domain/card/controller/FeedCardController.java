package com.sooum.core.domain.card.controller;

import com.sooum.core.domain.card.dto.CardDto;
import com.sooum.core.domain.card.dto.CreateCommentDto;
import com.sooum.core.domain.card.dto.CreateFeedCardDto;
import com.sooum.core.domain.card.dto.TagFeedCardDto;
import com.sooum.core.domain.card.service.DetailFeedService;
import com.sooum.core.domain.card.service.FeedService;
import com.sooum.core.domain.card.service.TagFeedService;
import com.sooum.core.global.auth.annotation.CurrentUser;
import com.sooum.core.global.responseform.ResponseCollectionModel;
import com.sooum.core.global.responseform.ResponseEntityModel;
import com.sooum.core.global.responseform.ResponseStatus;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
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
@RequestMapping("/cards")
public class FeedCardController {
    private final FeedService feedService;
    private final DetailFeedService detailFeedService;
    private final TagFeedService tagFeedService;

    @GetMapping("/{cardId}/detail")
    public ResponseEntity<?> findFeedCardInfo(
            @RequestParam(required = false, value = "latitude") Optional<Double> latitude,
            @RequestParam(required = false, value = "longitude") Optional<Double> longitude,
            @PathVariable("cardId") @NotNull Long cardId, @CurrentUser Long memberPk) {
        CardDto detailFeedCard = detailFeedService.findDetailFeedCard(cardId, memberPk, latitude, longitude);
        return ResponseEntity.ok(ResponseEntityModel.<CardDto>builder()
                .status(ResponseStatus.builder()
                        .httpStatus(HttpStatus.OK)
                        .httpCode(HttpStatus.OK.value())
                        .responseMessage("Card details retrieved successfully.")
                        .build()
                ).content(detailFeedCard)
                .build()
        );
    }

    @PostMapping
    public ResponseEntity<ResponseStatus> createFeedCard(@RequestBody @Valid CreateFeedCardDto cardDto,
                                                         @CurrentUser Long memberPk) {
        feedService.createFeedCard(memberPk, cardDto);
        return ResponseEntity.created(URI.create(""))
                .body(ResponseStatus.builder()
                        .httpCode(HttpStatus.CREATED.value())
                        .httpStatus(HttpStatus.CREATED)
                        .responseMessage("Feed Card created successfully.")
                        .build());
    }

    @PostMapping("/{cardPk}")
    public ResponseEntity<ResponseStatus> createCommentCard(@PathVariable(value = "cardPk") @NotNull Long cardPk,
                                                            @CurrentUser Long memberPk,
                                                            @RequestBody @Valid CreateCommentDto cardDto) {
        feedService.createCommentCard(memberPk, cardPk, cardDto);
        return ResponseEntity.created(URI.create(""))
                .body(ResponseStatus.builder()
                        .httpCode(HttpStatus.CREATED.value())
                        .httpStatus(HttpStatus.CREATED)
                        .responseMessage("Comment Card created successfully.")
                        .build());
    }

    @GetMapping("/tags/{tagPk}")
    public ResponseEntity<?> findTagFeeds(@RequestParam(required = false) Optional<Double> latitude,
                                          @RequestParam(required = false) Optional<Double> longitude,
                                          @RequestParam(required = false) Optional<Long> lastPk,
                                          @PathVariable Long tagPk,
                                          @CurrentUser Long memberPk) {
        List<TagFeedCardDto> tagFeedsInfo = tagFeedService.createTagFeedsInfo(tagPk, lastPk, latitude, longitude, memberPk);

        if (tagFeedsInfo.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        }

        return ResponseEntity.ok(ResponseCollectionModel.<TagFeedCardDto>builder()
                .status(ResponseStatus.builder()
                        .httpStatus(HttpStatus.OK)
                        .httpCode(HttpStatus.OK.value())
                        .responseMessage("Tag feeds retrieve successfully")
                        .build()
                ).content(tagFeedsInfo)
                .build()
                .add(tagFeedService.createNextTagFeedsUrl(tagPk, tagFeedsInfo))
        );

    }
}
