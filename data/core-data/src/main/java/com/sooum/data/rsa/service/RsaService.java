package com.sooum.data.rsa.service;

import com.sooum.data.rsa.entity.Rsa;
import com.sooum.data.rsa.repository.RsaRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;

@Service
@RequiredArgsConstructor
public class RsaService {

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

    public Rsa findRsaKey() {
        List<Rsa> rsa = rsaRepository.findRsa(LocalDateTime.now(), PageRequest.ofSize(1));
        if (rsa.isEmpty()) {
            throw new IllegalArgumentException("rsa key가 없습니다 findRsaKey()");}
        return rsa.get(0);
    }


}
