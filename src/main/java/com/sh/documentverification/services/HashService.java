package com.sh.documentverification.services;


import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

@Service
public class HashService {

    public String calculateSHA256Hash(InputStream path) throws IOException, NoSuchAlgorithmException {
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        byte[] buffer = new byte[1024];
        int bytesRead;
        while ((bytesRead = path.read(buffer)) != -1) {
            digest.update(buffer, 0, bytesRead);
        }
        byte[] hashBytes = digest.digest();
        // 해시 값을 16진수 문자열로 변환
        StringBuilder hashStringBuilder = new StringBuilder();
        for (byte hashByte : hashBytes) {
            hashStringBuilder.append(String.format("%02x", hashByte));
        }
        return hashStringBuilder.toString();
    }

}
