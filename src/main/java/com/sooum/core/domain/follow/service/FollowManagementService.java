package com.sooum.core.domain.follow.service;

import com.sooum.core.domain.member.entity.Member;
import com.sooum.core.domain.member.service.MemberService;
import com.sooum.core.global.exceptionmessage.ExceptionMessage;
import jakarta.persistence.EntityExistsException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class FollowManagementService {
    private final FollowService followService;
    private final MemberService memberService;

    @Transactional
    public void saveFollower(final Long fromMemberId, final Long toMemberId) {
        Member fromMember = memberService.findByPk(fromMemberId);
        Member toMember = memberService.findByPk(toMemberId);
        if (followService.isAlreadyFollowing(fromMember, toMember)) {
            throw new EntityExistsException(ExceptionMessage.ALREADY_Following.getMessage());
        }
        followService.saveFollower(fromMember, toMember);
    }

    @Transactional
    public void deleteFollower(final Long fromMemberId, final Long toMemberId) {
        Member fromMember = memberService.findByPk(fromMemberId);
        Member toMember = memberService.findByPk(toMemberId);
        followService.deleteFollower(fromMember, toMember);
    }

}
