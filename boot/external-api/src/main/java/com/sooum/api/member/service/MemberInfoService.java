package com.sooum.api.member.service;

import com.sooum.api.img.service.ImgService;
import com.sooum.api.member.dto.AuthDTO.*;
import com.sooum.api.member.dto.MemberDto.DefaultMemberResponse;
import com.sooum.api.member.exception.PolicyNotAllowException;
import com.sooum.api.member.mapper.MemberMapper;
import com.sooum.api.member.mapper.PolicyMapper;
import com.sooum.api.rsa.service.RsaUseCase;
import com.sooum.data.member.entity.Member;
import com.sooum.data.member.entity.RefreshToken;
import com.sooum.data.member.service.MemberService;
import com.sooum.data.member.service.PolicyService;
import com.sooum.data.member.service.RefreshTokenService;
import com.sooum.global.config.jwt.InvalidTokenException;
import com.sooum.global.config.jwt.TokenProvider;
import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MemberInfoService {

    private final PolicyService policyService;
    private final RefreshTokenService refreshTokenService;
    private final TokenProvider tokenProvider;
    private final MemberService memberService;
    private final RsaUseCase rsaUseCase;
    private final ImgService imgService;
    private final MemberMapper memberMapper;
    private final PolicyMapper policyMapper;

    @Transactional
    public LoginResponse login(Login dto) {
        String deviceId = rsaUseCase.decodeDeviceId(dto.encryptedDeviceId());

        try {
            Member member = memberService.findByDeviceId(deviceId);
            Token token = tokenProvider.createToken(member.getPk());
            RefreshToken refreshToken = refreshTokenService.findByPk(member.getPk());
            refreshToken.update(refreshToken.getRefreshToken());

            return new LoginResponse(true, token);
        } catch (EntityNotFoundException e) {
            return new LoginResponse(false, null);
        }
    }

    @Transactional
    public ReissuedToken reissueAccessToken(HttpServletRequest request) {
        String refreshToken = tokenProvider.getToken(request)
                .orElseThrow(InvalidTokenException::new);

        if (tokenProvider.isAccessToken(refreshToken))    // 재발급은 리프래쉬로만 발급 가능
            throw new InvalidTokenException();

        Member member = memberService.findByPk(tokenProvider.getId(refreshToken).orElseThrow(NoSuchElementException::new));
        return new ReissuedToken(tokenProvider.createAccessToken(member.getPk(), member.getRole()));
    }

    public DefaultMemberResponse getDefaultMember(Member member) {
        return DefaultMemberResponse.builder()
                .id(member.getPk().toString())
                .nickname(member.getNickname())
                .profileImgUrl(imgService.findProfileImgUrl(member.getProfileImgName()))
                .build();
    }

    @Transactional
    public SignUpResponse signUp(SignUp dto) {
        if(!dto.policy().checkAllPolicyIsTrue())
            throw new PolicyNotAllowException();

        String deviceId = rsaUseCase.decodeDeviceId(dto.memberInfo().encryptedDeviceId());

        Member member = memberService.findMember(deviceId);
        if(member == null) {    // if new user's sign up request
            member = memberService.save(memberMapper.from(dto.memberInfo(), deviceId));
            policyService.save(policyMapper.from(dto.policy(), member));
        }

        Token token = tokenProvider.createToken(member.getPk());
        refreshTokenService.save(token.refreshToken(), member);
        return new SignUpResponse(token);
    }
}
