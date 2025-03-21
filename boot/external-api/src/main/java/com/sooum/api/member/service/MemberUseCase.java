package com.sooum.api.member.service;

import com.sooum.api.member.dto.MemberDto;
import com.sooum.data.member.entity.Member;
import com.sooum.data.member.entity.Role;
import com.sooum.data.member.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class MemberUseCase {
    private final MemberService memberService;

    @Transactional
    public MemberDto.MemberStatus findMemberStatus(Long memberPk) {
        Member member = memberService.findMember(memberPk);
        if(isBanPeriodExpired(member)){
            member.unban();
        }
        return MemberDto.MemberStatus.builder()
                .banEndAt(member.getUntilBan())
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

    public MemberDto.NotifyAllowResponse findNotifyAllow(Long requesterPk) {
        return MemberDto.NotifyAllowResponse.builder()
                .isAllowNotify(memberService.findMemberNotifyAllow(requesterPk))
                .build();
    }

    private static boolean isBanPeriodExpired(Member member) {
        return member.getRole() == Role.BANNED && member.getUntilBan().isBefore(LocalDateTime.now());
    }
}
