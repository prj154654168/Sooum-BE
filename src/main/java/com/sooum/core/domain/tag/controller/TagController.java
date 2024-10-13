package com.sooum.core.domain.tag.controller;

import com.sooum.core.domain.tag.dto.TagDto;
import com.sooum.core.domain.tag.service.FavoriteTagService;
import com.sooum.core.domain.tag.service.RecommendTagService;
import com.sooum.core.domain.tag.service.TagService;
import com.sooum.core.global.auth.annotation.CurrentUser;
import com.sooum.core.global.responseform.ResponseCollectionModel;
import com.sooum.core.global.responseform.ResponseEntityModel;
import com.sooum.core.global.responseform.ResponseStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;
import java.util.Optional;

@RestController
@RequiredArgsConstructor
@RequestMapping("/tags")
public class TagController {
    private final FavoriteTagService favoriteTagService;
    private final TagService tagService;
    private final RecommendTagService recommendTagService;

    @GetMapping("/search")
    ResponseEntity<?> findRelatedTags(@RequestParam String keyword, @RequestParam Integer size) {
        return ResponseEntity.ok(ResponseCollectionModel.<TagDto.RelatedTag>builder()
                .status(ResponseStatus.builder()
                        .httpStatus(HttpStatus.OK)
                        .httpCode(HttpStatus.OK.value())
                        .responseMessage("Find related tags successfully")
                        .build()
                )
                .content(tagService.findRelatedTags(keyword, size))
                .build());
    }

    @PostMapping("/{tagPk}/favorite")
    public ResponseEntity<ResponseStatus> saveFavoriteTag(@PathVariable Long tagPk,
                                                          @CurrentUser Long memberPk) {
        favoriteTagService.saveFavoriteTag(tagPk, memberPk);

        return ResponseEntity.created(URI.create(""))
                .body(ResponseStatus.builder()
                        .httpCode(HttpStatus.CREATED.value())
                        .httpStatus(HttpStatus.CREATED)
                        .responseMessage("Favorite tag save successfully")
                        .build());
    }

    @DeleteMapping("/{tagPk}/favorite")
    public ResponseEntity<ResponseStatus> deleteFavoriteTag(@PathVariable Long tagPk,
                                                            @CurrentUser Long memberPk) {
        favoriteTagService.deleteFavoriteTag(tagPk, memberPk);

        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{tagPk}/summary")
    public ResponseEntity<ResponseEntityModel<TagDto.TagSummary>> createTagSummary(@PathVariable Long tagPk,
                                                                                   @CurrentUser Long memberPk) {
        TagDto.TagSummary tagSummary = tagService.createTagSummary(tagPk, memberPk);

        return ResponseEntity.ok(
                ResponseEntityModel.<TagDto.TagSummary>builder()
                        .status(ResponseStatus.builder()
                                .httpCode(HttpStatus.OK.value())
                                .httpStatus(HttpStatus.OK)
                                .responseMessage("Tag summary retrieve successfully")
                                .build())
                        .content(tagSummary)
                        .build()
        );
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

    @GetMapping({"/favorite", "/favorite/{last}"})
    ResponseEntity<?> findFavoriteTags(@PathVariable(required = false, value = "last") Optional<Long> last,
                                       @CurrentUser Long memberPk) {
        List<TagDto.FavoriteTag> myFavoriteTags = favoriteTagService.findMyFavoriteTags(memberPk, last.orElse(0L));
        if (myFavoriteTags.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(ResponseCollectionModel.<TagDto.FavoriteTag>builder()
                .status(ResponseStatus.builder()
                        .httpStatus(HttpStatus.OK)
                        .httpCode(HttpStatus.OK.value())
                        .responseMessage("Favorite tags retrieved successfully.")
                        .build()
                ).content(myFavoriteTags)
                .build()
        );
    }
}