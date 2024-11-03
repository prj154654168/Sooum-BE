package com.sooum.api.rsa.service;

import com.sooum.api.member.dto.AuthDTO;
import com.sooum.data.rsa.entity.Rsa;
import com.sooum.data.rsa.service.RsaService;
import com.sooum.global.rsa.RsaProvider;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.HashMap;

@Service
@RequiredArgsConstructor
public class RsaUseCase {
    private final RsaService rsaService;
    private final RsaProvider rsaProvider;
    private final RedisTemplate<String, String> redisTemplate;

    public AuthDTO.Key findPublicKey() {
        String publicKey;

        try {
            publicKey = redisTemplate.opsForValue().get("public");
        } catch (Exception e) {
            Rsa rsa = rsaService.findByExpiredAtIsAfter();
            publicKey = rsa.getPublicKey();
        }

        return new AuthDTO.Key(publicKey);
    }



    public String decodeDeviceId(String encryptedData) {
        String privateKey;

        try {
            privateKey = redisTemplate.opsForValue().get("private");
        } catch (Exception e) {
            Rsa rsa = rsaService.findByExpiredAtIsAfter();
            privateKey = rsa.getPrivateKey();
        }

        return rsaProvider.decode(encryptedData, privateKey);
    }

    public AuthDTO.Key save() {
        HashMap<String, String> keyPair = rsaProvider.generateKeyPair();

        // Save to RDB
        LocalDateTime expiredAt = LocalDateTime.now().plusMonths(6);
        Rsa rsa = Rsa.builder()
                .publicKey(keyPair.get("publicKey"))
                .privateKey(keyPair.get("privateKey"))
                .expiredAt(expiredAt)
                .build();
        rsaService.save(rsa);

        // Save to Redis
        redisTemplate.opsForValue().set("public", rsa.getPublicKey(), Duration.between(LocalDateTime.now(), expiredAt));
        redisTemplate.opsForValue().set("private", rsa.getPrivateKey(), Duration.between(LocalDateTime.now(), expiredAt));

        return new AuthDTO.Key(rsa.getPublicKey());
    }
}
