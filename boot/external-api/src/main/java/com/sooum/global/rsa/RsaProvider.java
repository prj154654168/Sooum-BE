package com.sooum.global.rsa;

import com.sooum.api.rsa.exception.RsaDecodeException;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.Base64;

@Slf4j
@Getter
@Component
@RequiredArgsConstructor
public class RsaProvider {

    public String decode(String encryptedData, String oldPrivateKey, String newPrivateKey) {
        String decryptedData = null;

        try {
            decryptedData = decryptData(newPrivateKey, encryptedData);
        } catch (Exception e) {
            try {
                decryptedData = decryptData(oldPrivateKey, encryptedData);
            }catch (Exception e1) {
                throw new RsaDecodeException();
            }
        }

        return decryptedData;
    }

    @NotNull
    private static String decryptData(String stringPrivateKey, String encryptedData) throws NoSuchAlgorithmException, InvalidKeySpecException, NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        byte[] bytePrivateKey = Base64.getDecoder().decode(stringPrivateKey.getBytes());
        PKCS8EncodedKeySpec privateKeySpec = new PKCS8EncodedKeySpec(bytePrivateKey);
        PrivateKey privateKey = keyFactory.generatePrivate(privateKeySpec);

        // Set decrypt mode
        Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.DECRYPT_MODE, privateKey);

        byte[] byteEncryptedData = Base64.getDecoder().decode(encryptedData.getBytes());
        byte[] byteDecryptedData = cipher.doFinal(byteEncryptedData);
        return new String(byteDecryptedData);
    }
}
