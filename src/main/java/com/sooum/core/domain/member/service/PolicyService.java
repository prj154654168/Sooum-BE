package com.sooum.core.domain.member.service;

import com.sooum.core.domain.member.dto.AuthDTO.Policy;
import com.sooum.core.domain.member.entity.Member;
import com.sooum.core.domain.member.entity.PolicyTerm;
import com.sooum.core.domain.member.mapper.PolicyMapper;
import com.sooum.core.domain.member.repository.PolicyTermRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PolicyService {

    private final PolicyMapper policyMapper;
    private final PolicyTermRepository policyRepository;

    public PolicyTerm save(Policy dto, Member member) {
        return policyRepository.save(policyMapper.from(dto, member));
    }
}
