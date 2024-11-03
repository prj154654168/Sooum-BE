package com.sooum.data.member.service;

import com.sooum.data.member.repository.TransferIdRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TransferIdService {
    private final TransferIdRepository transferIdRepository;

    public String findTransferId() {
        return transferIdRepository.findRandomTransferId();
    }
}
