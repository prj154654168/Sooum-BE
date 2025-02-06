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
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static com.slack.api.webhook.WebhookPayloads.payload;

@Slf4j
@Service
@RequiredArgsConstructor
public class SlackService {
    @Value("${slack.webhook.url}")
    private String url;
    private final Slack slack = Slack.getInstance();

    @Async
    public void sendSlackErrorMsg(Exception e, RequestDto requestDto) {
        try {
            slack.send(url, payload(p -> p.attachments(List.of(createSlackMsg(e, requestDto)))));
        } catch (IOException ioE) {
            log.error(ioE.getMessage(), ioE);
            log.error(Arrays.toString(ioE.getStackTrace()));
        }
    }

    public void sendSlackFCMMsg() {
        try {
            slack.send(url, payload(p -> p.attachments(List.of(createFcmSlackMsg()))));
        } catch (IOException ioE) {
            log.error(ioE.getMessage(), ioE);
            log.error(Arrays.toString(ioE.getStackTrace()));
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

    private Attachment createFcmSlackMsg() {
        return Attachment.builder()
                .blocks(List.of(
                        ContextBlock.builder()
                                .blockId("error_context")
                                .elements(List.of(
                                        MarkdownTextObject.builder()
                                                .text("*🚨 FCM 에러 발생 알림 🚨*").build()
                                ))
                                .build(),

                        SectionBlock.builder()
                                .blockId("error_time")
                                .text(MarkdownTextObject.builder()
                                        .text("*Error Execute Time:*\n`" + LocalDateTime.now() + "`")
                                        .build())
                                .build(),

                        SectionBlock.builder()
                                .blockId("error_message")
                                .text(MarkdownTextObject.builder()
                                        .text("*Error Message:*\n```" + "FCM Server 문제로 알림 전송이 안됩니다." + "```")
                                        .build())
                                .build()
                ))
                .build();
    }
}
