package com.example.barber_server.auth;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;

@Service
public class JwtService {

    private static final long EXPIRATION_TIME = 86400000; // 1 ngày

    // 1. Khai báo một chuỗi khóa bí mật CỐ ĐỊNH (Phải dài ít nhất 32 ký tự)
    private static final String SECRET_STRING = "DayLaKhoaBiMatRatDaiVaKhoDoanCuaToi1234567890";

    // 2. Chuyển chuỗi cố định đó thành SecretKey
    private final SecretKey key = Keys.hmacShaKeyFor(SECRET_STRING.getBytes());

    public String generateToken(String username) {
        return Jwts.builder()
                .subject(username)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                .signWith(key)
                .compact();
    }

    public String extractUsername(String token) {
        return Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .getSubject();
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parser()
                    .verifyWith(key)
                    .build()
                    .parseSignedClaims(token);
            return true;
        } catch (Exception e) {
            System.out.println("Lỗi validate token: " + e.getMessage()); // In ra lỗi nếu có
            return false;
        }
    }
}