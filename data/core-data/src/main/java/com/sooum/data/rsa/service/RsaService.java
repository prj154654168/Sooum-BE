package com.sooum.data.rsa.service;

import com.sooum.data.common.event.SlackEvent;
import com.sooum.data.rsa.entity.Rsa;
import com.sooum.data.rsa.repository.RsaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class RsaService {
    private final ApplicationEventPublisher eventPublisher;
    private final RsaRepository rsaRepository;


    public Rsa save(HashMap<String, String> keyPair, LocalDateTime expiredAt) {
        Rsa rsa = Rsa.builder()
                .publicKey(keyPair.get("publicKey"))
                .privateKey(keyPair.get("privateKey"))
                .expiredAt(expiredAt)
                .build();

        return rsaRepository.save(rsa);
    }

    public void deleteExpiredRsaKey() {
        rsaRepository.deleteExpiredKey(LocalDateTime.now());
    }

    public Rsa findLatestRsaKey() {
        Optional<Rsa> latestRsaKey = rsaRepository.findLatestRsaKey(LocalDateTime.now());
        return latestRsaKey.orElseGet(this::findLastRsaKey);
    }

    private Rsa findLastRsaKey() {
        Optional<Rsa> lastGeneratedRsaKey = rsaRepository.findLastGeneratedRsaKey();

        if (lastGeneratedRsaKey.isEmpty()) {
            throw new IllegalArgumentException("Rsa 테이블이 비어있습니다.");
        }
        eventPublisher.publishEvent(SlackEvent.builder()
                .eventMsg("Rsa Key가 업데이트 되지 않아 가장 마지막 Rsa Key를 반환하였습니다. Schedule log를 확인해주세요.")
                .build());
        return lastGeneratedRsaKey.get();
    }
}
