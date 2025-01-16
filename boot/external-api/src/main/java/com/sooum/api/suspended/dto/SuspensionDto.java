package com.sooum.api.suspended.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

public class SuspensionDto {

    @Getter
    @NoArgsConstructor
    public static class SuspensionRequest {
        private String encryptedDeviceId;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    public static class SuspensionResponse {
        private LocalDateTime untilBan;
        @JsonProperty("isBanUser")
        private boolean isBanUser;

        @Builder
        public SuspensionResponse(LocalDateTime untilBan, boolean isBanUser) {
            this.untilBan = untilBan;
            this.isBanUser = isBanUser;
        }
    }
}