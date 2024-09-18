package com.sooum.core.domain.member.usecase;

import com.sooum.core.domain.member.entity.Member;
import com.sooum.core.domain.member.entity.PolicyTerm;
import com.sooum.core.domain.member.exception.DuplicateSignUpException;
import com.sooum.core.domain.member.exception.MemberNotFoundException;
import com.sooum.core.domain.member.exception.PolicyNotAllowException;
import com.sooum.core.domain.member.service.MemberGetService;
import com.sooum.core.domain.member.service.MemberSaveService;
import com.sooum.core.domain.member.service.PolicySaveService;
import com.sooum.core.domain.member.service.RefreshTokenSaveService;
import com.sooum.core.global.config.jwt.TokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.sooum.core.domain.member.dto.AuthDTO.*;

@Service
@RequiredArgsConstructor
public class MemberUseCase {

    private final MemberGetService memberGetService;
    private final TokenProvider tokenProvider;
    private final MemberSaveService memberSaveService;
    private final PolicySaveService policySaveService;
    private final RefreshTokenSaveService refreshTokenSaveService;

    public LoginResponse login(Login dto) {
        // DeviceID RSA Decode

        try {
            Member member = memberGetService.findMemberByDeviceId(dto.deviceId());
            return new LoginResponse(true, tokenProvider.createToken(member.getPk()));
        } catch (MemberNotFoundException e) {
            return new LoginResponse(false, null);
        }
    }

    @Transactional
    public SignUpResponse signUp(SignUp dto) {
        if(!dto.policy().checkAllPolicyIsTrue())
            throw new PolicyNotAllowException();

        if(memberGetService.isAlreadySignUp(dto.member().deviceId()))
            throw new DuplicateSignUpException();

        Member member = memberSaveService.save(dto.member());
        PolicyTerm policyTerm = policySaveService.save(dto.policy(), member);
        Token token = tokenProvider.createToken(policyTerm.getPk());
        refreshTokenSaveService.save(token.refreshToken(), member);
        return new SignUpResponse(token);
    }
}
