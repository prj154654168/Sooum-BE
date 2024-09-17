package com.sooum.core.domain.member.dto;

import com.sooum.core.domain.member.entity.devicetype.DeviceType;

public class AuthDTO {

    public record Login(
            String deviceId
    ) {}

    public record LoginResponse(
            Boolean isRegistered,
            Token token
    ) {}

    public record SignUp(
            String deviceId,
            DeviceType deviceType,
            String firebaseToken,
            Boolean isAllowTermOne,
            Boolean isAllowTermTwo,
            Boolean isAllowTermThree
    ) {
        public boolean checkAllPolicyIsTrue() {
            return isAllowTermOne && isAllowTermTwo && isAllowTermThree;
        }
    }

    public record SignUpResponse(
            Token token
    ) {}

    public record Token(
            String accessToken,
            String refreshToken
    ) {}

    public record Policy(
            Boolean isAllowTermOne,
            Boolean isAllowTermTwo,
            Boolean isAllowTermThree
    ) {}
}
