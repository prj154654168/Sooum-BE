package com.sooum.core.domain.member.service;

import com.sooum.core.domain.member.dto.AuthDTO;
import com.sooum.core.domain.member.dto.AuthDTO.LoginResponse;
import com.sooum.core.domain.member.dto.AuthDTO.SignUpResponse;
import com.sooum.core.domain.member.entity.Member;
import com.sooum.core.domain.member.entity.PolicyTerm;
import com.sooum.core.domain.member.exception.DuplicateSignUpException;
import com.sooum.core.domain.member.exception.MemberNotFoundException;
import com.sooum.core.domain.member.exception.PolicyNotAllowException;
import com.sooum.core.global.config.jwt.EmptyTokenException;
import com.sooum.core.global.config.jwt.TokenProvider;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalTime;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MemberInfoService {
    private final PolicyService policySaveService;
    private final RefreshTokenService refreshTokenSaveService;
    private final TokenProvider tokenProvider;
    private final MemberService memberService;
    private final BlacklistService blacklistService;


    public LoginResponse login(AuthDTO.Login dto) {
        // DeviceID RSA Decode

        try {
            Member member = memberService.findByDeviceId(dto.deviceId());
            return new LoginResponse(true, tokenProvider.createToken(member.getPk()));
        } catch (MemberNotFoundException e) {
            return new LoginResponse(false, null);
        }
    }

    @Transactional
    public SignUpResponse signUp(AuthDTO.SignUp dto) {
        if(!dto.policy().checkAllPolicyIsTrue())
            throw new PolicyNotAllowException();

        if(memberService.isAlreadySignUp(dto.member().deviceId()))
            throw new DuplicateSignUpException();

        Member member = memberService.save(dto.member());
        PolicyTerm policyTerm = policySaveService.save(dto.policy(), member);
        AuthDTO.Token token = tokenProvider.createToken(policyTerm.getPk());
        refreshTokenSaveService.save(token.refreshToken(), member);
        return new SignUpResponse(token);
    }

    public String reissueAccessToken(HttpServletRequest request) {
        String accessToken = tokenProvider.getAccessToken(request)
                .orElseThrow(EmptyTokenException::new);

        Member member = memberService.findByPk(tokenProvider.getId(accessToken).orElse(null));
        blacklistService.save(accessToken, Duration.between(LocalTime.now(), tokenProvider.getExpiration(accessToken)));

        return tokenProvider.createAccessToken(member.getPk(), member.getRole());
    }
}
