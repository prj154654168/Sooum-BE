package com.sooum.data.rsa.service;

import com.sooum.data.rsa.entity.Rsa;
import com.sooum.data.rsa.repository.RsaRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;

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



    public Rsa findByExpiredAtIsAfter() {
        return rsaRepository.findByExpiredAtIsAfter(LocalDateTime.now())
                .orElseThrow(EntityNotFoundException::new);
    }


}
