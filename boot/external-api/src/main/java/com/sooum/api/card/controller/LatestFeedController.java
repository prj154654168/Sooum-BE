package com.sooum.api.card.controller;

import com.sooum.api.card.dto.LatestFeedCardDto;
import com.sooum.api.card.service.LatestFeedUseCase;
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


@RequiredArgsConstructor
@RestController
@RequestMapping("/cards/home")
public class LatestFeedController {

    private final LatestFeedUseCase latestFeedUseCase;

    @GetMapping(value = {"/latest","/latest/{last}"})
    public ResponseEntity<?> getLatestFeed(@PathVariable(required = false, value = "last") Optional<Long> last,
                                           @RequestParam(required = false, value = "latitude") Optional<Double> latitude,
                                           @RequestParam(required = false, value = "longitude") Optional<Double> longitude,
                                           @CurrentUser Long memberPk) {

        List<LatestFeedCardDto> latestFeedInfo = latestFeedUseCase.createLatestFeedInfo(last, memberPk, latitude, longitude);

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
