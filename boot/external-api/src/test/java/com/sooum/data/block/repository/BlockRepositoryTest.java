package com.sooum.data.block.repository;

import com.sooum.data.DataJpaTestSupport;
import com.sooum.data.block.entity.Block;
import com.sooum.data.member.entity.Member;
import com.sooum.data.member.entity.devicetype.DeviceType;
import com.sooum.data.member.repository.MemberRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;


class BlockRepositoryTest extends DataJpaTestSupport {
    @Autowired
    private BlockRepository blockRepository;
    @Autowired
    private MemberRepository memberRepository;

    @DisplayName("멤버의 pk로 차단한 모든 사용자들의 pk를 조회한다.")
    @Test
    void findAllBlockToPk() {
        Member member1 = saveMember();
        Member member2 = saveMember();
        Member member3 = saveMember();

        saveBlockBy(member1, member2); //맴버1이 멤버2 차단
        saveBlockBy(member1, member3); //맴버1이 멤버3 차단

        assertThat(blockRepository.findAllBlockToPk(member1.getPk()))
                .hasSize(2)
                .containsExactly(member2.getPk(), member3.getPk());
    }
    private Member saveMember() {
        return memberRepository.save(Member.builder()
                .deviceId(UUID.randomUUID().toString())
                .deviceType(DeviceType.IOS)
                .nickname(UUID.randomUUID().toString())
                .build()
        );
    }

    private Block saveBlockBy(Member fromMember, Member toMember) {
        return blockRepository.save(Block.builder()
                .fromMember(fromMember)
                .toMember(toMember)
                .build()
        );
    }
}