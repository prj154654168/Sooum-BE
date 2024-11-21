package com.sooum.global.slack.service;

import com.slack.api.Slack;
import com.slack.api.model.Attachment;
import com.slack.api.model.block.ContextBlock;
import com.slack.api.model.block.SectionBlock;
import com.slack.api.model.block.composition.MarkdownTextObject;
import com.sooum.global.slack.dto.RequestDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.util.ContentCachingRequestWrapper;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static com.slack.api.webhook.WebhookPayloads.payload;

@Slf4j
@Service
@Profile("prod")
@RequiredArgsConstructor
public class SlackService {
    @Value("${slack.webhook.url}")
    private String url;
    private final Slack slack = Slack.getInstance();

    @Async
    public void sendSlackMsg(Exception e, ContentCachingRequestWrapper request) {
        try {
            RequestDto requestDto = new RequestDto(request);
            slack.send(url, payload(p -> p.attachments(List.of(createSlackMsg(e, requestDto)))));
        } catch (IOException ignored) {
        }
    }

    private Attachment createSlackMsg(Exception e, RequestDto requestDto) {
        return Attachment.builder()
                .blocks(List.of(
                        ContextBlock.builder()
                                .blockId("error_context")
                                .elements(List.of(
                                        MarkdownTextObject.builder()
                                                .text("*🚨 API 에러 발생 알림 🚨*").build()
                                ))
                                .build(),

                        SectionBlock.builder()
                                .blockId("error_time")
                                .text(MarkdownTextObject.builder()
                                        .text("*Request Time:*\n`" + LocalDateTime.now() + "`")
                                        .build())
                                .build(),

                        SectionBlock.builder()
                                .blockId("error_summary")
                                .text(MarkdownTextObject.builder()
                                        .text("*Request URL:*\n`" + requestDto.getUrl() + " " + requestDto.getMethod() + "`")
                                        .build())
                                .build(),

                        SectionBlock.builder()
                                .blockId("error_parameter")
                                .text(MarkdownTextObject.builder()
                                        .text("*Request:*\n```" + requestDto.getRequestParamOrBody() + "```")
                                        .build())
                                .build(),

                        SectionBlock.builder()
                                .blockId("error_message")
                                .text(MarkdownTextObject.builder()
                                        .text("*Error Message:*\n```" + e.getMessage() + "```")
                                        .build())
                                .build(),

                        ContextBlock.builder()
                                .blockId("error_stacktrace")
                                .elements(List.of(
                                        MarkdownTextObject.builder()
                                                .text("*Error StackTrace:*\n```" +
                                                        Arrays.stream(e.getStackTrace())
                                                                .map(StackTraceElement::toString)
                                                                .collect(Collectors.joining("\n")) +
                                                        "```")
                                                .build()
                                ))
                                .build()
                ))
                .build();
    }
}
