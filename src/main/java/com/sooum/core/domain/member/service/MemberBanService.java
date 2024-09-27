package com.sooum.core.domain.member.service;

import com.sooum.core.domain.member.entity.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class MemberBanService {

    private final BlacklistService blacklistService;

    public void ban(Member member, String accessToken) {
        blacklistService.save(accessToken, LocalDateTime.now().plusDays(member.ban()));
    }
}
