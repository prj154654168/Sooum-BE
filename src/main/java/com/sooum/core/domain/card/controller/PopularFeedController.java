package com.sooum.core.domain.card.controller;

import com.sooum.core.domain.card.dto.PopularCardDto;
import com.sooum.core.domain.card.service.PopularFeedService;
import com.sooum.core.global.auth.annotation.CurrentUser;
import com.sooum.core.global.responseform.ResponseEntityModel;
import com.sooum.core.global.responseform.ResponseStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Optional;

@RestController
@RequiredArgsConstructor
@RequestMapping("/cards")
public class PopularFeedController {
    private final PopularFeedService popularFeedService;

    @GetMapping("/home/popular")
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