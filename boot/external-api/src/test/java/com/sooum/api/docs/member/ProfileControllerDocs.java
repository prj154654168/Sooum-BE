package com.sooum.api.docs.member;

import com.sooum.api.docs.RestDocsSupport;
import com.sooum.api.follow.service.FollowInfoService;
import com.sooum.api.member.controller.ProfileController;
import com.sooum.api.member.dto.ProfileDto;
import com.sooum.api.member.service.ProfileInfoUseCase;
import com.sooum.api.member.service.ProfileService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.hateoas.Link;
import org.springframework.restdocs.payload.JsonFieldType;


import static org.mockito.BDDMockito.*;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.*;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class ProfileControllerDocs extends RestDocsSupport {
    private final ProfileService profileService = mock(ProfileService.class);
    private final ProfileInfoUseCase profileInfoUseCase = mock(ProfileInfoUseCase.class);
    private final FollowInfoService followInfoService = mock(FollowInfoService.class);
    @Override
    protected Object controllerInitializer() {
        return new ProfileController(profileService, profileInfoUseCase, followInfoService);
    }

    @DisplayName("내 프로필 조회 API")
    @Test
    void findMyProfileInfo() throws Exception {
        ProfileDto.MyProfileInfoResponse myProfileInfoResponse = ProfileDto.MyProfileInfoResponse.builder()
                .nickname("조용한 사자")
                .currentDayVisitors(12L)
                .totalVisitorCnt(30L)
                .profileImg(Link.of("https://sooum-img.s3.ap-northeast-2.amazonaws.com/profile/f18b3ad4-ddad-441e-898d-bae3c0900e11.JPEG?X-Amz-Algorithm=AWS4-HMAC-SHA256&X-Amz-Date=20241204T005855Z&X-Amz-SignedHeaders=host&X-Amz-Credential=AKIAU72LGIH6NKMQTJ4L%2F20241204%2Fap-northeast-2%2Fs3%2Faws4_request&X-Amz-Expires=86400&X-Amz-Signature=3245de8f5c52b19e3308d33b47eb68755451b1b70c8e4f03402a72b6b91ef9f6"))
                .cardCnt(20L)
                .followerCnt(10L)
                .followingCnt(15L)
                .build();
        given(profileInfoUseCase.findMyProfileInfo(any()))
                .willReturn(myProfileInfoResponse);

        mockMvc.perform(get("/profiles/my").header("Authorization","Access Token"))
                .andDo(print())
                .andExpect(status().isOk())
                .andDo(
                        document("profile",
                                preprocessResponse(prettyPrint()),
                                responseFields(

                                        fieldWithPath("nickname")
                                                .type(JsonFieldType.STRING)
                                                .description("닉네임"),
                                        fieldWithPath("currentDayVisitors")
                                                .type(JsonFieldType.STRING)
                                                .description("당일 방문자 수"),
                                        fieldWithPath("totalVisitorCnt")
                                                .type(JsonFieldType.STRING)
                                                .description("전체 방문자 수"),
                                        fieldWithPath("profileImg")
                                                .type(JsonFieldType.OBJECT)
                                                .description("프로필 이미지 정보"),
                                        fieldWithPath("profileImg.rel")
                                                .type(JsonFieldType.STRING)
                                                .description("rel"),
                                        fieldWithPath("profileImg.href")
                                                .type(JsonFieldType.STRING)
                                                .description("프로필 이미지 URL"),
                                        fieldWithPath("links")
                                                .type(JsonFieldType.ARRAY)
                                                .description("links"),
                                        fieldWithPath("cardCnt")
                                                .type(JsonFieldType.STRING)
                                                .description("작성한 카드 개수"),
                                        fieldWithPath("followingCnt")
                                                .type(JsonFieldType.STRING)
                                                .description("팔로잉 유저 수"),
                                        fieldWithPath("followerCnt")
                                                .type(JsonFieldType.STRING)
                                                .description("팔로워 유저 수"),
                                        fieldWithPath("status")
                                                .type(JsonFieldType.OBJECT)
                                                .description("응답 상태 정보"),
                                        fieldWithPath("status.httpCode")
                                                .type(JsonFieldType.NUMBER)
                                                .description("HTTP 상태 코드"),
                                        fieldWithPath("status.httpStatus")
                                                .type(JsonFieldType.STRING)
                                                .description("HTTP 상태 메세지"),
                                        fieldWithPath("status.responseMessage")
                                                .type(JsonFieldType.STRING)
                                                .description("응답 메세지")
                                )
                        )
                );
    }
}
