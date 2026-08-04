package com.zalo.modules.admin.oa.category;

import com.zalo.common.configuration.anotation.ResponseMessage;
import com.zalo.common.configuration.anotation.currentUser.CurrentUser;
import com.zalo.common.configuration.anotation.permission.RequiresPermission;
import com.zalo.common.util.PermissionConstant;
import com.zalo.modules.admin.oa.category.dto.request.CategoryRequest;
import com.zalo.modules.admin.oa.category.dto.response.CategoryResponse;
import com.zalo.modules.admin.oa.category.service.CategoryService;
import com.zalo.modules.admin.system.user.dto.response.UserPayload;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin/oa/category")
@RequiredArgsConstructor
public class CategoryController {
    private final CategoryService categoryService;

    @GetMapping
    @RequiresPermission(PermissionConstant.ADMIN.OA_CATEGORY.READ)
    public List<CategoryResponse> getAll() {
        return categoryService.getAll().stream().map(CategoryResponse::new).toList();
    }

    @PostMapping
    @ResponseMessage("success")
    @RequiresPermission(PermissionConstant.ADMIN.OA_CATEGORY.CREATE)
    public CategoryResponse create(@RequestBody CategoryRequest req, @CurrentUser UserPayload user) {
        return new CategoryResponse(categoryService.create(req, user.getId()));
    }

    @PutMapping("/{id}")
    @ResponseMessage("success")
    @RequiresPermission(PermissionConstant.ADMIN.OA_CATEGORY.UPDATE)
    public CategoryResponse update(
            @PathVariable Long id,
            @RequestBody CategoryRequest req, @CurrentUser UserPayload user
    ) {
        return new CategoryResponse(categoryService.update(id, req, user.getId()));
    }

    @DeleteMapping("/{id}")
    @ResponseMessage("success")
    @RequiresPermission(PermissionConstant.ADMIN.OA_CATEGORY.DELETE)
    public void delete(@PathVariable Long id, @CurrentUser UserPayload user) {
        categoryService.delete(id, user.getId());
    }
}
