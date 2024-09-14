package com.sooum.core.domain.member.service;

import com.sooum.core.domain.member.entity.Member;
import com.sooum.core.domain.member.repository.MemberRepository;
import com.sooum.core.global.exceptionmessage.ExceptionMessage;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MemberService {
    private final MemberRepository memberRepository;

    public Member findByPk(final Long memberPk) {
        return memberRepository.findById(memberPk)
                .orElseThrow(() -> new EntityNotFoundException(ExceptionMessage.MEMBER_NOT_FOUND.getMessage()));
    }
}
