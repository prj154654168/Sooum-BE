package com.sooum.core.domain.card.controller;

import com.sooum.core.domain.card.dto.DistanceCardDto;
import com.sooum.core.domain.card.dto.distancefilter.DistanceFilter;
import com.sooum.core.domain.card.service.DistanceFeedService;
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
    ResponseEntity<ResponseEntityModel<DistanceCardDto>> getFeedsByDistance(
            @PathVariable(required = false) Optional<Long> last,
            @RequestParam @NotNull Double latitude,
            @RequestParam @NotNull Double longitude,
            @RequestParam(defaultValue = "UNDER_1") DistanceFilter distanceFilter) {

        Long mockMemberPk = 1L;

        List<DistanceCardDto> distanceFeeds = distanceFeedService.findDistanceFeeds(last.orElse(0L), mockMemberPk, latitude, longitude, distanceFilter);

        if (distanceFeeds.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        }

        ResponseEntityModel<DistanceCardDto> response = ResponseEntityModel.<DistanceCardDto>builder()
                .status(ResponseStatus.builder()
                        .httpStatus(HttpStatus.OK)
                        .httpCode(HttpStatus.OK.value())
                        .responseMessage("Success").build())
                .content(NextPageLinkGenerator.appendEachCardDetailLink(distanceFeeds)).build();
        response.add(NextPageLinkGenerator.generateNextPageLink(distanceFeeds));
        return ResponseEntity.ok(response);
    }
}