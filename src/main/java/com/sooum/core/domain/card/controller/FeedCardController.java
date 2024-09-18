package com.sooum.core.domain.card.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.hateoas.EntityModel;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/cards")
public class FeedCardController {
    @GetMapping("/{cardPk}")
    public EntityModel<?> findFeedCardInfo(@PathVariable("cardPk") Long cardPk) {
        //todo 추후에 글 상세보기 api 구현
        return null;
    }
}
