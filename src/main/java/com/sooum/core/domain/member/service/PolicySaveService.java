package com.sooum.core.domain.member.service;

import com.sooum.core.domain.member.dto.AuthDTO.Policy;
import com.sooum.core.domain.member.entity.Member;
import com.sooum.core.domain.member.entity.PolicyTerm;
import com.sooum.core.domain.member.mapper.PolicyMapper;
import com.sooum.core.domain.member.repository.PolicyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PolicySaveService {

    private final PolicyMapper policyMapper;
    private final PolicyRepository policyRepository;

    public PolicyTerm save(Policy dto, Member member) {
        return policyRepository.save(policyMapper.from(dto, member));
    }
}
