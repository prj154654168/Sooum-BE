package com.sooum.core.domain.member.service;

import com.sooum.core.domain.member.dto.AccountTransferDto;
import com.sooum.core.domain.member.dto.ProfileDto;
import com.sooum.core.domain.member.entity.AccountTransfer;
import com.sooum.core.domain.member.entity.Member;
import com.sooum.core.domain.member.repository.AccountTransferRepository;
import com.sooum.core.domain.rsa.service.RsaService;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.mockito.BDDMockito.*;

@ExtendWith(MockitoExtension.class)
class AccountTransferServiceTest {
    @Mock
    AccountTransferRepository accountTransferRepository;
    @Mock
    RsaService rsaService;
    @Mock
    MemberService memberService;
    @Mock
    TransferIdService transferIdService;
    @Spy @InjectMocks
    AccountTransferService accountTransferService;

    @Test
    @DisplayName("만료 안된 계정 이관 코드 바로 조회")
    void findTransferAccountId_isExistAndNotExpired() throws Exception{
        // given
        AccountTransfer mockAccountTransfer = mock(AccountTransfer.class);
        given(accountTransferRepository.findByMember_Pk(any())).willReturn(Optional.of(mockAccountTransfer));
        given(mockAccountTransfer.isExpired()).willReturn(false);
        given(mockAccountTransfer.getTransferId()).willReturn("mockTransferId");

        // when
        ProfileDto.AccountTransferCodeResponse response = accountTransferService.findOrSaveAccountTransferId(any());

        // then
        Assertions.assertThat(response.getTransferCode()).isEqualTo("mockTransferId");
    }

    @Test
    @DisplayName("만료된 계정 이관 코드 업데이트 후 조회")
    void findTransferAccountId_isExistAndExpired() throws Exception{
        // given
        AccountTransfer mockAccountTransfer = mock(AccountTransfer.class);
        given(accountTransferRepository.findByMember_Pk(any())).willReturn(Optional.of(mockAccountTransfer));
        given(mockAccountTransfer.isExpired()).willReturn(true);
        doNothing().when(mockAccountTransfer).updateTransferId(any());

        // when
        ProfileDto.AccountTransferCodeResponse response = accountTransferService.findOrSaveAccountTransferId(any());

        // then
        verify(mockAccountTransfer).updateTransferId(any());
    }

    @Test
    @DisplayName("계정 이관 코드 생성 후 조회")
    void findTransferAccountId_save() throws Exception{
        // given
        AccountTransfer mockAccountTransfer = mock(AccountTransfer.class);
        given(accountTransferRepository.findByMember_Pk(any())).willReturn(Optional.empty());
        given(memberService.findByPk(any())).willReturn(mock(Member.class));
        given(accountTransferRepository.save(any())).willReturn(AccountTransfer.builder().transferId("123").build());

        // when
        ProfileDto.AccountTransferCodeResponse response = accountTransferService.findOrSaveAccountTransferId(any());

        // then
        Assertions.assertThat(response.getTransferCode()).isEqualTo("123");
        verify(accountTransferRepository).save(any());
    }

    @Test
    @DisplayName("계정 이관 코드 업데이트 성공")
    void updateAccountTransfer() throws Exception{
        // given
        AccountTransfer mockAccountTransfer = mock(AccountTransfer.class);
        given(accountTransferRepository.findByMember_Pk(any())).willReturn(Optional.of(mockAccountTransfer));
        doNothing().when(mockAccountTransfer).updateTransferId(any());

        // when
        accountTransferService.updateAccountTransfer(any());

        // then
        verify(mockAccountTransfer).updateTransferId(any());
    }

    @Test
    @DisplayName("계정 이관 성공")
    void transferAccount() throws Exception{
        // given
        AccountTransfer mockAccountTransfer = mock(AccountTransfer.class);
        Member mockMember = mock(Member.class);
        String mockDeviceId = "mockDeviceId";
        given(accountTransferRepository.findAvailableAccountTransfer(any())).willReturn(Optional.of(mockAccountTransfer));
        given(rsaService.decodeDeviceId(any())).willReturn(mockDeviceId);
        given(mockAccountTransfer.getMember()).willReturn(mockMember);
        doNothing().when(mockMember).updateDeviceId(any());

        // when
        accountTransferService.transferAccount(AccountTransferDto.TransferAccount.builder().build());

        // then
        verify(mockMember).updateDeviceId(any());
    }

    @Test
    @DisplayName("계정 이관 코드 생성")
    void createTransferId() throws Exception{
        // given
        String mockTransferId = "mockTransferId";
        given(transferIdService.findTransferId()).willReturn(mockTransferId).willReturn(mockTransferId);
        given(accountTransferRepository.existsByTransferId(any())).willReturn(true).willReturn(false);

        // when
        accountTransferService.createTransferId();

        // then
        verify(accountTransferRepository, times(2)).existsByTransferId(any());
    }


}