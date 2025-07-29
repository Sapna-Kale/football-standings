package com.ps.footballstandings.util;

import java.security.Key;
import java.util.Date;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

@Component
public class JwtUtil {

  private final String secret = "yourSecretKeyyourSecretKeyyourSecretKey12"; // at least 256-bit key for HS256
  private final Key key = Keys.hmacShaKeyFor(secret.getBytes()); // convert to Key object

  private final long expiration = 86400000; // 24 hours

  // Generate JWT token
  public String generateToken(String username) {
    return Jwts.builder()
            .setSubject(username)
            .setIssuedAt(new Date())
            .setExpiration(new Date(System.currentTimeMillis() + expiration))
            .signWith(key, SignatureAlgorithm.HS256)  // use Key + algorithm
            .compact();
  }

  // Extract username from token
  public String extractUsername(String token) {
    return Jwts.parserBuilder()
            .setSigningKey(key) // use Key object here
            .build()
            .parseClaimsJws(token)
            .getBody()
            .getSubject();
  }

  // Validate token
  public boolean validateToken(String token) {
    try {
      Jwts.parserBuilder()
              .setSigningKey(key)
              .build()
              .parseClaimsJws(token);
      return true;
    } catch (JwtException | IllegalArgumentException e) {
      return false;
    }
  }
}

