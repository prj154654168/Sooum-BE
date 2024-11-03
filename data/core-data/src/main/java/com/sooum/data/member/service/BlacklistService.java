package com.sooum.data.member.service;

import com.sooum.data.member.entity.Blacklist;
import com.sooum.data.member.repository.BlacklistRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BlacklistService {
    private final BlacklistRepository blacklistRepository;

    public void save(Blacklist blacklist) {
        blacklistRepository.save(blacklist);
    }
}
