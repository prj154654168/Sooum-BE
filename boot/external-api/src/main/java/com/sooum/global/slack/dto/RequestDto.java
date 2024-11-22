package com.sooum.global.slack.dto;

import lombok.Getter;
import org.springframework.web.util.ContentCachingRequestWrapper;

@Getter
public class RequestDto {
    private final String url;
    private final String method;
    private final String queryParam;
    private final String body;

    public RequestDto(ContentCachingRequestWrapper request) {
        this.body = request.getContentAsString();
        this.queryParam = request.getQueryString();
        this.method = request.getMethod();
        this.url = request.getRequestURL().toString();
    }

    public String getRequestParamOrBody() {
        return this.queryParam != null
                ? queryParam
                : body;
    }
}
