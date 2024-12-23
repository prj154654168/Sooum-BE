package com.sooum.api.member.service;

import com.sooum.api.member.dto.MemberDto;
import com.sooum.data.member.entity.Member;
import com.sooum.data.member.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class MemberUseCase {
    private final MemberService memberService;

    public MemberDto.MemberStatus findMemberStatus(Long memberPk) {
        return MemberDto.MemberStatus.builder()
                .banEndAt(memberService.findMember(memberPk).getUntilBan())
                .build();
    }

    @Transactional
    public void updateFCMToken(MemberDto.FCMTokenUpdateRequest requestDto, Long requesterPk) {
        Member requester = memberService.findMember(requesterPk);
        requester.updateFCMToken(requestDto.getFcmToken());
    }

    @Transactional
    public void updateNotifyAllow(MemberDto.NotifyAllowUpdateRequest requestDto, Long requesterPk) {
        Member requester = memberService.findMember(requesterPk);
        requester.updateNotifyAllow(requestDto.isAllowNotify());
    }
}
