package com.sooum.core.domain.member.service;

import com.sooum.core.domain.member.entity.Member;
import com.sooum.core.domain.member.entity.RefreshToken;
import com.sooum.core.domain.member.repository.RefreshTokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RefreshTokenService {

    private final RefreshTokenRepository refreshTokenRepository;

    public void save(String token, Member member) {
        refreshTokenRepository.save(new RefreshToken(token, member));
    }
}
