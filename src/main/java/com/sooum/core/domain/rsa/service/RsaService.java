package com.sooum.core.domain.rsa.service;

import com.sooum.core.domain.rsa.entity.Rsa;
import com.sooum.core.domain.rsa.repository.RsaRepository;
import com.sooum.core.global.rsa.RsaProvider;
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

    public String save() {
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
        redisTemplate.opsForValue().set(rsa.getPublicKey(), rsa.getPrivateKey());
        redisTemplate.expire(rsa.getPublicKey(), Duration.between(Instant.now(), expiredAt));

        return rsa.getPublicKey();
    }

    public String decodeDeviceId(String encryptedData, String publicKey) {
        String privateKey = redisTemplate.opsForValue().get(publicKey);
        return rsaProvider.decode(encryptedData, privateKey);
    }
}
