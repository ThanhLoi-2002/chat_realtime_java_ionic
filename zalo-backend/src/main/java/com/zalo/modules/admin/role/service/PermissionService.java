package com.zalo.modules.admin.role.service;

import com.zalo.common.util.PermissionConstant;
import com.zalo.modules.admin.role.dto.response.PermissionResponse;
import org.springframework.stereotype.Service;

import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

@Service
public class PermissionService {

    public List<PermissionResponse> getAllPermissions() {

        List<PermissionResponse> result = new ArrayList<>();

        extract(PermissionConstant.class, "", result);

        return result;
    }

    private void extract(Class<?> clazz,
                         String prefix,
                         List<PermissionResponse> result) {

        List<String> permissions = Arrays.stream(clazz.getDeclaredFields())
                .filter(f -> Modifier.isStatic(f.getModifiers()))
                .filter(f -> f.getType().equals(String.class))
                .map(f -> {
                    try {
                        return (String) f.get(null);
                    } catch (Exception e) {
                        return null;
                    }
                })
                .filter(Objects::nonNull)
                .toList();

        if (!permissions.isEmpty()) {
            result.add(new PermissionResponse(
                    prefix,
                    permissions
            ));
        }

        for (Class<?> nested : clazz.getDeclaredClasses()) {

            String group = prefix.isBlank()
                    ? nested.getSimpleName()
                    : prefix + "." + nested.getSimpleName();

            extract(nested, group, result);
        }
    }
}
