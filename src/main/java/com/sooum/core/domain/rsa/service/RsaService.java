package com.sooum.core.domain.rsa.service;

import com.sooum.core.domain.member.dto.AuthDTO.Key;
import com.sooum.core.domain.rsa.entity.Rsa;
import com.sooum.core.domain.rsa.exception.RsaDecodeException;
import com.sooum.core.domain.rsa.repository.RsaRepository;
import com.sooum.core.global.rsa.RsaProvider;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.HashMap;

@Service
@RequiredArgsConstructor
public class RsaService {

    private final RsaRepository rsaRepository;
    private final RsaProvider rsaProvider;
    private final RedisTemplate<String, String> redisTemplate;

    public Key save() {
        HashMap<String, String> keyPair = rsaProvider.generateKeyPair();

        // Save to RDB
        Instant expiredAt = LocalDateTime.now().plusMonths(6).atZone(ZoneId.systemDefault()).toInstant();
        Rsa rsa = Rsa.builder()
                .publicKey(keyPair.get("publicKey"))
                .privateKey(keyPair.get("privateKey"))
                .expiredAt(expiredAt)
                .build();
        rsaRepository.save(rsa);

        // Save to Redis
        redisTemplate.opsForValue().set("public", rsa.getPublicKey(), Duration.between(Instant.now(), expiredAt));
        redisTemplate.opsForValue().set("private", rsa.getPrivateKey(), Duration.between(Instant.now(), expiredAt));

        return new Key(rsa.getPublicKey());
    }

    public Key findPublicKey() {
        String publicKey;

        try {
            publicKey = redisTemplate.opsForValue().get("public");
        } catch (Exception e) {
            Rsa rsa = rsaRepository.findByExpiredAtIsAfter(Instant.now())
                    .orElseThrow(EntityNotFoundException::new);
            publicKey = rsa.getPublicKey();
        }

        return new Key(publicKey);
    }

    public String decodeDeviceId(String encryptedData) {
        String privateKey;

        try {
            privateKey = redisTemplate.opsForValue().get("private");
        } catch (Exception e) {
            Rsa rsa = rsaRepository.findByExpiredAtIsAfter(Instant.now())
                    .orElseThrow(RsaDecodeException::new);
            privateKey = rsa.getPrivateKey();
        }

        return rsaProvider.decode(encryptedData, privateKey);
    }
}
