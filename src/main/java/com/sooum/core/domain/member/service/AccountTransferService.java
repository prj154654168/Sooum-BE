package com.sooum.core.domain.member.service;

import com.sooum.core.domain.member.dto.AccountTransferDto;
import com.sooum.core.domain.member.dto.ProfileDto;
import com.sooum.core.domain.member.entity.AccountTransfer;
import com.sooum.core.domain.member.repository.AccountTransferRepository;
import com.sooum.core.domain.rsa.service.RsaService;
import com.sooum.core.global.exceptionmessage.ExceptionMessage;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@RequiredArgsConstructor
public class AccountTransferService {
    private final MemberService memberService;
    private final AccountTransferRepository accountTransferRepository;
    private final RsaService rsaService;
    private final TransferIdService transferIdService;

    @Transactional
    public ProfileDto.AccountTransferCodeResponse findOrSaveAccountTransferId(Long memberPk) {
        Optional<AccountTransfer> findAccountTransferOpt = accountTransferRepository.findByMember_Pk(memberPk);
        if (findAccountTransferOpt.isEmpty()) {
            AccountTransfer saveAccountTransfer = saveAccountTransfer(memberPk);
            return ProfileDto.AccountTransferCodeResponse.builder()
                    .transferCode(saveAccountTransfer.getTransferId())
                    .build();
        }

        AccountTransfer findAccountTransfer = findAccountTransferOpt.get();
        if (findAccountTransfer.isExpired()) {
            findAccountTransfer.updateTransferId(createTransferId());
        }

        return ProfileDto.AccountTransferCodeResponse.builder()
                .transferCode(findAccountTransfer.getTransferId())
                .build();
    }

    @Transactional
    public AccountTransfer saveAccountTransfer(Long memberPk) {
        return accountTransferRepository.save(AccountTransfer.builder()
                .member(memberService.findByPk(memberPk))
                .transferId(createTransferId())
                .build());
    }

    @Transactional
    public ProfileDto.AccountTransferCodeResponse updateAccountTransfer(Long memberPk) {
        AccountTransfer findAccountTransfer = findAccountTransfer(memberPk);
        findAccountTransfer.updateTransferId(createTransferId());

        return ProfileDto.AccountTransferCodeResponse.builder()
                .transferCode(findAccountTransfer.getTransferId())
                .build();
    }

    protected String createTransferId() {
        boolean isExistTransferId;
        String transferId;

        do {
            transferId = transferIdService.findTransferId() + (100 + new Random().nextInt(900));
            isExistTransferId = accountTransferRepository.existsByTransferId(transferId);
        } while (isExistTransferId);

        return transferId;
    }

    public AccountTransfer findAccountTransfer(Long memberPk) {
        return accountTransferRepository.findByMember_Pk(memberPk)
                .orElseThrow(() -> new EntityNotFoundException(ExceptionMessage.ACCOUNT_TRANSFER_NOT_FOUND.getMessage()));
    }

    @Transactional
    public void transferAccount(AccountTransferDto.TransferAccount transferAccount) {
        AccountTransfer findAccountTransfer = findAvailableAccountTransfer(transferAccount.getTransferId());

        String decryptedDeviceId = rsaService.decodeDeviceId(transferAccount.getEncryptedDeviceId());
        findAccountTransfer.getMember().updateDeviceId(decryptedDeviceId);
    }

    public AccountTransfer findAvailableAccountTransfer(String transferId) {
        return accountTransferRepository.findAvailableAccountTransfer(transferId)
                .orElseThrow(() -> new EntityNotFoundException(ExceptionMessage.ACCOUNT_TRANSFER_NOT_FOUND.getMessage()));
    }
}
