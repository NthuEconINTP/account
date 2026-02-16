package com.moneyflow.account.util;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;

@Component
public class JwtUtil {

	private static final String SECRET_KEY = "YourSuperSecretKeyChangeMe!MustBeLongEnough123"; // key 要夠長 //以後要記得改，需要放到config檔案統一去改
    private static final long EXPIRATION_MS = 24 * 60 * 60 * 1000; // 24小時

    private Key key = Keys.hmacShaKeyFor(SECRET_KEY.getBytes());

    // 生成 JWT
    public String generateToken(Long userId, String username, String role) {
        return Jwts.builder()
                .setSubject(username)
                .claim("id", userId)
                .claim("role", role)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_MS))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    // 解析 JWT
    public Claims parseToken(String token) throws JwtException {
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    // 驗證是否有效
    public boolean validateToken(String token) {
        try {
            parseToken(token);
            return true;
        } catch (JwtException e) {
            return false;
        }
    }

    public String getUsername(String token) {
        return parseToken(token).getSubject();
    }

    public String getRole(String token) {
        return (String) parseToken(token).get("role");
    }

    public Long getUserId(String token) {
        return ((Number) parseToken(token).get("id")).longValue();
    }
}