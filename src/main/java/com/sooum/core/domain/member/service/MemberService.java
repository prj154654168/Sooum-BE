package com.sooum.core.domain.member.service;

import com.sooum.core.domain.member.dto.MemberDto;
import com.sooum.core.domain.member.entity.Member;
import com.sooum.core.domain.member.mapper.MemberMapper;
import com.sooum.core.domain.member.repository.MemberRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import static com.sooum.core.domain.member.dto.AuthDTO.MemberInfo;
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

    public Member save(MemberInfo dto, String deviceId) {
        return memberRepository.save(memberMapper.from(dto, deviceId));
    }

    public void incrementTotalVisitorCnt(Member profileOwnerMember) {
        memberRepository.incrementTotalVisitorCnt(profileOwnerMember);
    }

    public Long findTotalVisitorCnt(Member profileOwnerMember) {
        return memberRepository.findTotalVisitorCnt(profileOwnerMember);
    }

    public MemberDto.MemberStatus findMemberStatus(Long memberPk) {
        return MemberDto.MemberStatus.builder()
                .banEndAt(findByPk(memberPk).getUntilBan())
                .build();
    }
}
