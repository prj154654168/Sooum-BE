package com.sooum.core.domain.tag.controller;

import com.sooum.core.domain.tag.dto.TagDto;
import com.sooum.core.domain.tag.service.RecommendTagService;
import com.sooum.core.global.auth.annotation.CurrentUser;
import com.sooum.core.global.responseform.ResponseCollectionModel;
import com.sooum.core.global.responseform.ResponseStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/tags")
public class TagController {
    private final RecommendTagService recommendTagService;

    @GetMapping("/{tagPk}")
    ResponseEntity<?> findFeedsByTag(@PathVariable("tagPk") Long tagPk) {
        //TODO: 추후 태그별 피드조회 구현
        return null;
    }

    @GetMapping("/recommendation")
    ResponseEntity<?> findRecommendationsByTag(@CurrentUser Long memberPk) {
        List<TagDto.RecommendTag> recommendedTags = recommendTagService.findRecommendedTags(memberPk);
        if (recommendedTags.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(
                ResponseCollectionModel.<TagDto.RecommendTag>builder()
                        .status(ResponseStatus.builder()
                                .httpStatus(HttpStatus.OK)
                                .httpCode(HttpStatus.OK.value())
                                .responseMessage("Retrieve recommended tag list data")
                                .build()
                        )
                        .content(recommendedTags)
                        .build()
        );
    }
}
