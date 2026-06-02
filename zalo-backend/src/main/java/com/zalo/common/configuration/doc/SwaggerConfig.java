package com.zalo.common.configuration.doc;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {
    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Chat App Sticker API - Documentation")
                        .version("1.0.0")
                        .description("Tài liệu hệ thống API tích hợp Sticker cho ứng dụng chat Vue + Capacitor")
                        .contact(new Contact()
                                .name("Developer Support")
                                .email("your-email@example.com")));
    }
}
