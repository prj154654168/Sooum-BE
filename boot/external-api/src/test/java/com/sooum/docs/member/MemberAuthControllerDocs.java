package com.sooum.docs.member;

import com.sooum.docs.RestDocsSupport;
import com.sooum.api.member.controller.MemberAuthController;
import com.sooum.api.member.dto.AuthDTO;
import com.sooum.api.member.service.MemberInfoService;
import com.sooum.api.rsa.service.RsaUseCase;
import com.sooum.global.config.jwt.TokenProvider;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.restdocs.payload.JsonFieldType;

import static org.mockito.BDDMockito.*;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.*;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

public class MemberAuthControllerDocs extends RestDocsSupport {
    private final MemberInfoService memberInfoService = mock(MemberInfoService.class);
    private final RsaUseCase rsaUseCase = mock(RsaUseCase.class);
    private final TokenProvider tokenProvider = mock(TokenProvider.class);
    @Override
    protected Object controllerInitializer() {
        return new MemberAuthController(
                memberInfoService,
                rsaUseCase,
                tokenProvider
        );
    }

    @DisplayName("RSA public key 요청 API")
    @Test
    void requestPublicKey() throws Exception {

        given(rsaUseCase.findPublicKey())
                .willReturn(new AuthDTO.Key("MIGfMA0GCSqGSIb3DQEBAQUAA4GNA" +
                        "DCBiQKBgQCiJ96gHv22oSSezF1v1tRE6uEESgjH0e9Gr3CX2rkvt1H1NIzR" +
                        "piFrCY27+16Z7QIPr4ZxhHbDqvaOh8ZpQ+HXpn5tJO7/yi4bB0bY+60mWSe" +
                        "OW2c3YDASYbMmS/sPity6WyvrctgBpq14JPeJQRLjOoagxupXo1aBfPEJ28zD/QIDAQAB")
                );

        mockMvc.perform(get("/users/key"))
                .andDo(print())
                .andExpect(status().isOk())
                .andDo(document("users-key",
                                preprocessResponse(prettyPrint()),
                                responseFields(
                                        fieldWithPath("publicKey")
                                                .type(JsonFieldType.STRING)
                                                .description("RSA Public Key"),
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
                                                .description("응답 메세지"),
                                        fieldWithPath("links")
                                                .type(JsonFieldType.ARRAY)
                                                .description("HATEOAS 링크 목록"),
                                        fieldWithPath("links[].rel")
                                                .type(JsonFieldType.STRING)
                                                .description("로그인"),
                                        fieldWithPath("links[].href")
                                                .type(JsonFieldType.STRING)
                                                .description("URL")
                                )
                        )
                );
    }
}
