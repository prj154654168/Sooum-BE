package com.sooum.core.domain.tag.controller;

import com.sooum.core.domain.tag.dto.TagDto;
import com.sooum.core.domain.tag.service.TagService;
import com.sooum.core.global.responseform.ResponseCollectionModel;
import com.sooum.core.global.responseform.ResponseStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/tags")
public class TagController {

    private final TagService tagService;

    @GetMapping("/{tagPk}")
    ResponseEntity<?> findFeedsByTag(@PathVariable("tagPk") Long tagPk) {
        //TODO: 추후 태그별 피드조회 구현
        return null;
    }

    @GetMapping("")
    ResponseEntity<?> findRelatedTags(@RequestParam String keyword) {
        return ResponseEntity.ok(ResponseCollectionModel.<TagDto.RelatedTag>builder()
                .status(ResponseStatus.builder()
                        .httpStatus(HttpStatus.OK)
                        .httpCode(HttpStatus.OK.value())
                        .responseMessage("Find related tags successfully")
                        .build()
                )
                .content(tagService.findRelatedTags(keyword))
                .build());
    }
}
