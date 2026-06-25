package com.zalo.common.configuration.anotation.permission;

import com.zalo.modules.admin.system.user.dto.response.UserPayload;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import java.util.List;
import java.util.Objects;

@Aspect
@Component
public class PermissionAspect {
    // Kích hoạt TRƯỚC KHI hàm API chạy. Nó tự động ép biến "requiresPermission" vào để đọc value
    @Before("@annotation(requiresPermission)")
    public void checkPermission(RequiresPermission requiresPermission) {
        // Lấy đối tượng UserPayload từ Security Context
        Object principal = Objects.requireNonNull(SecurityContextHolder.getContext().getAuthentication()).getPrincipal();

        if (!(principal instanceof UserPayload currentUser)) {
            throw new AccessDeniedException("User không hợp lệ!");
        }

        // Tiến hành check quyền từ currentUser tương tự như trên...
        String requiredPermission = requiresPermission.value();
        if (currentUser.getPermissions() == null || !currentUser.getPermissions().contains(requiredPermission)) {
            throw new AccessDeniedException("accessDenied");
        }
    }
}
