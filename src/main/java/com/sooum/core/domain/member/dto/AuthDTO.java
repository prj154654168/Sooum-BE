package com.sooum.core.domain.member.dto;

import com.sooum.core.domain.member.entity.devicetype.DeviceType;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

public class AuthDTO {

    public record Login(
            @NotEmpty String encryptedDeviceId
    ) {}

    public record LoginResponse(
            Boolean isRegistered,
            Token token
    ) {}

    public record SignUp(
            @Valid MemberInfo member,
            @Valid Policy policy
    ) {}

    public record SignUpResponse(
            Token token
    ) {}

    public record Token(
            String accessToken,
            String refreshToken
    ) {}

    public record MemberInfo(
            @NotEmpty String encryptedDeviceId,
            @NotNull DeviceType deviceType,
            @NotEmpty String firebaseToken,
            @NotNull Boolean isAllowNotify
    ) {}

    public record Policy(
            @NotNull Boolean isAllowTermOne,
            @NotNull Boolean isAllowTermTwo,
            @NotNull Boolean isAllowTermThree
    ) {
        public boolean checkAllPolicyIsTrue() {
            return isAllowTermOne && isAllowTermTwo && isAllowTermThree;
        }
    }

    public record Key(
            String publicKey
    ) {}

    public record ReissuedToken(
            String accessToken
    ) {}
}
