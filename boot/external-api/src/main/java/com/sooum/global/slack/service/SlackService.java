package com.sooum.global.slack.service;

import com.slack.api.Slack;
import com.slack.api.model.Attachment;
import com.slack.api.model.block.ContextBlock;
import com.slack.api.model.block.SectionBlock;
import com.slack.api.model.block.composition.MarkdownTextObject;
import com.slack.api.webhook.WebhookResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
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
@RequiredArgsConstructor
public class SlackService {
    @Value("${slack.webhook.url}")
    private String url;
    private final Slack slack = Slack.getInstance();

    public void sendSlackMsg(Exception e, ContentCachingRequestWrapper request) {
        try {
            WebhookResponse response = slack.send(url, payload(p -> p
                    .attachments(List.of(createSlackMsg(e, request)))));
            log.info(response.getBody());
        } catch (IOException ex) {
            log.error("slack webhook error");
        }
    }

    private Attachment createSlackMsg(Exception e, ContentCachingRequestWrapper request) {
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
                                        .text("*Request URL:*\n`" + request.getRequestURL() + " " + request.getMethod() + "`")
                                        .build())
                                .build(),

                        SectionBlock.builder()
                                .blockId("error_parameter")
                                .text(MarkdownTextObject.builder()
                                        .text("*Request:*\n```" + getRequestParamOrBody(request) + "```")
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

    private String getRequestParamOrBody(ContentCachingRequestWrapper request) {
        return request.getQueryString() != null
                ? request.getQueryString()
                : request.getContentAsString();
    }
}
