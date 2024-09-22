package com.sooum.core.domain.card.controller;

import com.sooum.core.domain.card.service.FeedService;
import lombok.RequiredArgsConstructor;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping
public class FeedController {
    private final FeedService feedService;

    @GetMapping("/{cardPk}")
    public EntityModel<?> findFeedCardInfo(@PathVariable("cardPk") Long cardPk) {
        //todo 추후에 글 상세보기 api 구현
        return null;
    }

    @DeleteMapping("/cards/{cardPk}")
    public ResponseEntity<Void> deleteFeedCardInfo(@PathVariable("cardPk") Long cardPk) {
        feedService.deleteFeedCard(cardPk);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/comments/{cardPk}")
    public ResponseEntity<Void> deleteCommentCardInfo(@PathVariable("cardPk") Long cardPk) {
        feedService.deleteCommentCard(cardPk);
        return ResponseEntity.noContent().build();
    }

}
