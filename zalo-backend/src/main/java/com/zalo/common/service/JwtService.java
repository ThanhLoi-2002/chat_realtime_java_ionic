package com.zalo.common.service;

import com.zalo.modules.user.entities.User;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.security.Key;
import java.util.Date;

@Service
public class JwtService {
    public int tokenTime = 86400000; // millisecond 86400000
    public int refreshTokenTime = tokenTime * 7;

    private Key getKey() {
        String SECRET = "mysecretkeymysecretkeymysecretkeymysecretkey";
        return Keys.hmacShaKeyFor(SECRET.getBytes());
    }

    public String generateToken(User user, int time) {

        return Jwts.builder()
                .setSubject(user.getId().toString())
                .claim("id", user.getId())
                .claim("phone", user.getPhone())
                .setIssuedAt(new Date())
                .setExpiration(
                        new Date(System.currentTimeMillis() + time)
                )
                .signWith(getKey())
                .compact();
    }

    public Claims extractAllClaims(String token) {
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(getKey())
                    .build()
                    .parseClaimsJws(token)
                    .getBody();

        } catch (ExpiredJwtException e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "expiredToken");

        } catch (JwtException e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "invalidToken");
        }
    }
}
