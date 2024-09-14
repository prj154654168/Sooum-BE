package com.sooum.core.domain.card.controller;

import com.sooum.core.domain.card.service.PopularFeedService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/cards")
public class PopularCardController {
    private final PopularFeedService popularFeedService;



}
