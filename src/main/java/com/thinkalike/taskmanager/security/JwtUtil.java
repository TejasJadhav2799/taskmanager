package com.thinkalike.taskmanager.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Base64;
import java.util.Date;

@Component
public class JwtUtil {

    @Value("${jwt.secret}")
    private String secret;
    // @Value reads jwt.secret from application.properties
    // Spring injects it automatically at startup

    @Value("${jwt.expiration}")
    private Long expiration;
    // 86400000 milliseconds = 24 hours

    // converts the secret string into a cryptographic key
    // used to sign and verify tokens
    private SecretKey getSigningKey() {
        byte[] keyBytes = Base64.getDecoder().decode(secret);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    public String generateToken(String email) {
        return Jwts.builder()
                .subject(email)
        // subject = who this token belongs to (the user's email)
                .issuedAt(new Date())
        // issuedAt = when was this token created
                .expiration(new Date(System.currentTimeMillis() + expiration))
        // expiration = when does this token expire (24 hours from now)
                .signWith(getSigningKey())
        // signWith = sign with our secret key
        // this is what prevents tampering —
        // if anyone modifies the token, signature won't match
                .compact();
    }

    // EXTRACT EMAIL — reads the email (subject) from inside the token
    public String extractEmail(String token) {
        return extractClaims(token).getSubject();
    }

    // VALIDATE TOKEN — checks two things:
    // 1. does the email in the token match the user we expect?
    // 2. has the token expired?
    public boolean validateToken(String token, String email) {
        String extractedEmail = extractEmail(token);
        return extractedEmail.equals(email) && !isTokenExpired(token);
    }

    // checks if token expiry date is before right now
    private boolean isTokenExpired(String token) {
        return extractClaims(token).getExpiration().before(new Date());
    }

    // parses the token and extracts all claims (payload data)
    private Claims extractClaims(String token) {
        return Jwts.parser()
                .verifyWith(getSigningKey())
                // verifyWith checks the signature — if token was tampered
                // this throws an exception automatically
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}
