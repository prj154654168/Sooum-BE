package com.sooum.data.member.service;

import com.sooum.data.member.entity.Member;
import com.sooum.data.member.entity.RefreshToken;
import com.sooum.data.member.repository.RefreshTokenRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RefreshTokenService {

    private final RefreshTokenRepository refreshTokenRepository;

    public void save(String token, Member member) {
        refreshTokenRepository.save(new RefreshToken(token, member));
    }

    public RefreshToken findByPk(Long memberPk) {
        return refreshTokenRepository.findById(memberPk)
                .orElseThrow(() -> new EntityNotFoundException("refreshToken을 찾을 수 없습니다."));
    }

    public void deleteRefreshToken(Long memberPk) {
        refreshTokenRepository.deleteRefreshToken(memberPk);
    }
}
