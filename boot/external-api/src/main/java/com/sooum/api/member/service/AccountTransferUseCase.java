package com.sooum.api.member.service;

import com.sooum.api.member.dto.AccountTransferDto;
import com.sooum.api.member.dto.ProfileDto;
import com.sooum.api.notification.dto.FCMDto;
import com.sooum.api.rsa.service.RsaUseCase;
import com.sooum.data.member.entity.AccountTransfer;
import com.sooum.data.member.entity.Member;
import com.sooum.data.member.service.AccountTransferService;
import com.sooum.data.member.service.MemberService;
import com.sooum.data.member.service.RefreshTokenService;
import com.sooum.data.notification.entity.notificationtype.NotificationType;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AccountTransferUseCase {
    private final AccountTransferService accountTransferService;
    private final MemberService memberService;
    private final RsaUseCase rsaUseCase;
    private final MemberWithdrawalService memberWithdrawalService;
    private final RefreshTokenService refreshTokenService;
    private final BlackListUseCase blackListUseCase;
    private final ApplicationEventPublisher sendEventPublisher;

    @Transactional
    public ProfileDto.AccountTransferCodeResponse findOrSaveAccountTransferId(Long memberPk) {
        Optional<AccountTransfer> findAccountTransferOpt = accountTransferService.getByMemberPk(memberPk);
        if (findAccountTransferOpt.isEmpty()) {
            AccountTransfer saveAccountTransfer = accountTransferService.saveAccountTransfer(memberPk);
            return ProfileDto.AccountTransferCodeResponse.builder()
                    .transferCode(saveAccountTransfer.getTransferId())
                    .build();
        }

        AccountTransfer findAccountTransfer = findAccountTransferOpt.get();
        if (findAccountTransfer.isExpired()) {
            findAccountTransfer.updateTransferId(accountTransferService.createTransferId());
        }

        return ProfileDto.AccountTransferCodeResponse.builder()
                .transferCode(findAccountTransfer.getTransferId())
                .build();
    }

    @Transactional
    public ProfileDto.AccountTransferCodeResponse updateAccountTransfer(Long memberPk) {
        AccountTransfer findAccountTransfer = accountTransferService.findAccountTransfer(memberPk);
        findAccountTransfer.updateTransferId(accountTransferService.createTransferId());

        return ProfileDto.AccountTransferCodeResponse.builder()
                .transferCode(findAccountTransfer.getTransferId())
                .build();
    }

    @Transactional
    public void transferAccount(AccountTransferDto.TransferAccount transferAccount) {
        AccountTransfer findAccountTransfer = accountTransferService.findAvailableAccountTransfer(transferAccount.getTransferId());
        Member transferedMember = findAccountTransfer.getMember();

        String decryptedRequesterDeviceId = rsaUseCase.decodeDeviceId(transferAccount.getEncryptedDeviceId());
        withdrawRequesterIfPresent(decryptedRequesterDeviceId);
        saveTransferedMemberRefreshTokenInBlackList(transferedMember.getPk());

        memberService.updateDeviceIdAndType(transferedMember.getPk(), decryptedRequesterDeviceId, transferAccount.getDeviceType());
        accountTransferService.deleteAccountTransfer(transferedMember.getPk());

        sendEventPublisher.publishEvent(
                FCMDto.SystemFcmSendEvent.builder()
                        .notificationId(1L)
                        .notificationType(NotificationType.TRANSFER_SUCCESS)
                        .targetDeviceType(transferedMember.getDeviceType())
                        .targetFcmToken(transferedMember.getFirebaseToken())
                        .source(this)
                        .build()
        );
    }

    private void withdrawRequesterIfPresent(String decryptedDeviceId) {
        Optional<Member> requesterOp = memberService.findMemberOp(decryptedDeviceId);
        requesterOp.ifPresent(member -> memberWithdrawalService.withdrawMember(member.getPk()));
    }

    private void saveTransferedMemberRefreshTokenInBlackList(Long transferMemberPk) {
        String refreshToken = refreshTokenService.findRefreshToken(transferMemberPk);
        blackListUseCase.saveBlackListRefreshToken(refreshToken);
    }
}
