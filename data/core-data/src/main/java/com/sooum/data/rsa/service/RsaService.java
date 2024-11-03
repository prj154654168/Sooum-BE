package com.sooum.data.rsa.service;

import com.sooum.data.rsa.entity.Rsa;
import com.sooum.data.rsa.repository.RsaRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class RsaService {

    private final RsaRepository rsaRepository;

    public Rsa save(Rsa rsa) {
        return rsaRepository.save(rsa);
    }

    public Rsa findByExpiredAtIsAfter() {
        return rsaRepository.findByExpiredAtIsAfter(LocalDateTime.now())
                .orElseThrow(EntityNotFoundException::new);
    }
}
