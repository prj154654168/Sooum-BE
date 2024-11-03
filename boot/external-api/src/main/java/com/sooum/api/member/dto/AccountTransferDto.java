package com.sooum.api.member.dto;

import lombok.Builder;
import lombok.Getter;

public class AccountTransferDto {

    @Getter
    public static class TransferAccount {
        private final String transferId;
        private final String encryptedDeviceId;

        @Builder
        public TransferAccount(String encryptedDeviceId, String transferId) {
            this.encryptedDeviceId = encryptedDeviceId;
            this.transferId = transferId;
        }
    }
}
