package com.sooum.core.domain.card.controller;

import com.sooum.core.domain.card.dto.LatestFeedCardDto;
import com.sooum.core.domain.card.service.LatestFeedService;
import com.sooum.core.global.auth.annotation.CurrentUser;
import com.sooum.core.global.responseform.ResponseCollectionModel;
import com.sooum.core.global.responseform.ResponseStatus;
import com.sooum.core.global.util.NextPageLinkGenerator;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;


@RequiredArgsConstructor
@RestController
@RequestMapping("/cards/home")
public class LatestFeedController {

    private final LatestFeedService latestFeedService;

    @GetMapping(value = {"/latest","/latest/{last}"})
    public ResponseEntity<?> getLatestFeed(@PathVariable(required = false, value = "last") Optional<Long> last,
                                           @RequestParam(required = false, value = "latitude") Optional<Double> latitude,
                                           @RequestParam(required = false, value = "longitude") Optional<Double> longitude,
                                           @CurrentUser Long memberPk) {

        List<LatestFeedCardDto> latestFeedInfo = latestFeedService.createLatestFeedInfo(last, memberPk, latitude, longitude);

        if (latestFeedInfo.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        }

        return ResponseEntity.ok(ResponseCollectionModel.<LatestFeedCardDto>builder()
                .status(ResponseStatus.builder()
                        .httpStatus(HttpStatus.OK)
                        .httpCode(HttpStatus.OK.value())
                        .responseMessage("Retrieve latest feed data")
                        .build()
                ).content(latestFeedInfo)
                .build()
                .add(NextPageLinkGenerator.generateNextPageLink(latestFeedInfo))
        );
    }
}
