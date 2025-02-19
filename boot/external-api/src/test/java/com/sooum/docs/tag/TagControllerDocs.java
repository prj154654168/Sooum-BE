//package com.sooum.docs.tag;
//
//import com.google.common.net.MediaType;
//import com.sooum.api.tag.controller.TagController;
//import com.sooum.api.tag.dto.TagDto;
//import com.sooum.api.tag.service.FavoriteTagUseCase;
//import com.sooum.api.tag.service.RecommendTagUseCase;
//import com.sooum.api.tag.service.TagUseCase;
//import com.sooum.data.tag.service.FavoriteTagService;
//import com.sooum.docs.RestDocsSupport;
//import org.junit.jupiter.api.DisplayName;
//import org.junit.jupiter.api.Test;
//import org.springframework.hateoas.Link;
//import org.springframework.restdocs.payload.JsonFieldType;
//
//import java.util.List;
//
//import static org.mockito.ArgumentMatchers.any;
//import static org.mockito.BDDMockito.*;
//import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
//import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessResponse;
//import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
//import static org.springframework.restdocs.payload.PayloadDocumentation.*;
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
//import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
//
//public class TagControllerDocs extends RestDocsSupport {
//    private final FavoriteTagUseCase favoriteTagUseCase = mock(FavoriteTagUseCase.class);
//    private final FavoriteTagService favoriteTagService = mock(FavoriteTagService.class);
//    private final TagUseCase tagUseCase = mock(TagUseCase.class);
//    private final RecommendTagUseCase recommendTagUseCase = mock(RecommendTagUseCase.class);
//
//    @Override
//    protected Object controllerInitializer() {
//        return new TagController(favoriteTagUseCase, favoriteTagService, tagUseCase, recommendTagUseCase);
//    }
//
//    @DisplayName("즐겨찾기 태그 조회 API")
//    @Test
//    void findFavoriteTags() throws Exception {
//        TagDto.FavoriteTag tag1 = TagDto.FavoriteTag.builder()
//                .id("637022162983887395")
//                .tagContent("tag1")
//                .tagUsageCnt("2")
//                .previewCards(List.of(
//                        TagDto.FavoriteTag.PreviewCard.builder()
//                                .id("637026827597304652")
//                                .content("feed-content1")
//                                .backgroundImgUrl(Link.of("https://sooum-img.s3.ap-northeast-2.amazonaws.com/card/default/abcfr.jpeg")).build(),
//                        TagDto.FavoriteTag.PreviewCard.builder()
//                                .id("637022162845475571")
//                                .content("feed-content2")
//                                .backgroundImgUrl(Link.of("https://sooum-img.s3.ap-northeast-2.amazonaws.com/card/default/abcfr.jpeg")).build()
//                )).build();
//
//        TagDto.FavoriteTag tag2 = TagDto.FavoriteTag.builder()
//                .id("637022162979693253")
//                .tagContent("tag2")
//                .tagUsageCnt("1")
//                .previewCards(List.of(
//                        TagDto.FavoriteTag.PreviewCard.builder()
//                                .id("637022162845475571")
//                                .content("feed-content2")
//                                .backgroundImgUrl(Link.of("https://sooum-img.s3.ap-northeast-2.amazonaws.com/card/default/abcfr.jpeg")).build()
//                )).build();
//
//        given(favoriteTagUseCase.findTop5FeedByFavoriteTags(any(), any())).willReturn(List.of(tag1, tag2));
//        mockMvc.perform(get("/tags/favorites").header("Authorization","Access Token").accept(String.valueOf(MediaType.HAL_JSON)))
//                .andDo(print())
//                .andExpect(status().isOk())
//                .andDo(
//                        document("favorite-tag",
//                                preprocessResponse(prettyPrint()),
//                                responseFields(
//                                        fieldWithPath("_embedded.favoriteTagList[]")
//                                                .type(JsonFieldType.ARRAY)
//                                                .description("즐겨찾기 태그 목록"),
//                                        fieldWithPath("_embedded.favoriteTagList[].id")
//                                                .type(JsonFieldType.STRING)
//                                                .description("태그 ID"),
//                                        fieldWithPath("_embedded.favoriteTagList[].tagContent")
//                                                .type(JsonFieldType.STRING)
//                                                .description("태그 내용"),
//                                        fieldWithPath("_embedded.favoriteTagList[].tagUsageCnt")
//                                                .type(JsonFieldType.STRING)
//                                                .description("태그 사용 횟수"),
//                                        fieldWithPath("_embedded.favoriteTagList[].previewCards[]")
//                                                .type(JsonFieldType.ARRAY)
//                                                .description("미리보기 카드 목록"),
//                                        fieldWithPath("_embedded.favoriteTagList[].previewCards[].id")
//                                                .type(JsonFieldType.STRING)
//                                                .description("카드 ID"),
//                                        fieldWithPath("_embedded.favoriteTagList[].previewCards[].content")
//                                                .type(JsonFieldType.STRING)
//                                                .description("카드 내용"),
//                                        fieldWithPath("_embedded.favoriteTagList[].previewCards[].backgroundImgUrl")
//                                                .type(JsonFieldType.OBJECT)
//                                                .description("카드 배경 이미지 URL"),
//                                        fieldWithPath("_embedded.favoriteTagList[].previewCards[].backgroundImgUrl.href")
//                                                .type(JsonFieldType.STRING)
//                                                .description("배경 이미지 URL"),
//                                        fieldWithPath("links")
//                                                .type(JsonFieldType.ARRAY)
//                                                .description("links"),
//                                        fieldWithPath("status")
//                                                .type(JsonFieldType.OBJECT)
//                                                .description("응답 상태 정보"),
//                                        fieldWithPath("status.httpCode")
//                                                .type(JsonFieldType.NUMBER)
//                                                .description("HTTP 상태 코드"),
//                                        fieldWithPath("status.httpStatus")
//                                                .type(JsonFieldType.STRING)
//                                                .description("HTTP 상태 메시지"),
//                                        fieldWithPath("status.responseMessage")
//                                                .type(JsonFieldType.STRING)
//                                                .description("응답 메시지")
//                                ).)
//                );
//
//    }
//
//}
