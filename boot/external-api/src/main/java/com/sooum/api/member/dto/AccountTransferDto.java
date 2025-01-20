package com.sooum.api.member.dto;

import com.sooum.data.member.entity.devicetype.DeviceType;
import lombok.Builder;
import lombok.Getter;

public class AccountTransferDto {

    @Getter
    public static class TransferAccount {
        private final String transferId;
        private final String encryptedDeviceId;
        private final DeviceType deviceType;

        @Builder
        public TransferAccount(String encryptedDeviceId, String transferId, DeviceType deviceType) {
            this.encryptedDeviceId = encryptedDeviceId;
            this.transferId = transferId;
            this.deviceType = deviceType;
        }
    }
}
