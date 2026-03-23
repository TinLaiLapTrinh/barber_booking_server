package com.example.barber_server.utils;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;

public class SignatureUtils {
    private static final String HMAC_SHA256 = "HmacSHA256";
    private static final char[] HEX_ARRAY = "0123456789abcdef".toCharArray();

    public static String computeHmacSha256(String data, String key) {
        try {
            Mac sha256_HMAC = Mac.getInstance(HMAC_SHA256);

            SecretKeySpec secret_key =
                    new SecretKeySpec(key.getBytes(StandardCharsets.UTF_8), HMAC_SHA256);

            sha256_HMAC.init(secret_key);

            byte[] array = sha256_HMAC.doFinal(data.getBytes(StandardCharsets.UTF_8));

            StringBuilder sb = new StringBuilder();
            for (byte item : array) {
                sb.append(String.format("%02x", item));
            }

            return sb.toString();

        } catch (Exception e) {
            throw new RuntimeException("Lỗi tạo chữ ký", e);
        }
    }

    public static String bytesToHex(byte[] bytes) {
        char[] hexChars = new char[bytes.length * 2];
        for (int j = 0; j < bytes.length; j++) {
            int v = bytes[j] & 0xFF;
            hexChars[j * 2] = HEX_ARRAY[v >>> 4];
            hexChars[j * 2 + 1] = HEX_ARRAY[v & 0x0F];
        }
        return new String(hexChars);
    }


    public static boolean verifySignature(String data, String secretKey, String momoSignature) {
        String mySignature = computeHmacSha256(data, secretKey);
        return MessageDigest.isEqual(
                mySignature.getBytes(StandardCharsets.UTF_8),
                momoSignature.getBytes(StandardCharsets.UTF_8)
        );
    }
}

