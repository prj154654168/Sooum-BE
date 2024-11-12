package com.sooum.data.block.service;

import com.sooum.data.block.entity.Block;
import com.sooum.data.block.repository.BlockRepository;
import com.sooum.data.card.entity.Card;
import com.sooum.data.member.entity.Member;
import com.sooum.data.member.service.MemberService;
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
            throw new EntityExistsException();
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

    public void deleteBlockMember(Long fromMemberPk, Long toMemberId) {
        Member fromMember = memberService.findByPk(fromMemberPk);
        Member toMember = memberService.findByPk(toMemberId);
        blockRepository.deleteBlockMember(fromMember, toMember);
    }

    public List<Long> findAllBlockToPk(Long memberPk) {
        return blockRepository.findAllBlockToPk(memberPk);
    }

    public void deleteAllBlockMember(Long memberPK) {
        blockRepository.deleteAllBlockMember(memberPK);
    }
}
