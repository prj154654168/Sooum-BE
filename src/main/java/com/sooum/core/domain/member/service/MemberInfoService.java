package com.sooum.core.domain.member.service;

import com.sooum.core.domain.member.dto.AuthDTO;
import com.sooum.core.domain.member.entity.Member;
import com.sooum.core.domain.member.entity.PolicyTerm;
import com.sooum.core.domain.member.exception.DuplicateSignUpException;
import com.sooum.core.domain.member.exception.MemberNotFoundException;
import com.sooum.core.domain.member.exception.PolicyNotAllowException;
import com.sooum.core.global.config.jwt.TokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MemberInfoService {
    private final PolicyService policySaveService;
    private final RefreshTokenService refreshTokenSaveService;
    private final TokenProvider tokenProvider;
    private final MemberService memberInfoService;


    public AuthDTO.LoginResponse login(AuthDTO.Login dto) {
        // DeviceID RSA Decode

        try {
            Member member = memberInfoService.findByDeviceId(dto.deviceId());
            return new AuthDTO.LoginResponse(true, tokenProvider.createToken(member.getPk()));
        } catch (MemberNotFoundException e) {
            return new AuthDTO.LoginResponse(false, null);
        }
    }

    @Transactional
    public AuthDTO.SignUpResponse signUp(AuthDTO.SignUp dto) {
        if(!dto.policy().checkAllPolicyIsTrue())
            throw new PolicyNotAllowException();

        if(memberInfoService.isAlreadySignUp(dto.member().deviceId()))
            throw new DuplicateSignUpException();

        Member member = memberInfoService.save(dto.member());
        PolicyTerm policyTerm = policySaveService.save(dto.policy(), member);
        AuthDTO.Token token = tokenProvider.createToken(policyTerm.getPk());
        refreshTokenSaveService.save(token.refreshToken(), member);
        return new AuthDTO.SignUpResponse(token);
    }
}
