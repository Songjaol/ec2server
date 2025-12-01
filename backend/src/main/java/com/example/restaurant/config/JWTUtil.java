package com.example.restaurant.config;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

import javax.crypto.SecretKey;
import java.util.Date;

public class JWTUtil {

    // (이전 대화에서 사용한 유효한 테스트 키)
    // 이 키는 488비트입니다.
    private static final String BASE64_SECRET = "VGhpcy1pcy1hLXN1cGVyLWxvbmctYW5kLXNlY3VyZS1zZWNyZXQtS2V5LWZvci1teS1KU1QtcHJvamVjdA==";

    private static final SecretKey SECRET_KEY = Keys.hmacShaKeyFor(
            Decoders.BASE64URL.decode(BASE64_SECRET)
    );
    
    private static final long EXPIRATION_MILLIS = 60 * 60 * 1000; // 1시간

    /**
     * 사용자 이메일과 ID를 받아 JWT를 생성합니다.
     */
    public static String createJwt(String userEmail, Long userId) {
        
        if (userId == null) {
            throw new IllegalArgumentException("User ID cannot be null when creating JWT");
        }
        if (userEmail == null) {
            throw new IllegalArgumentException("User Email cannot be null when creating JWT");
        }

        long nowMillis = System.currentTimeMillis();
        Date now = new Date(nowMillis);
        Date expirationDate = new Date(nowMillis + EXPIRATION_MILLIS);

        return Jwts.builder()
                .subject(userEmail) 
                .claim("userId", userId) 
                .issuer("MyWebApp") 
                .issuedAt(now) 
                .expiration(expirationDate) 
                
                // --- (수정) 500 오류 해결 ---
                // HS512 (512비트 필요) 대신 HS256 (256비트 필요)을 사용합니다.
                // 우리의 488비트 키는 HS256에 사용하기에 충분히 강력합니다.
                .signWith(SECRET_KEY, Jwts.SIG.HS256) 
                // ---
                
                .compact();
    }

    /**
     * "Bearer " 토큰 헤더를 검증하고 Claims를 추출합니다.
     */
    private static Claims validateTokenAndGetClaims(String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new IllegalArgumentException("Invalid or missing Authorization header");
        }

        String token = authHeader.substring(7); // "Bearer " 부분 제거

        try {
            // SECRET_KEY를 사용하여 토큰 검증
            return Jwts.parser()
                    .verifyWith(SECRET_KEY)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
        } catch (Exception e) {
            // ExpiredJwtException, SignatureException, MalformedJwtException 등
            // 모든 JWT 관련 예외를 포괄하여 처리
            throw new RuntimeException("Invalid token", e);
        }
    }

    /**
     * Authorization 헤더에서 UserId (Long)를 추출합니다.
     */
    public static Long getUserIdFromToken(String authHeader) {
        String token = authHeader.startsWith("Bearer ")
                ? authHeader.substring(7)
                : authHeader;

        try {
            Claims claims = Jwts.parser()
                    .verifyWith(SECRET_KEY)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();

            return claims.get("userId", Number.class).longValue();

        } catch (Exception e) {
            throw new RuntimeException("Invalid token", e);
        }
    }


}