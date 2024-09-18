package com.sooum.core.domain.member.service;

import com.sooum.core.domain.member.entity.Member;
import com.sooum.core.domain.member.exception.MemberNotFoundException;
import com.sooum.core.domain.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MemberGetService {
    private final MemberRepository memberRepository;

    public Member findMemberByDeviceId(String deviceId) {
        return memberRepository.findByDeviceId(deviceId)
                .orElseThrow(MemberNotFoundException::new);
    }

    public boolean isAlreadySignUp(String deviceId) {
        return memberRepository.existsByDeviceId(deviceId);
    }
}
