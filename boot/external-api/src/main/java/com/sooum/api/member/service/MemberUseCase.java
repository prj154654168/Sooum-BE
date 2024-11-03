package com.sooum.api.member.service;

import com.sooum.api.member.dto.AuthDTO;
import com.sooum.api.member.dto.MemberDto;
import com.sooum.api.member.mapper.MemberMapper;
import com.sooum.data.member.entity.Member;
import com.sooum.data.member.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MemberUseCase {
    private final MemberService memberService;
    private final MemberMapper memberMapper;

    public MemberDto.MemberStatus findMemberStatus(Long memberPk) {
        return MemberDto.MemberStatus.builder()
                .banEndAt(memberService.findByPk(memberPk).getUntilBan())
                .build();
    }
}
