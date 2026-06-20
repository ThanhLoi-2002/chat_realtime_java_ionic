package com.zalo.common.service;

import com.zalo.modules.admin.role.service.RoleService;
import com.zalo.modules.app.user.dto.response.UserPayload;
import com.zalo.modules.app.user.entities.User;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.security.Key;
import java.util.Date;
import java.util.List;

@Service
@RequiredArgsConstructor
public class JwtService {
    public int tokenTime = 86400000; // millisecond 86400000
    public int refreshTokenTime = tokenTime * 7;
    private final RoleService roleService;

    private Key getKey() {
        String SECRET = "mysecretkeymysecretkeymysecretkeymysecretkey";
        return Keys.hmacShaKeyFor(SECRET.getBytes());
    }

    public String generateToken(User user, int time) {

        List<String> roles = roleService.getUserRoles(user.getId());
        List<String> permissions = roleService.getUserPermissions(user.getId());
        UserPayload userPayload = new UserPayload(user);
        userPayload.setRoles(roles);
        userPayload.setPermissions(permissions);

        return Jwts.builder()
                .setSubject(user.getId().toString())
//                .claim("id", user.getId())
//                .claim("phone", user.getPhone())
                .claim("payload", userPayload)
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
