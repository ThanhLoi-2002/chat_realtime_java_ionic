package com.zalo.modules.admin.system.structure.service;

import com.zalo.common.configuration.json.G;
import com.zalo.modules.admin.system.structure.dto.request.StructureSortRequest;
import com.zalo.modules.admin.system.structure.dto.response.StructureResponse;
import com.zalo.modules.admin.system.structure.entity.AppType;
import com.zalo.modules.admin.system.structure.entity.Structure;
import jakarta.transaction.Transactional;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class StructureService {
    StructureRepository structureRepository;

    public Map<AppType, StructureResponse> getMenuTree() {
        List<Structure> allMenus = structureRepository.findBySttOrderBySortAsc(1);

        return allMenus.stream()
                .collect(Collectors.groupingBy(Structure::getAppType))
                .entrySet()
                .stream()
                .map(entry -> Map.entry(entry.getKey(), buildTree(entry.getValue())))
                .sorted(Comparator.comparing(entry -> entry.getValue().getSort()))
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue,
                        (a, b) -> a,
                        LinkedHashMap::new
                ));
    }

    private StructureResponse buildTree(List<Structure> menus) {

        Map<Long, StructureResponse> nodeMap = menus.stream()
                .map(StructureResponse::new)
                .collect(Collectors.toMap(
                        StructureResponse::getId,
                        node -> node
                ));

        StructureResponse root = null;

        for (StructureResponse node : nodeMap.values()) {
            if (node.getPid() == null || node.getPid() == 0) {
                root = node;
            } else {
                // parent có tham chiếu với nodeMap
                StructureResponse parent = nodeMap.get(node.getPid());
                if (parent != null) {
                    parent.getChildren().add(node);
                }
            }
        }

        nodeMap.values().forEach(node ->
                node.getChildren().sort(Comparator.comparing(StructureResponse::getSort))
        );

        return root;
    }

    public List<Structure> getTrashMenu() {
        return structureRepository.findByStt(-1);
    }

    public Structure saveOrUpdate(Structure structure) {
        return structureRepository.save(structure);
    }

    public void updateMenuOrder(List<StructureSortRequest> updates) {
        for (StructureSortRequest dto : updates) {
            structureRepository.updateParentAndSort(dto.getId(), dto.getSort());
        }
    }

    public StructureResponse getMenuByUser(Long userId, AppType appType, List<String> permissions, List<String> roles) {
        List<Structure> menus = structureRepository.findBySttAndAppTypeOrderBySortAsc(1, appType);

        List<Structure> menusByPermissions = menus.stream()
                .filter(menu -> {
                    // Nếu menu không yêu cầu quyền cụ thể nào -> Cho phép hiển thị luôn
                    if (menu.getPermissions() == null || menu.getPermissions().trim().isEmpty()) {
                        return true;
                    }

                    // Cắt chuỗi quyền của menu ra thành mảng (ví dụ: "user:view,user:create")
                    String[] requiredPerms = menu.getPermissions().split(",");

                    // Kiểm tra xem danh sách quyền của User có chứa ít nhất 1 quyền mà Menu yêu cầu không
                    return Arrays.stream(requiredPerms)
                            .anyMatch(perm -> permissions.contains(perm.trim()));
                })
                .toList();
        return buildTree(menusByPermissions);
    }

    public List<Structure> getModuleByAppType(AppType appType) {
        return structureRepository.findBySttAndAppTypeAndTypeOrderBySortAsc(1, appType, 2);
    }

    public List<Structure> getControllersByModule(Long moduleId) {
        return structureRepository.findBySttAndPidOrderBySortAsc(1, moduleId);
    }
}
