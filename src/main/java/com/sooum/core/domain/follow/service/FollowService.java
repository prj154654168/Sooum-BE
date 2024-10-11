package com.sooum.core.domain.follow.service;

import com.sooum.core.domain.follow.entity.Follow;
import com.sooum.core.domain.follow.repository.FollowRepository;
import com.sooum.core.domain.member.entity.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class FollowService {
    private final FollowRepository followRepository;

    public boolean isAlreadyFollowing(Member fromMember, Member toMember) {
        return followRepository.findFollow(fromMember, toMember).isPresent();
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void saveFollower(Member fromMember, Member toMember) {
        followRepository.save(Follow.builder()
                .fromMember(fromMember)
                .toMember(toMember)
                .build());
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void deleteFollower(Member fromMember, Member toMember) {
        followRepository.deleteFollower(fromMember, toMember);
    }
}
