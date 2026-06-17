package com.zalo.modules.admin.structure.service;

import com.zalo.modules.admin.structure.dto.request.StructureSortRequest;
import com.zalo.modules.admin.structure.dto.response.StructureResponse;
import com.zalo.modules.admin.structure.entity.AppType;
import com.zalo.modules.admin.structure.entity.Structure;
import jakarta.transaction.Transactional;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Transactional
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class StructureService {
    StructureRepository structureRepository;

    public Map<AppType, List<StructureResponse>> getMenuTree() {
        List<Structure> allMenus = structureRepository.findBySttOrderBySortAsc(1);

        return allMenus.stream()
                .collect(Collectors.groupingBy(Structure::getAppType))
                .entrySet()
                .stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        entry -> buildTree(entry.getValue())
                ));
    }

    private List<StructureResponse> buildTree(List<Structure> menus) {

        List<StructureResponse> nodes = menus.stream()
                .map(StructureResponse::new)
                .toList();

        Map<Long, StructureResponse> nodeMap = nodes.stream()
                .collect(Collectors.toMap(
                        StructureResponse::getId,
                        node -> node
                ));

        List<StructureResponse> roots = new ArrayList<>();

        for (StructureResponse node : nodes) {
            if (node.getPid() == null || node.getPid() == 0) {
                roots.add(node);
            } else {
                StructureResponse parent = nodeMap.get(node.getPid());

                if (parent != null) {
                    parent.getChildren().add(node);
                } else {
                    roots.add(node);
                }
            }
        }

        for (StructureResponse node : nodes) {
            node.getChildren().sort(
                    Comparator.comparing(StructureResponse::getSort)
            );
        }

        roots.sort(
                Comparator.comparing(StructureResponse::getSort)
        );

        return roots;
    }

    public List<Structure> getTrashMenu() {
        return structureRepository.findByStt(-1);
    }

    public Structure saveOrUpdate(Structure structure) {
        return structureRepository.save(structure);
    }

    public void updateMenuOrder(List<StructureSortRequest> updates) {
        for (StructureSortRequest dto : updates) {
            structureRepository.updateParentAndSort(dto.getId(), dto.getPid(), dto.getSort());
        }
    }
}
