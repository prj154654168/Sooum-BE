package com.sooum.api.rsa.service;

import com.sooum.data.rsa.entity.Rsa;
import com.sooum.data.rsa.service.RsaService;
import com.sooum.global.rsa.RsaProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import static com.sooum.api.member.dto.AuthDTO.Key;

@Service
@RequiredArgsConstructor
public class RsaUseCase {
    private final RsaService rsaService;
    private final RsaProvider rsaProvider;
    private final StringRedisTemplate stringRedisTemplate;

    public Key findPublicKey() {
        String newPublicKey;

        try {
            newPublicKey = stringRedisTemplate.opsForValue().get("rsa:public-key:new");
            if (newPublicKey == null) {
                Rsa rsa = rsaService.findLatestRsaKey();
                newPublicKey = rsa.getPublicKey();
            }
        } catch (Exception e) {
            Rsa rsa = rsaService.findLatestRsaKey();
            newPublicKey = rsa.getPublicKey();
        }

        return new Key(newPublicKey);
    }



    public String decodeDeviceId(String encryptedData) {
        String newPrivateKey;
        String oldPrivateKey;

        try {
            newPrivateKey = stringRedisTemplate.opsForValue().get("rsa:private-key:new");
            oldPrivateKey = stringRedisTemplate.opsForValue().get("rsa:private-key:old");
            if (newPrivateKey == null) {
                Rsa rsa = rsaService.findLatestRsaKey();
                newPrivateKey = rsa.getPrivateKey();
                oldPrivateKey = rsa.getPrivateKey();
            }
        } catch (Exception e) {
            Rsa rsa = rsaService.findLatestRsaKey();
            newPrivateKey = rsa.getPrivateKey();
            oldPrivateKey = rsa.getPrivateKey();
        }

        return rsaProvider.decode(encryptedData, oldPrivateKey, newPrivateKey);
    }
}
