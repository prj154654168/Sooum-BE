package com.sooum.core.domain.card.service;

import com.sooum.core.domain.card.repository.FeedCardRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class FeedCardService {
    private final FeedCardRepository feedCardRepository;
}
