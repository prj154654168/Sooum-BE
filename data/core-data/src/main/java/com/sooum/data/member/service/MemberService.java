package com.sooum.data.member.service;

import com.sooum.data.member.entity.Member;
import com.sooum.data.member.entity.devicetype.DeviceType;
import com.sooum.data.member.repository.MemberRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;


@Service
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;

    public Member save(Member member) {
        memberRepository.save(member);
        return member;
    }

    public Member findByDeviceId(String deviceId) {
        return memberRepository.findByDeviceId(deviceId)
                .orElseThrow(() -> new EntityNotFoundException("사용자를 찾을 수 없습니다."));
    }

    public Optional<Member> findMemberOp(String deviceId) {
        return memberRepository.findByDeviceId(deviceId);
    }

    public Member findMember(String deviceId) {
        return memberRepository.findByDeviceId(deviceId)
                .orElse(null);
    }

    public Member findMember(final Long memberPk) {
        return memberRepository.findById(memberPk)
                .orElseThrow(() -> new EntityNotFoundException("사용자를 찾을 수 없습니다."));
    }

    public void incrementTotalVisitorCnt(Member profileOwnerMember) {
        memberRepository.incrementTotalVisitorCnt(profileOwnerMember);
    }

    public Long findTotalVisitorCnt(Member profileOwnerMember) {
        return memberRepository.findTotalVisitorCnt(profileOwnerMember);
    }

    public void deleteMember(Long memberPk) {
        memberRepository.deleteById(memberPk);
    }

    public boolean findMemberNotifyAllow(Long memberPk) {
        return memberRepository.findMemberNotifyAllow(memberPk);
    }

    public void updateDeviceId(String deviceId, Long memberPk) {
        memberRepository.updateDeviceId(deviceId, memberPk);
    }

    public void updateDeviceType(DeviceType deviceType, Long memberPk) {
        memberRepository.updateDeviceType(deviceType, memberPk);
    }

    public void updateDeviceIdAndType(Long memberPk, String deviceId, DeviceType deviceType) {
        updateDeviceId(deviceId, memberPk);
        updateDeviceType(deviceType, memberPk);
    }
}
