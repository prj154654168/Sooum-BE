package com.sooum.core.domain.member.service;

import com.sooum.core.domain.member.entity.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.temporal.ChronoUnit;

@Service
@RequiredArgsConstructor
public class MemberBanService {

    private final BlacklistService blacklistService;

    public void ban(Member member, String accessToken) {
        blacklistService.save(accessToken, Duration.of(member.ban(), ChronoUnit.DAYS));
    }
}
