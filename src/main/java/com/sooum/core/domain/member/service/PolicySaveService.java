package com.sooum.core.domain.member.service;

import com.sooum.core.domain.member.dto.AuthDTO.SignUp;
import com.sooum.core.domain.member.entity.Member;
import com.sooum.core.domain.member.entity.PolicyTerm;
import com.sooum.core.domain.member.mapper.PolicyMapper;
import com.sooum.core.domain.member.repository.PolicyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PolicySaveService {

    private final PolicyMapper policyMapper;
    private final PolicyRepository policyRepository;

    public PolicyTerm save(SignUp dto, Member member) {
        return policyRepository.save(policyMapper.from(dto, member));
    }
}
