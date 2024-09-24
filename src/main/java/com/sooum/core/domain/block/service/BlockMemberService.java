package com.sooum.core.domain.block.service;

import com.sooum.core.domain.block.repository.BlockRepository;
import com.sooum.core.domain.card.entity.Card;
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

    public <T extends Card> List<T> filterBlockedMembers (List<T> cards, Long memberPk) {
        List<Long> allBlockToPk = blockRepository.findAllBlockToPk(memberPk);
        return cards.stream()
                .filter(feedCard -> !allBlockToPk.contains(feedCard.getPk()))
                .toList();
    }
}
