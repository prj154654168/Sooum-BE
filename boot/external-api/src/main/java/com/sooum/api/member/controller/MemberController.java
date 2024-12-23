package com.sooum.api.member.controller;

import com.sooum.api.card.dto.MyCommentCardDto;
import com.sooum.api.card.dto.MyFeedCardDto;
import com.sooum.api.card.service.CommentInfoService;
import com.sooum.api.card.service.FeedCardUseCase;
import com.sooum.api.member.dto.AuthDTO;
import com.sooum.api.member.dto.MemberDto;
import com.sooum.api.member.service.MemberUseCase;
import com.sooum.api.member.service.MemberWithdrawalService;
import com.sooum.global.auth.annotation.CurrentUser;
import com.sooum.global.responseform.ResponseCollectionModel;
import com.sooum.global.responseform.ResponseStatus;
import com.sooum.global.util.NextPageLinkGenerator;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RequestMapping("/members")
@RestController
@RequiredArgsConstructor
public class MemberController {
    private final CommentInfoService commentInfoService;
    private final FeedCardUseCase feedCardUseCase;
    private final MemberWithdrawalService memberWithdrawalService;
    private final MemberUseCase memberUseCase;

    @GetMapping(value = {"/feed-cards", "/{targetMemberId}/feed-cards"})
    public ResponseEntity<?> findMyFeedCards(@RequestParam(required = false) Optional<Long> lastId,
                                             @PathVariable(required = false) Optional<Long> targetMemberId,
                                             @CurrentUser Long memberPk) {
        List<MyFeedCardDto> cards = targetMemberId.isEmpty()
                ? feedCardUseCase.findMyFeedCards(memberPk, lastId)
                : feedCardUseCase.findMemberFeedCards(targetMemberId.get(), lastId);

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

    @DeleteMapping
    public ResponseEntity<?> withdrawMember(@CurrentUser Long memberPk, @RequestBody AuthDTO.Token token) {
        memberWithdrawalService.withdrawMember(memberPk, token);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/fcm")
    public ResponseEntity<Void> updateFCMToken(@RequestBody MemberDto.FCMTokenUpdateRequest requestDto,
                                               @CurrentUser Long memberPk) {
        memberUseCase.updateFCMToken(requestDto, memberPk);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/notify")
    public ResponseEntity<Void> updateNotifyAllow(@RequestBody MemberDto.NotifyAllowUpdateRequest requestDto,
                                                  @CurrentUser Long memberPk) {
        memberUseCase.updateNotifyAllow(requestDto, memberPk);
        return ResponseEntity.noContent().build();
    }
}
