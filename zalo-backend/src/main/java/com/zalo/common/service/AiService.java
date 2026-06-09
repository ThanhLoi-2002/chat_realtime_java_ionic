package com.zalo.common.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.zalo.common.dto.ApiResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;

@Service
public class AiService {
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    @Value("${spring.ai.ai-url}")
    private String aiUrl;

    private final WebClient webClient;

    public AiService(
            @Value("${spring.ai.ai-url}") String aiUrl
    ) {
        this.webClient = WebClient.builder()
                .baseUrl(aiUrl)
                .build();
    }

    public <R> R get(
            String uri,
            ParameterizedTypeReference<ApiResponse<R>> responseType
    ) {
        return execute(
                webClient.get()
                        .uri(uri),
                responseType
        );
    }

    public <T, R> R post(
            String uri,
            T body,
            ParameterizedTypeReference<ApiResponse<R>> responseType
    ) {
        return execute(
                webClient.post()
                        .uri(uri)
                        .bodyValue(body),
                responseType
        );
    }

    public <T, R> R put(
            String uri,
            T body,
            ParameterizedTypeReference<ApiResponse<R>> responseType
    ) {
        return execute(
                webClient.put()
                        .uri(uri)
                        .bodyValue(body),
                responseType
        );
    }

    public <R> R delete(
            String uri,
            ParameterizedTypeReference<ApiResponse<R>> responseType
    ) {
        return execute(
                webClient.delete()
                        .uri(uri),
                responseType
        );
    }

    private <R> R execute(
            WebClient.RequestHeadersSpec<?> request,
            ParameterizedTypeReference<ApiResponse<R>> responseType
    ) {
        ApiResponse<R> response = request
                .retrieve()
                .onStatus(
                        HttpStatusCode::isError,
                        clientResponse -> {
                            // 1. Kiểm tra nếu chính xác là lỗi 422 từ FastAPI
                            if (clientResponse.statusCode().value() == 422) {
                                // Ép kiểu JSON lỗi về dạng Map cấu trúc động của Java
                                return clientResponse.bodyToMono(new ParameterizedTypeReference<Map<String, Object>>() {})
                                        .flatMap(errorMap -> {
                                            try {
                                                // Bóc mảng "errors" từ JSON ra
                                                List<Object> errorsList = (List<Object>) errorMap.get("errors");

                                                // Ép nguyên cái mảng errors này thành chuỗi JSON String y hệt bạn muốn
                                                String errorsJsonString = OBJECT_MAPPER.writeValueAsString(errorsList);

                                                // Gán chuỗi JSON này vào message của Exception trả về
                                                return Mono.error(new ResponseStatusException(
                                                        HttpStatus.UNPROCESSABLE_ENTITY,
                                                        errorsJsonString
                                                ));
                                            } catch (Exception e) {
                                                return Mono.error(new ResponseStatusException(
                                                        HttpStatus.UNPROCESSABLE_ENTITY,
                                                        "Validation error parsing failed"
                                                ));
                                            }
                                        });
                            }

                            // Các lỗi khác (500, 404...) giữ nguyên như cũ của bạn
                            return clientResponse.bodyToMono(new ParameterizedTypeReference<ApiResponse<Object>>() {})
                                    .flatMap(error ->
                                            Mono.error(new ResponseStatusException(
                                                    HttpStatus.BAD_GATEWAY,
                                                    error.getMessage()
                                            ))
                                    );
                        }
                )
                .bodyToMono(responseType)
                .block();

        return response.getResult();
    }
}