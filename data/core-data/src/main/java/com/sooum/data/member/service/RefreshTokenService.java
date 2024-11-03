package com.sooum.data.member.service;

import com.sooum.data.member.entity.Member;
import com.sooum.data.member.entity.RefreshToken;
import com.sooum.data.member.repository.RefreshTokenRepository;
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
