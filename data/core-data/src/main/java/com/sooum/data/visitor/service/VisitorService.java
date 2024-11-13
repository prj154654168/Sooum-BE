package com.sooum.data.visitor.service;

import com.sooum.data.member.entity.Member;
import com.sooum.data.visitor.entity.Visitor;
import com.sooum.data.visitor.repository.VisitorRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class VisitorService {
    private final VisitorRepository visitorRepository;
    public boolean saveVisitor(Member profileOwnerMember, Member visitorMember){

        if (isNewVisitor(profileOwnerMember, visitorMember)) {
            visitorRepository.save(Visitor.builder()
                    .profileOwner(profileOwnerMember)
                    .visitor(visitorMember)
                    .build());
            return true;
        }
        return false;
    }

    private boolean isNewVisitor(Member profileOwner, Member visitorMember) {
        return visitorRepository.findCurrentDateVisitor(profileOwner, visitorMember).isEmpty();
    }

    public Long findCurrentDateVisitorCnt(Member profileOwnerMember){
        return visitorRepository.findCurrentDateVisitorCnt(profileOwnerMember);
    }

    public void handleVisitorOnMemberWithdraw(Long memberPk) {
        visitorRepository.deleteByProfileOwner(memberPk);
        visitorRepository.updateVisitorToNull(memberPk);
    }
}
