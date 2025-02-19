package com.sooum.api.tag.service;

import com.sooum.api.IntegrationTestSupport;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class FavoriteTagUseCaseTest extends IntegrationTestSupport {

    @Autowired
    private FavoriteTagUseCase favoriteTagUseCase;

    @DisplayName("")
    @Test
    void findTop5FeedByFavoriteTags() {

    }
}
