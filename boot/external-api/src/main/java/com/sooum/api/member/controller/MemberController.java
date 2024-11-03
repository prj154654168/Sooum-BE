package com.sooum.api.member.controller;

import com.sooum.api.card.dto.MyCommentCardDto;
import com.sooum.api.card.dto.MyFeedCardDto;
import com.sooum.api.card.service.CardService;
import com.sooum.api.card.service.CommentInfoService;
import com.sooum.global.auth.annotation.CurrentUser;
import com.sooum.global.responseform.ResponseCollectionModel;
import com.sooum.global.responseform.ResponseStatus;
import com.sooum.global.util.NextPageLinkGenerator;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Optional;

@RequestMapping("/members")
@RestController
@RequiredArgsConstructor
public class MemberController {
    private final CommentInfoService commentInfoService;
    private final CardService cardService;

    @GetMapping("/feed-cards")
    public ResponseEntity<?> findMyFeedCards(@CurrentUser Long memberPk, @RequestParam(required = false) Optional<Long> lastId) {
        List<MyFeedCardDto> cards = cardService.findMyCards(memberPk, lastId);

        if (cards.isEmpty())
            return ResponseEntity.noContent().build();

        return ResponseEntity.ok(ResponseCollectionModel.<MyFeedCardDto>builder()
                .status(ResponseStatus.builder()
                        .httpStatus(HttpStatus.OK)
                        .httpCode(HttpStatus.OK.value())
                        .responseMessage("Find my feed cards successfully")
                        .build()
                )
                .content(cards)
                .build());
    }

    @GetMapping("/comment-cards")
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
                .add(NextPageLinkGenerator.generateMyCardNextPageLink(myCommentCards))
        );
    }
}
