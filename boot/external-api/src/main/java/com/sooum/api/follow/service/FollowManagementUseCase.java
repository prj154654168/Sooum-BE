package com.sooum.api.follow.service;

import com.sooum.data.follow.service.FollowService;
import com.sooum.data.member.entity.Member;
import com.sooum.data.member.service.MemberService;
import com.sooum.global.exceptionmessage.ExceptionMessage;
import jakarta.persistence.EntityExistsException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class FollowManagementUseCase {
    private final FollowService followService;
    private final MemberService memberService;

    @Transactional
    public void saveFollower(final Long fromMemberId, final Long toMemberId) {
        Member fromMember = memberService.findMember(fromMemberId);
        Member toMember = memberService.findMember(toMemberId);
        if (followService.isAlreadyFollowing(fromMember, toMember)) {
            throw new EntityExistsException(ExceptionMessage.ALREADY_Following.getMessage());
        }
        followService.saveFollower(fromMember, toMember);
    }

    @Transactional
    public void deleteFollower(final Long fromMemberId, final Long toMemberId) {
        Member fromMember = memberService.findMember(fromMemberId);
        Member toMember = memberService.findMember(toMemberId);
        followService.deleteFollower(fromMember, toMember);
    }

}
