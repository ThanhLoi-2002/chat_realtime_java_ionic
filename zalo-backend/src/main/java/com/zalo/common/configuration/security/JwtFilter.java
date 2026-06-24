package com.zalo.common.configuration.security;

import com.cloudinary.api.exceptions.NotFound;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.zalo.modules.admin.system.user.dto.response.UserPayload;
import com.zalo.common.service.JwtService;
import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class JwtFilter extends OncePerRequestFilter {

    private final JwtService jwtService;

    public UserPayload getOneByToken(String token) throws NotFound {
        Claims claims = jwtService.extractAllClaims(token);

        ObjectMapper mapper = new ObjectMapper();

        return mapper.convertValue(
                claims.get("payload"),
                UserPayload.class
        );
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {

        String authHeader = request.getHeader("Authorization");

        if (authHeader != null && !authHeader.isBlank()) {

            try {
                UserPayload user = getOneByToken(authHeader);

                request.setAttribute("currentUser", user);

            } catch (Exception ignored) {
            }
        }

        filterChain.doFilter(request, response);
    }
}
