package com.sooum.core.domain.member.controller;

import com.sooum.core.domain.card.dto.MyCommentCardDto;
import com.sooum.core.domain.card.service.CommentInfoService;
import com.sooum.core.global.auth.annotation.CurrentUser;
import com.sooum.core.global.responseform.ResponseCollectionModel;
import com.sooum.core.global.responseform.ResponseStatus;
import com.sooum.core.global.util.NextPageLinkGenerator;
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
