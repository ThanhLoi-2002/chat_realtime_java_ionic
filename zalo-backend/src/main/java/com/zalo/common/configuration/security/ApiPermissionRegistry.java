package com.zalo.common.configuration.security;

import com.zalo.common.configuration.anotation.permission.RequiresPermission;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

@Component
public class ApiPermissionRegistry implements ApplicationListener<ContextRefreshedEvent> {

    // Bản đồ lưu trữ tự động: Key = (Method + Pattern), Value = Mã quyền
    private final Map<ApiConfig, String> registry = new LinkedHashMap<>();

    public Map<ApiConfig, String> getRegistry() {
        return registry;
    }

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        ApplicationContext context = event.getApplicationContext();

        // Lấy bộ ánh xạ URL của Spring Web
        RequestMappingHandlerMapping mapping = context.getBean(RequestMappingHandlerMapping.class);
        Map<RequestMappingInfo, HandlerMethod> handlerMethods = mapping.getHandlerMethods();

        // Quét qua tất cả các endpoint API trong hệ thống
        for (Map.Entry<RequestMappingInfo, HandlerMethod> entry : handlerMethods.entrySet()) {
            HandlerMethod handlerMethod = entry.getValue();

            // Nếu hàm trong Controller có gắn @RequiresPermission
            if (handlerMethod.hasMethodAnnotation(RequiresPermission.class)) {
                RequiresPermission annotation = handlerMethod.getMethodAnnotation(RequiresPermission.class);
                String requiredPermission = annotation.value(); // Lấy ra chuỗi quyền, VD: "user:delete"

                RequestMappingInfo mappingInfo = entry.getKey();

                // Trích xuất các URL patterns gắn với API đó
                Set<String> patterns = mappingInfo.getDirectPaths();
                if (patterns.isEmpty() && mappingInfo.getPathPatternsCondition() != null) {
                    patterns = mappingInfo.getPathPatternsCondition().getPatternValues();
                }

                // Trích xuất các HTTP Method (GET, POST, PUT, DELETE...)
                Set<RequestMethod> methods = mappingInfo.getMethodsCondition().getMethods();

                // Nạp tự động vào bản đồ bảo mật
                for (String pattern : patterns) {
                    for (RequestMethod method : methods) {
                        HttpMethod httpMethod = HttpMethod.valueOf(method.name());
                        registry.put(new ApiConfig(httpMethod, pattern), requiredPermission);
                    }
                }
            }
        }
    }

    public record ApiConfig(HttpMethod method, String pattern) {}
}
