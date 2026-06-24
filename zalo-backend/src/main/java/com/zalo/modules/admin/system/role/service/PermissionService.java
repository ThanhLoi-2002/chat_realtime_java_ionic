package com.zalo.modules.admin.system.role.service;

import com.zalo.common.util.PermissionConstant;
import com.zalo.modules.admin.system.role.dto.response.ModulePermissionResponse;
import com.zalo.modules.admin.system.role.dto.response.PermissionTreeResponse;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;

import java.lang.reflect.Modifier;
import java.util.*;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class PermissionService {

    public List<PermissionTreeResponse> getAllPermissions() {

        List<PermissionTreeResponse> result = new ArrayList<>();

        for (Class<?> appClass : PermissionConstant.class.getDeclaredClasses()) {

            List<ModulePermissionResponse> modules = new ArrayList<>();

            for (Class<?> moduleClass : appClass.getDeclaredClasses()) {

                List<String> permissions = Arrays.stream(moduleClass.getDeclaredFields())
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

                modules.add(new ModulePermissionResponse(
                        moduleClass.getSimpleName().toLowerCase(),
                        permissions
                ));
            }

            result.add(new PermissionTreeResponse(
                    appClass.getSimpleName(),
                    modules
            ));
        }

        return result;
    }
}
