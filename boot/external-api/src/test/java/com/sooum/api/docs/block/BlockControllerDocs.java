package com.sooum.api.docs.block;

import com.sooum.api.block.controller.BlockController;
import com.sooum.api.block.dto.BlockDto;
import com.sooum.api.docs.RestDocsSupport;
import com.sooum.data.block.service.BlockMemberService;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.restdocs.payload.JsonFieldType;

import static org.mockito.Mockito.mock;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessResponse;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class BlockControllerDocs extends RestDocsSupport {
    private final BlockMemberService blockMemberService = mock(BlockMemberService.class);


    @Test
    void blockMember() throws Exception {
        BlockDto blockDto = new BlockDto(848323993L);
        mockMvc.perform(
                        post("/blocks")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(blockDto))
                                .header("Authorization","Access Token")
                )
                .andDo(print())
                .andExpect(status().isCreated())
                .andDo(
                        document("block",
                                preprocessResponse(prettyPrint()),
                                responseFields(
                                        fieldWithPath("httpCode")
                                                .type(JsonFieldType.NUMBER)
                                                .description("HTTP 상태 코드"),
                                        fieldWithPath("httpStatus")
                                                .type(JsonFieldType.STRING)
                                                .description("HTTP 상태 메세지"),
                                        fieldWithPath("responseMessage")
                                                .type(JsonFieldType.STRING)
                                                .description("응답 메세지")
                                )
                        )
                );
    }

    @Override
    protected Object controllerInitializer() {
        return new BlockController(blockMemberService);
    }
}