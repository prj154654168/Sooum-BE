package com.sooum.api.tag.controller;

import com.sooum.api.tag.dto.TagDto;
import com.sooum.api.tag.service.FavoriteTagUseCase;
import com.sooum.api.tag.service.RecommendTagService;
import com.sooum.api.tag.service.TagUseCase;
import com.sooum.data.tag.service.FavoriteTagService;
import com.sooum.global.auth.annotation.CurrentUser;
import com.sooum.global.responseform.ResponseCollectionModel;
import com.sooum.global.responseform.ResponseEntityModel;
import com.sooum.global.responseform.ResponseStatus;
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
    private final FavoriteTagUseCase favoriteTagUseCase;
    private final FavoriteTagService favoriteTagService;
    private final TagUseCase tagUseCase;
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
                .content(tagUseCase.findRelatedTags(keyword, size))
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
        TagDto.TagSummary tagSummary = tagUseCase.createTagSummary(tagPk, memberPk);

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

    @GetMapping({"/favorites", "/favorites/{last}"})
    ResponseEntity<?> findFavoriteTags(@PathVariable(required = false, value = "last") Optional<Long> last,
                                       @CurrentUser Long memberPk) {
        List<TagDto.FavoriteTag> myFavoriteTags = favoriteTagUseCase.findMyFavoriteTags(memberPk, last.orElse(0L));
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