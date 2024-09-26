package com.sooum.core.domain.member.service;

import com.sooum.core.domain.member.dto.AuthDTO.Login;
import com.sooum.core.domain.member.dto.AuthDTO.LoginResponse;
import com.sooum.core.domain.member.dto.AuthDTO.ReissuedToken;
import com.sooum.core.domain.member.dto.AuthDTO.SignUpResponse;
import com.sooum.core.domain.member.dto.MemberDto;
import com.sooum.core.domain.member.entity.Member;
import com.sooum.core.domain.member.entity.PolicyTerm;
import com.sooum.core.domain.member.exception.DuplicateSignUpException;
import com.sooum.core.domain.member.exception.MemberNotFoundException;
import com.sooum.core.domain.member.exception.PolicyNotAllowException;
import com.sooum.core.domain.rsa.service.RsaService;
import com.sooum.core.global.config.jwt.InvalidTokenException;
import com.sooum.core.global.config.jwt.TokenProvider;
import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalTime;

import static com.sooum.core.domain.member.dto.AuthDTO.SignUp;
import static com.sooum.core.domain.member.dto.AuthDTO.Token;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MemberInfoService {
    private final PolicyService policySaveService;
    private final RefreshTokenService refreshTokenSaveService;
    private final TokenProvider tokenProvider;
    private final MemberService memberService;
    private final BlacklistService blacklistService;
    private final RsaService rsaService;


    public LoginResponse login(Login dto) {
        String deviceId = rsaService.decodeDeviceId(dto.encryptedDeviceId());

        try {
            Member member = memberService.findByDeviceId(deviceId);
            return new LoginResponse(true, tokenProvider.createToken(member.getPk()));
        } catch (EntityNotFoundException e) {
            return new LoginResponse(false, null);
        }
    }

    @Transactional
    public SignUpResponse signUp(SignUp dto) {
        if(!dto.policy().checkAllPolicyIsTrue())
            throw new PolicyNotAllowException();

        String deviceId = rsaService.decodeDeviceId(dto.member().encryptedDeviceId());

        if(memberService.isAlreadySignUp(deviceId))
            throw new DuplicateSignUpException();

        Member member = memberService.save(dto.member(), deviceId);
        PolicyTerm policyTerm = policySaveService.save(dto.policy(), member);
        Token token = tokenProvider.createToken(policyTerm.getPk());
        refreshTokenSaveService.save(token.refreshToken(), member);
        return new SignUpResponse(token);
    }

    @Transactional
    public ReissuedToken reissueAccessToken(HttpServletRequest request) {
        String accessToken = tokenProvider.getAccessToken(request)
                .orElseThrow(InvalidTokenException::new);

        Member member = memberService.findByPk(tokenProvider.getId(accessToken).orElseThrow(MemberNotFoundException::new));
        blacklistService.save(accessToken, Duration.between(LocalTime.now(), tokenProvider.getExpiration(accessToken)));

        return new ReissuedToken(tokenProvider.createAccessToken(member.getPk(), member.getRole()));
    }

    public MemberDto.DefaultMemberResponse getDefaultMember(Member member) {
        return MemberDto.DefaultMemberResponse.builder()
                .id(member.getPk())
                .nickname(member.getNickname())
                .profileImgUrl(null) //TODO: 프로필 이미지 URL 추가
                .build();
    }
}
