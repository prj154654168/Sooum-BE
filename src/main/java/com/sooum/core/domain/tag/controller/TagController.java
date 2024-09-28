package com.sooum.core.domain.tag.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/tags")
public class TagController {

    @GetMapping("/{tagPk}")
    ResponseEntity<?> findFeedsByTag(@PathVariable("tagPk") Long tagPk) {
        //TODO: 추후 태그별 피드조회 구현
        return null;
    }
}
