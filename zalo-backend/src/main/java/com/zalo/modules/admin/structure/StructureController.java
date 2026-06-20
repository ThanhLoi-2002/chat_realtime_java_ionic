package com.zalo.modules.admin.structure;

import com.zalo.common.configuration.anotation.ResponseMessage;
import com.zalo.common.configuration.anotation.currentUser.CurrentUser;
import com.zalo.common.configuration.anotation.permission.RequiresPermission;
import com.zalo.common.configuration.json.G;
import com.zalo.common.util.PermissionConstant;
import com.zalo.modules.admin.structure.dto.request.StructureSortRequest;
import com.zalo.modules.admin.structure.dto.response.StructureResponse;
import com.zalo.modules.admin.structure.entity.AppType;
import com.zalo.modules.admin.structure.entity.Structure;
import com.zalo.modules.admin.structure.service.StructureService;
import com.zalo.modules.app.user.dto.response.UserPayload;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/structure")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class StructureController {
    StructureService structureService;

    @GetMapping("/tree")
    @RequiresPermission(PermissionConstant.Admin.Structure.VIEW_TREE)
    public Map<AppType, List<StructureResponse>> getTree() {
        return structureService.getMenuTree();
    }

    @GetMapping("/trash")
    public List<StructureResponse> getTrash() {
        return structureService.getTrashMenu().stream().map(StructureResponse::new).toList();
    }

    @GetMapping("/menu-by-user")
    public List<StructureResponse> getMenuByUser(@CurrentUser UserPayload user, @RequestParam AppType appType) {
        return structureService.getMenuByUser(user.getId(), appType, user.getPermissions(), user.getRoles());
    }

    @PostMapping
    public StructureResponse createOrUpdate(@RequestBody Structure structure) {
        return new StructureResponse(structureService.saveOrUpdate(structure));
    }

    @PutMapping("/sort")
    @ResponseMessage("success")
    public void updateSort(@RequestBody List<StructureSortRequest> updates) {
        structureService.updateMenuOrder(updates);
    }
}
