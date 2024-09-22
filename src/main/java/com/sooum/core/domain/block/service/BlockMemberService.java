package com.sooum.core.domain.block.service;

import com.sooum.core.domain.block.repository.BlockRepository;
import com.sooum.core.domain.card.entity.FeedCard;
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

    public List<FeedCard> filterBlockedMembers(List<FeedCard> feedCards, Long memberPk) {
        List<Long> allBlockToPk = blockRepository.findAllBlockToPk(memberPk);
        return feedCards.stream()
                .filter(feedCard -> !allBlockToPk.contains(feedCard.getPk()))
                .toList();
    }
}
