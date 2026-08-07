package com.zalo.common.configuration.anotation.currentUser;

import com.zalo.common.configuration.anotation.Public.PublicEndpointInterceptor;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import java.util.List;

@Configuration
@RequiredArgsConstructor
public class WebConfig implements WebMvcConfigurer {

    private final CurrentUserArgumentResolver currentUserArgumentResolver;
    private final PublicEndpointInterceptor publicEndpointInterceptor;

    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
        resolvers.add((HandlerMethodArgumentResolver) currentUserArgumentResolver);
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // Cấu hình: ánh xạ url /uploads/** vào thư mục vật lý uploads/ trên ổ cứng server
        registry.addResourceHandler("/uploads/**")
                .addResourceLocations("file:uploads/");
    }

//    @Override
//    public void addInterceptors(InterceptorRegistry registry) {
//        // Áp dụng Interceptor cho tất cả các routes
//        registry.addInterceptor(publicEndpointInterceptor)
//                .addPathPatterns("/**");
//    }
}
