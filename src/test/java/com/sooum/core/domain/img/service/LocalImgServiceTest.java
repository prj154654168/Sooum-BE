package com.sooum.core.domain.img.service;

import com.sooum.core.domain.img.dto.ImgUrlInfo;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@ExtendWith(MockitoExtension.class)
class LocalImgServiceTest {
    private static final int DEFAULT_IMG_CNT = 8;
    private static final String DEFAULT_IMG_EXTENSION = "png";
    private static final int defaultImgSize = 50;
    private static final LocalImgService localImgService = new LocalImgService();

    @BeforeEach
    public void init() {
        ReflectionTestUtils.setField(localImgService, "defaultImgSize", defaultImgSize);
    }
    @Test
    @DisplayName("전에 조회된 이미지가 있을 경우")
    void createDefaultImgInfos_existPreviousImgs() throws Exception{
        // given
        List<String> allDefaultImgs = IntStream.rangeClosed(1, 50)
                .boxed()
                .map(img -> img + "." + DEFAULT_IMG_EXTENSION)
                .collect(Collectors.toList());
        Collections.shuffle(allDefaultImgs);
        List<String> previousDefaultImgs = allDefaultImgs.subList(0, DEFAULT_IMG_CNT);

        // when
        List<ImgUrlInfo> result = localImgService.createDefaultImgRetrieveUrls(previousDefaultImgs);

        // then
        List<String> resultImgNames = result.stream().map(ImgUrlInfo::getImgName).toList();
        boolean isNotDuplicate = resultImgNames.stream().noneMatch(previousDefaultImgs::contains);
        Assertions.assertThat(isNotDuplicate).isTrue();
    }

    @Test
    @DisplayName("전에 조회된 이미지가 없을 경우")
    void createDefaultImgInfos_notExistPreviousImgs() throws Exception{
        // given

        // when
        List<ImgUrlInfo> result = localImgService.createDefaultImgRetrieveUrls(Collections.emptyList());

        // then
        Assertions.assertThat(result.size()).isEqualTo(DEFAULT_IMG_CNT);
    }
}