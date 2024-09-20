package com.sooum.core.domain.member.service;

import com.sooum.core.domain.member.dto.AuthDTO;
import com.sooum.core.domain.member.entity.Member;
import com.sooum.core.domain.member.mapper.MemberMapper;
import com.sooum.core.domain.member.repository.MemberRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import static com.sooum.core.global.exceptionmessage.ExceptionMessage.MEMBER_NOT_FOUND;

@Service
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;
    private final MemberMapper memberMapper;

    public Member findByDeviceId(String deviceId) {
        return memberRepository.findByDeviceId(deviceId)
                .orElseThrow(() -> new EntityNotFoundException(MEMBER_NOT_FOUND.getMessage()));
    }

    public boolean isAlreadySignUp(String deviceId) {
        return memberRepository.existsByDeviceId(deviceId);
    }

    public Member findByPk(final Long memberPk) {
        return memberRepository.findById(memberPk)
                .orElseThrow(() -> new EntityNotFoundException(MEMBER_NOT_FOUND.getMessage()));
    }

    public Member save(AuthDTO.MemberInfo dto) {
        return memberRepository.save(memberMapper.from(dto));
    }
}
