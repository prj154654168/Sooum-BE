package com.sooum.data.member.service;

import com.sooum.data.member.entity.Blacklist;
import com.sooum.data.member.repository.BlacklistRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BlacklistService {
    private final BlacklistRepository blacklistRepository;

    public void save(Blacklist blacklist) {
        blacklistRepository.save(blacklist);
    }
    public void saveAll (List<Blacklist> blacklists) {
        blacklistRepository.saveAll(blacklists);
    }
}
