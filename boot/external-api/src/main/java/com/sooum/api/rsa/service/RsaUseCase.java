package com.sooum.api.rsa.service;

import com.sooum.data.rsa.entity.Rsa;
import com.sooum.data.rsa.service.RsaService;
import com.sooum.global.rsa.RsaProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import static com.sooum.api.member.dto.AuthDTO.Key;

@Service
@RequiredArgsConstructor
public class RsaUseCase {
    private final RsaService rsaService;
    private final RsaProvider rsaProvider;
    private final RedisTemplate<String, String> redisTemplate;

    public Key findPublicKey() {
        String publicKey;

        try {
            publicKey = redisTemplate.opsForValue().get("public");
            if (publicKey == null) {
                Rsa rsa = rsaService.findByExpiredAtIsAfter();
                publicKey = rsa.getPublicKey();
            }
        } catch (Exception e) {
            Rsa rsa = rsaService.findByExpiredAtIsAfter();
            publicKey = rsa.getPublicKey();
        }

        return new Key(publicKey);
    }



    public String decodeDeviceId(String encryptedData) {
        String privateKey;

        try {
            privateKey = redisTemplate.opsForValue().get("private");
            if (privateKey == null) {
                Rsa rsa = rsaService.findByExpiredAtIsAfter();
                privateKey = rsa.getPrivateKey();
            }
        } catch (Exception e) {
            Rsa rsa = rsaService.findByExpiredAtIsAfter();
            privateKey = rsa.getPrivateKey();
        }

        return rsaProvider.decode(encryptedData, privateKey);
    }
}
