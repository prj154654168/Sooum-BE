package com.sooum.core.domain.member.usecase;

import com.sooum.core.domain.member.entity.Member;
import com.sooum.core.domain.member.entity.PolicyTerm;
import com.sooum.core.domain.member.exception.MemberNotFoundException;
import com.sooum.core.domain.member.exception.PolicyNotAllowException;
import com.sooum.core.domain.member.service.MemberGetService;
import com.sooum.core.domain.member.service.MemberSaveService;
import com.sooum.core.domain.member.service.PolicySaveService;
import com.sooum.core.global.config.jwt.TokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import static com.sooum.core.domain.member.dto.AuthDTO.*;

@Service
@RequiredArgsConstructor
public class MemberUseCase {

    private final MemberGetService memberGetService;
    private final TokenProvider tokenProvider;
    private final MemberSaveService memberSaveService;
    private final PolicySaveService policySaveService;

    public LoginResponse login(Login dto) {
        // DeviceID RSA Decode

        try {
            Member member = memberGetService.findMemberByDeviceId(dto.deviceId());
            return new LoginResponse(true, tokenProvider.createToken(member.getPk()));
        } catch (MemberNotFoundException e) {
            return new LoginResponse(false, null);
        }
    }

    // 논의 사항: isAllowNotify를 DTO로 받을 것인가? 아니면 디폴트 값으로 둘 것인가?
    // 논의 사항: signUp 컨트롤러는 시나리오 상 로그인 이후에만 호출되어 이미 가입된 유저가 또 호출될 일이 없지만 혹시 모르니 막는게 좋을까요?
    public SignUpResponse signUp(SignUp dto) {
        if(!dto.checkAllPolicyIsTrue())
            throw new PolicyNotAllowException();

        Member member = memberSaveService.save(dto);
        PolicyTerm policyTerm = policySaveService.save(dto, member);

        return new SignUpResponse(tokenProvider.createToken(policyTerm.getPk()));
    }
}
