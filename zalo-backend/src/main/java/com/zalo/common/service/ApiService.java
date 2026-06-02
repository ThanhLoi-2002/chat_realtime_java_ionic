package com.zalo.common.service;

import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
@RequiredArgsConstructor
public class ApiService {

    private final RestTemplate restTemplate;

    public Object get(String url) {
        try {
            ResponseEntity<Object> response = restTemplate.exchange(url, HttpMethod.GET, null, Object.class);

            return response.getBody();
        } catch (Exception e) {

            throw new RuntimeException("Call API failed", e);
        }
    }
}