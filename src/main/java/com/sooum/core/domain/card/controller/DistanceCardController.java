package com.sooum.core.domain.card.controller;

import com.sooum.core.domain.card.dto.DistanceCardDto;
import com.sooum.core.domain.card.dto.distancefilter.DistanceFilter;
import com.sooum.core.domain.card.service.DistanceFeedService;
import com.sooum.core.global.auth.annotation.CurrentUser;
import com.sooum.core.global.responseform.ResponseEntityModel;
import com.sooum.core.global.responseform.ResponseStatus;
import com.sooum.core.global.util.NextPageLinkGenerator;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/cards/home/distance")
@RequiredArgsConstructor
public class DistanceCardController {
    private final DistanceFeedService distanceFeedService;

    @GetMapping(value = { "","/{last}"})
    ResponseEntity<?> getFeedsByDistance(
            @PathVariable(required = false, value = "last") Optional<Long> last,
            @RequestParam(value = "latitude") @NotNull Double latitude,
            @RequestParam(value = "longitude") @NotNull Double longitude,
            @RequestParam(defaultValue = "UNDER_1", value = "distanceFilter") DistanceFilter distanceFilter,
            @CurrentUser Long memberPk) {
        List<DistanceCardDto> distanceFeeds = distanceFeedService.findDistanceFeeds(last.orElse(0L), memberPk, latitude, longitude, distanceFilter);

        if (distanceFeeds.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        }

        return ResponseEntity.ok(ResponseEntityModel.<DistanceCardDto>builder()
                .status(ResponseStatus.builder()
                        .httpStatus(HttpStatus.OK)
                        .httpCode(HttpStatus.OK.value())
                        .responseMessage("Nearby feeds retrieved successfully")
                        .build()
                ).content(distanceFeeds)
                .build()
                .add(NextPageLinkGenerator.generateNextPageLink(distanceFeeds))
        );
    }
}