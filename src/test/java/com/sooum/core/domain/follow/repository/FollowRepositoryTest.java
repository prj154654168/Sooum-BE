package com.sooum.core.domain.follow.repository;

import com.sooum.core.domain.block.entity.Block;
import com.sooum.core.domain.block.repository.BlockRepository;
import com.sooum.core.domain.follow.entity.Follow;
import com.sooum.core.domain.member.entity.Member;
import com.sooum.core.domain.member.entity.devicetype.DeviceType;
import com.sooum.core.domain.member.repository.MemberRepository;
import com.sooum.core.domain.tag.repository.CachedTagRepository;
import com.sooum.core.global.config.redis.RedisConfig;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.PageRequest;

import java.util.ArrayList;
import java.util.List;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import(RedisConfig.class)
@MockBean(CachedTagRepository.class)
class FollowRepositoryTest {
    @Autowired
    FollowRepository followRepository;
    @Autowired
    BlockRepository blockRepository;
    @Autowired
    MemberRepository memberRepository;

    private static final int MEMBER_SIZE = 10;
    private static final PageRequest PAGE_REQUEST = PageRequest.ofSize(MEMBER_SIZE);

    @Test
    @DisplayName("팔로워 조회")
    void findFollowersByLastId() throws Exception{
        // given
        List<Member> members = createMembers();
        Member requester = members.get(0);
        Member toMember = members.get(1);
        Long followersLastPk = members.get(MEMBER_SIZE - 1).getPk();
        List<Long> blockedMembers = createBlockMembersPk(members.subList(3, 5), requester);
        List<Member> followers = members.subList(2, members.size());
        createFollowers(followers, toMember);

        // when
        List<Member> findFollowers = followRepository.findFollowersByFollowerLastPk(followersLastPk ,toMember.getPk(), blockedMembers, PAGE_REQUEST);

        // then
        Assertions.assertThat(findFollowers).hasSize(followers.size() - blockedMembers.size() - 1);
        Assertions.assertThat(findFollowers.stream().map(Member::getPk).toList().containsAll(blockedMembers)).isFalse();
    }

    @Test
    @DisplayName("팔로잉 조회")
    void findFollowingsByLastId() throws Exception{
        // given
        List<Member> members = createMembers();
        Member requester = members.get(0);
        Member fromMember = members.get(1);
        Long followersLastPk = members.get(MEMBER_SIZE - 1).getPk();
        List<Long> blockedMembers = createBlockMembersPk(members.subList(3, 5), requester);
        List<Member> followings = members.subList(2, members.size());
        createFollowings(followings, fromMember);

        // when
        List<Member> findFollowers = followRepository.findFollowingsByFollowingLastPk(followersLastPk ,fromMember.getPk(), blockedMembers, PAGE_REQUEST);

        // then
        Assertions.assertThat(findFollowers).hasSize(followings.size() - blockedMembers.size() - 1);
        Assertions.assertThat(findFollowers.stream().map(Member::getPk).toList().containsAll(blockedMembers)).isFalse();
    }

    @Test
    @DisplayName("다른 사람의 팔로잉들 중 팔로잉한 사용자 조회")
    void findFollowedFollowings() throws Exception{
        // given
        List<Member> members = createMembers();
        Member requester = members.get(0);
        Member fromMember = members.get(1);
        List<Member> fromMemberFollowings = createFollowings(members.subList(2, members.size()), fromMember);
        List<Member> requesterFollowings = createFollowings(members.subList(2, 5), requester);

        // when
        List<Long> followedFollowings = followRepository.findFollowedFollowings(requester.getPk(), fromMemberFollowings);

        // then
        Assertions.assertThat(followedFollowings).hasSize(requesterFollowings.size());
    }

    @Test
    @DisplayName("다른 사람의 팔로워들 중 팔로잉한 사용자 조회")
    void findFollowedFollowers() throws Exception{
        // given
        List<Member> members = createMembers();
        Member requester = members.get(0);
        Member toMember = members.get(1);
        List<Member> toMemberFollowings = createFollowers(members.subList(2, members.size()), toMember);
        List<Member> requesterFollowings = createFollowings(members.subList(2, 5), requester);

        // when
        List<Long> followedFollowings = followRepository.findFollowedFollowings(requester.getPk(), toMemberFollowings);

        // then
        Assertions.assertThat(followedFollowings).hasSize(requesterFollowings.size());
    }

    private List<Member> createFollowings(List<Member> followings, Member fromMember) {
        List<Follow> follows = new ArrayList<>();
        for (Member following : followings) {
            Follow follow = Follow.builder()
                    .fromMember(fromMember)
                    .toMember(following)
                    .build();
            follows.add(follow);
        }
        return followRepository.saveAll(follows).stream().map(Follow::getToMember).toList();
    }

    private List<Member> createFollowers(List<Member> followers, Member toMember) {
        List<Follow> follows = new ArrayList<>();
        for (Member follower : followers) {
            Follow follow = Follow.builder()
                    .fromMember(follower)
                    .toMember(toMember)
                    .build();
            follows.add(follow);
        }
        return followRepository.saveAll(follows).stream().map(Follow::getFromMember).toList();
    }

    private List<Long> createBlockMembersPk(List<Member> toMembers, Member fromMember) {
        List<Block> blocks = new ArrayList<>();
        for (Member toMember : toMembers) {
            Block block = Block.builder()
                    .fromMember(fromMember)
                    .toMember(toMember)
                    .build();
            blocks.add(block);
        }
        List<Block> blockMember = blockRepository.saveAll(blocks);
        return blockMember.stream().map(Block::getToMember).map(Member::getPk).toList();
    }

    private List<Member> createMembers() {
        List<Member> members = new ArrayList<>();
        for (int i = 0; i < MEMBER_SIZE; i++) {
            Member member = Member.builder()
                    .deviceId("dev" + i)
                    .deviceType(DeviceType.IOS)
                    .firebaseToken("firbaseDummy")
                    .nickname("nickname")
                    .isAllowNotify(true)
                    .build();
            members.add(member);
        }
        return memberRepository.saveAll(members);
    }
}