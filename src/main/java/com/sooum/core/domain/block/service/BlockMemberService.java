package com.sooum.core.domain.block.service;

import com.sooum.core.domain.block.entity.Block;
import com.sooum.core.domain.block.repository.BlockRepository;
import com.sooum.core.domain.card.entity.Card;
import com.sooum.core.domain.member.entity.Member;
import com.sooum.core.domain.member.service.MemberService;
import com.sooum.core.global.exceptionmessage.ExceptionMessage;
import jakarta.persistence.EntityExistsException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BlockMemberService {
    private final BlockRepository blockRepository;
    private final MemberService memberService;

    public void saveBlockMember(Long fromMemberPk, Long toMemberPk) {
        if (blockRepository.existsByFromMemberPkAndToMemberPk(fromMemberPk, toMemberPk)) {
            throw new EntityExistsException(ExceptionMessage.ALREADY_BLOCKED.getMessage());
        }

        Member fromMember = memberService.findByPk(fromMemberPk);
        Member toMember = memberService.findByPk(toMemberPk);

        blockRepository.save(Block.builder()
                .toMember(toMember)
                .fromMember(fromMember).build());
    }

    public <T extends Card> List<T> filterBlockedMembers (List<T> cards, Long memberPk) {
        List<Long> allBlockToPk = findAllBlockToPk(memberPk);
        return cards.stream()
                .filter(feedCard -> !allBlockToPk.contains(feedCard.getPk()))
                .toList();
    }

    public List<Long> findAllBlockToPk(Long memberPk) {
        return blockRepository.findAllBlockToPk(memberPk);
    }
}
