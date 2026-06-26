package com.zalo.modules.admin.system.structure;

import com.zalo.common.configuration.anotation.ResponseMessage;
import com.zalo.common.configuration.anotation.currentUser.CurrentUser;
import com.zalo.common.configuration.anotation.permission.RequiresPermission;
import com.zalo.common.util.PermissionConstant;
import com.zalo.modules.admin.system.structure.dto.request.StructureSortRequest;
import com.zalo.modules.admin.system.structure.dto.response.StructureResponse;
import com.zalo.modules.admin.system.structure.entity.AppType;
import com.zalo.modules.admin.system.structure.entity.Structure;
import com.zalo.modules.admin.system.structure.service.StructureService;
import com.zalo.modules.admin.system.user.dto.response.UserPayload;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/admin/system/structure")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class StructureController {
    StructureService structureService;

    @GetMapping("/tree")
    @RequiresPermission(PermissionConstant.ADMIN.Structure.READ)
    public Map<AppType, List<StructureResponse>> getTree() {
        return structureService.getMenuTree();
    }

    @GetMapping("/trash")
    @RequiresPermission(PermissionConstant.ADMIN.Structure.READ)
    public List<StructureResponse> getTrash() {
        return structureService.getTrashMenu().stream().map(StructureResponse::new).toList();
    }

    @GetMapping("/menu-by-user")
    public List<StructureResponse> getMenuByUser(@CurrentUser UserPayload user, @RequestParam AppType appType) {
        return structureService.getMenuByUser(user.getId(), appType, user.getPermissions(), user.getRoles());
    }

    @GetMapping("/module-by-app-type")
    public List<StructureResponse> getModuleByAppType(@RequestParam AppType appType) {
        return structureService.getModuleByAppType(appType).stream().map(StructureResponse::new).toList();
    }

    @GetMapping("/controller-by-module")
    public List<StructureResponse> getControllersByModule(@RequestParam Long moduleId) {
        return structureService.getControllersByModule(moduleId).stream().map(StructureResponse::new).toList();
    }

    @PostMapping
    @RequiresPermission(PermissionConstant.ADMIN.Structure.CREATE_UPDATE)
    public StructureResponse createOrUpdate(@RequestBody Structure structure) {
        return new StructureResponse(structureService.saveOrUpdate(structure));
    }

    @PutMapping("/sort")
    @ResponseMessage("success")
    @RequiresPermission(PermissionConstant.ADMIN.Structure.CREATE_UPDATE)
    public void updateSort(@RequestBody List<StructureSortRequest> updates) {
        structureService.updateMenuOrder(updates);
    }
}
