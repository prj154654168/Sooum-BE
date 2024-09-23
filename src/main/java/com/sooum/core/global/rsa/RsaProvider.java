package com.sooum.core.global.rsa;

import com.sooum.core.domain.rsa.exception.RsaDecodeException;
import com.sooum.core.domain.rsa.exception.RsaGenerateException;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.crypto.Cipher;
import java.security.*;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.Base64;
import java.util.HashMap;

@Slf4j
@Getter
@Component
@RequiredArgsConstructor
public class RsaProvider {

    public final int KEY_SIZE = 1024;

    public HashMap<String, String> generateKeyPair() {
        HashMap<String, String> stringKeypair = new HashMap<>();

        try {
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

        } catch (Exception e) {
            throw new RsaGenerateException();
        }

        return stringKeypair;
    }

    public String decode(String encryptedData, String stringPrivateKey) {
        String decryptedData = null;

        try {
            // Convert String to PrivateKey
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            byte[] bytePrivateKey = Base64.getDecoder().decode(stringPrivateKey.getBytes());
            PKCS8EncodedKeySpec privateKeySpec = new PKCS8EncodedKeySpec(bytePrivateKey);
            PrivateKey privateKey = keyFactory.generatePrivate(privateKeySpec);

            // Set decrypt mode
            Cipher cipher = Cipher.getInstance("RSA");
            cipher.init(Cipher.DECRYPT_MODE, privateKey);

            byte[] byteEncryptedData = Base64.getDecoder().decode(encryptedData.getBytes());
            byte[] byteDecryptedData = cipher.doFinal(byteEncryptedData);
            decryptedData = new String(byteDecryptedData);
        } catch (Exception e) {
            throw new RsaDecodeException();
        }

        return decryptedData;
    }
}
