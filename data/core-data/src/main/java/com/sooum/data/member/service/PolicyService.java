package com.sooum.data.member.service;

import com.sooum.data.member.entity.PolicyTerm;
import com.sooum.data.member.repository.PolicyTermRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PolicyService {
    private final PolicyTermRepository policyRepository;

    public PolicyTerm save(PolicyTerm policyTerm) {
        return policyRepository.save(policyTerm);
    }
}
