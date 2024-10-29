package com.sooum.core.domain.member.service;

import com.sooum.core.domain.img.service.ImgService;
import com.sooum.core.domain.member.dto.MemberDto;
import com.sooum.core.domain.member.entity.Member;
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

import static com.sooum.core.domain.member.dto.AuthDTO.*;

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
    private final ImgService imgService;

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
    public void signUp(SignUp dto, Long memberPk) {
        Member member = memberService.findByPk(memberPk);
        member.signUp(dto);
    }

    @Transactional
    public ReissuedToken reissueAccessToken(HttpServletRequest request) {
        String accessToken = tokenProvider.getAccessToken(request)
                .orElseThrow(InvalidTokenException::new);

        Member member = memberService.findByPk(tokenProvider.getId(accessToken).orElseThrow(MemberNotFoundException::new));
        blacklistService.save(accessToken, tokenProvider.getExpiration(accessToken));

        return new ReissuedToken(tokenProvider.createAccessToken(member.getPk(), member.getRole()));
    }

    public MemberDto.DefaultMemberResponse getDefaultMember(Member member) {
        return MemberDto.DefaultMemberResponse.builder()
                .id(member.getPk().toString())
                .nickname(member.getNickname())
                .profileImgUrl(imgService.findProfileImgUrl(member.getProfileImgName()))
                .build();
    }

    @Transactional
    public SignUpResponse acceptPolicies(AcceptPolicies dto) {
        if(!dto.policy().checkAllPolicyIsTrue())
            throw new PolicyNotAllowException();

        String deviceId = rsaService.decodeDeviceId(dto.memberInfo().encryptedDeviceId());

        if(memberService.isAlreadySignUp(deviceId))
            throw new DuplicateSignUpException();

        Member dummyMember = memberService.save(dto.memberInfo(), deviceId);

        policySaveService.save(dto.policy(), dummyMember);

        Token token = tokenProvider.createToken(dummyMember.getPk());
        refreshTokenSaveService.save(token.refreshToken(), dummyMember);
        return new SignUpResponse(token);
    }
}
