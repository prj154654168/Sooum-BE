package com.sooum.data.common.event;

import lombok.Builder;
import lombok.Getter;

@Getter
public class SlackEvent {
    private final String eventMsg;

    @Builder
    public SlackEvent(String eventMsg, String exceptionMsg) {
        this.eventMsg = eventMsg;
    }
}
