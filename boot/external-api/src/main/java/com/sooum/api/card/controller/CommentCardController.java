package com.sooum.api.card.controller;

import com.sooum.api.card.dto.CommentDto;
import com.sooum.api.card.service.CommentInfoService;
import com.sooum.global.auth.annotation.CurrentUser;
import com.sooum.global.responseform.ResponseCollectionModel;
import com.sooum.global.responseform.ResponseStatus;
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
}
