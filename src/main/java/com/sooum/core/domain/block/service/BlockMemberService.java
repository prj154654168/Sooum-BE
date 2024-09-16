package com.sooum.core.domain.block.service;

import com.sooum.core.domain.block.repository.BlockRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BlockMemberService {
    private final BlockRepository blockRepository;

    public List<Long> findAllBlockToPk(Long memberPk) {
        return blockRepository.findAllBlockToPk(memberPk);
    }
}
