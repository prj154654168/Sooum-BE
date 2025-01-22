package com.sooum.api.member.dto;

import com.sooum.data.member.entity.devicetype.DeviceType;
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

    public record SignUpResponse(
            Token token
    ) {}

    public record Token(
            String accessToken,
            String refreshToken
    ) {}

    public record SignUp(
            @Valid MemberInfo memberInfo,
            @Valid Policy policy
    ) {}

    public record MemberInfo(
            @NotEmpty String encryptedDeviceId,
            @NotNull DeviceType deviceType,
            String firebaseToken,
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
