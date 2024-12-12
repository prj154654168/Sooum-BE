package com.sooum.api.suspended.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class SuspensionDto {
    private String encryptedDeviceId;

    @Builder
    public SuspensionDto(String encryptedDeviceId) {
        this.encryptedDeviceId = encryptedDeviceId;
    }
}
