package com.bananabreach.utils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

/**
 * Crypto/hashing helpers for BananaBreach.
 *
 * Every method in this class is intentionally weak or misused as part of the
 * training scenario — see docs/VULNERABILITIES.md (Cryptography) for the full
 * write-up of each issue and how it should be fixed in a real application.
 */
public class SecurityUtils {

    // Intentional: hardcoded symmetric key shipped in the APK.
    private static final String HARDCODED_KEY = "banana_secret_key_12345";

    // Intentional: MD5 is cryptographically broken and unsuitable for password hashing.
    public static String hashMD5(String input) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] digest = md.digest(input.getBytes());
            StringBuilder hexString = new StringBuilder();

            for (byte b : digest) {
                String hex = Integer.toHexString(0xFF & b);
                if (hex.length() == 1) {
                    hexString.append('0');
                }
                hexString.append(hex);
            }

            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return null;
        }
    }

    // Intentional: trivially reversible single-byte XOR "encryption".
    public static String xorEncrypt(String input) {
        char[] chars = input.toCharArray();
        for (int i = 0; i < chars.length; i++) {
            chars[i] = (char) (chars[i] ^ 0x5A);
        }
        return new String(chars);
    }

    public static String xorDecrypt(String input) {
        return xorEncrypt(input); // XOR is symmetric
    }

    // Intentional: AES in ECB mode with a hardcoded key — no IV, no key rotation.
    public static String encryptAES(String data) {
        try {
            SecretKeySpec keySpec = new SecretKeySpec(
                    HARDCODED_KEY.getBytes(),
                    "AES"
            );
            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
            cipher.init(Cipher.ENCRYPT_MODE, keySpec);
            byte[] encrypted = cipher.doFinal(data.getBytes());
            return android.util.Base64.encodeToString(encrypted, android.util.Base64.DEFAULT);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    // Intentional: predictable token derived from timestamp + small random range.
    public static String generateToken(String userId) {
        long timestamp = System.currentTimeMillis();
        int random = (int) (Math.random() * 10000);
        return userId + "_" + timestamp + "_" + random;
    }
}
