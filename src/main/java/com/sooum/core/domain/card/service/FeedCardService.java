package com.sooum.core.domain.card.service;

import com.sooum.core.domain.card.entity.FeedCard;
import com.sooum.core.domain.card.repository.FeedCardRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class FeedCardService {
    private final FeedCardRepository feedCardRepository;
    private static final int MAX_PAGE_SIZE = 100;

    List<FeedCard> findByLastId(Long lastId) {
        Pageable pageRequest = PageRequest.of(0, MAX_PAGE_SIZE);
        if (lastId.equals(0L)) {
            return feedCardRepository.findFirstPage(pageRequest);
        }
        return feedCardRepository.findByNextPage(lastId, pageRequest);
    }
}
