package com.sooum.core.domain.member.service;

import com.sooum.core.domain.member.dto.AuthDTO.MemberInfo;
import com.sooum.core.domain.member.entity.Member;
import com.sooum.core.domain.member.mapper.MemberMapper;
import com.sooum.core.domain.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MemberSaveService {

    private final MemberRepository memberRepository;
    private final MemberMapper memberMapper;

    public Member save(MemberInfo dto) {
        return memberRepository.save(memberMapper.from(dto));
    }
}
