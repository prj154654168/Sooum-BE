package com.sooum.batch.rsa.service;

import com.sooum.data.rsa.entity.Rsa;
import com.sooum.data.rsa.service.RsaService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.*;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.HashMap;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class RsaSchedulerService {
    private final StringRedisTemplate stringRedisTemplate;
    private final RsaService rsaService;
    public final int KEY_SIZE = 1024;

    @Transactional
    public void save(LocalDateTime currentDateTime) throws NoSuchAlgorithmException {
        HashMap<String, String> keyPair = generateKeyPair();

        // Save to RDB
        LocalDateTime newKeyExpiredAt = currentDateTime.plusDays(1).plusMinutes(10);
        LocalDateTime oldKeyExpiredAt = currentDateTime.plusMinutes(10);
        Rsa rsa = rsaService.save(keyPair, newKeyExpiredAt);
        rsaService.deleteExpiredRsaKey();

        // Save to Redis
        String oldPublicKey = stringRedisTemplate.opsForValue()
                .get("rsa:public-key:new");
        String oldPrivateKey = stringRedisTemplate.opsForValue()
                .get("rsa:private-key:new");

        stringRedisTemplate.opsForValue()
                .set("rsa:public-key:new",
                        rsa.getPublicKey(),
                        Duration.between(currentDateTime, newKeyExpiredAt)
                );

        stringRedisTemplate.opsForValue()
                .set("rsa:private-key:new",
                        rsa.getPrivateKey(),
                        Duration.between(currentDateTime, newKeyExpiredAt)
                );

        if (!Objects.isNull(oldPrivateKey) || !Objects.isNull(oldPublicKey)) {
            stringRedisTemplate.opsForValue()
                    .set("rsa:public-key:old",
                            Objects.requireNonNull(oldPublicKey),
                            Duration.between(currentDateTime, oldKeyExpiredAt)
                    );

            stringRedisTemplate.opsForValue()
                    .set("rsa:private-key:old",
                            Objects.requireNonNull(oldPrivateKey),
                            Duration.between(currentDateTime, oldKeyExpiredAt)
                    );
        }

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
