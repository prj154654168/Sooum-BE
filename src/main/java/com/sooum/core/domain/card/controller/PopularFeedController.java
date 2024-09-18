package com.sooum.core.domain.card.controller;

import com.sooum.core.domain.card.dto.PopularCardDto;
import com.sooum.core.domain.card.service.PopularFeedService;
import com.sooum.core.domain.member.repository.MemberRepository;
import com.sooum.core.global.auth.annotation.CurrentUser;
import com.sooum.core.global.responseform.ResponseEntityModel;
import com.sooum.core.global.responseform.ResponseStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.Link;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequiredArgsConstructor
@RequestMapping("/cards")
public class PopularFeedController {
    private final PopularFeedService popularFeedService;
    private final MemberRepository memberRepository;

    @GetMapping(value = {"/home/popular", "/home/popular/{latitude}/{longitude}"})
    public ResponseEntity<?> findHomePopularFeeds(@RequestParam(required = false, value = "latitude") Optional<Double> latitude,
                                                  @RequestParam(required = false, value = "longitude") Optional<Double> longitude,
                                                  @CurrentUser Long memberPk) {
        List<PopularCardDto.PopularCardRetrieve> popularFeeds = popularFeedService.findHomePopularFeeds(latitude, longitude, memberPk);

        if (popularFeeds.isEmpty()) {
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.ok().body(
                ResponseEntityModel.<PopularCardDto.PopularCardRetrieve>builder()
                        .status(ResponseStatus.builder()
                                .httpCode(HttpStatus.OK.value())
                                .httpStatus(HttpStatus.OK)
                                .responseMessage("Popular feeds retrieve successfully")
                                .build())
                        .content(popularFeeds)
                        .build()
        );
    }
}