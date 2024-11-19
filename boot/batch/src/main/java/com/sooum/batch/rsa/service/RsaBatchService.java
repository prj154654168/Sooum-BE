package com.sooum.batch.rsa.service;

import com.sooum.data.rsa.entity.Rsa;
import com.sooum.data.rsa.service.RsaService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.security.*;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.HashMap;

@Service
@RequiredArgsConstructor
public class RsaBatchService {

    private final RedisTemplate<String, Object> redisTemplate;
    private final RsaService rsaService;
    public final int KEY_SIZE = 1024;

    public void save() throws NoSuchAlgorithmException {
        HashMap<String, String> keyPair = generateKeyPair();

        // Save to RDB
        LocalDateTime expiredAt = LocalDateTime.now().plusMonths(6);
        Rsa rsa = rsaService.save(keyPair, expiredAt);

        // Save to Redis
        redisTemplate.opsForValue().set("public", rsa.getPublicKey(), Duration.between(LocalDateTime.now(), expiredAt));
        redisTemplate.opsForValue().set("private", rsa.getPrivateKey(), Duration.between(LocalDateTime.now(), expiredAt));
    }

    private HashMap<String, String> generateKeyPair() throws NoSuchAlgorithmException {
        HashMap<String, String> stringKeypair = new HashMap<>();

        // Key Generator
        SecureRandom secureRandom = new SecureRandom();
        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
        keyPairGenerator.initialize(KEY_SIZE, secureRandom);

        // Generate Key
        KeyPair keyPair = keyPairGenerator.genKeyPair();
        PublicKey publicKey = keyPair.getPublic();
        PrivateKey privateKey = keyPair.getPrivate();

        // Convert Key to String
        String stringPublicKey = Base64.getEncoder().encodeToString(publicKey.getEncoded());
        String stringPrivateKey = Base64.getEncoder().encodeToString(privateKey.getEncoded());

        // Set return value
        stringKeypair.put("publicKey", stringPublicKey);
        stringKeypair.put("privateKey", stringPrivateKey);

        return stringKeypair;
    }
}
