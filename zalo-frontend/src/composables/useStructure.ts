import { useAdminStructureStore } from "@/stores/Admin/structure.storage"
import { StructureType } from "@/types/entities";
import { useRoute } from "vue-router";

export const useStructure = () => {
    const structureStor = useAdminStructureStore()

    const route = useRoute()

    const normalizePath = (path: string) => {
        return path.replace(/^\/+/, '')
    }

    const checkRouteActive = (path: string) => {
        const currentPath = route.path
        const menuPath = path

        return currentPath === menuPath ||
            currentPath.startsWith(menuPath + '/')
    }

    function getMenusByType(
        type: number
    ): StructureType[] {
        const result: StructureType[] = [];

        const traverse = (items: StructureType[]) => {
            items?.forEach(item => {
                if (item.type === type) {
                    result.push(item);
                }

                if (item.children?.length) {
                    traverse(item.children);
                }
            });
        };

        traverse(structureStor.menu?.children || []);
        return result;
    }

    function findParent(
        nodes: StructureType[],
        childId: number
    ): StructureType | null {

        for (const node of nodes) {

            if (
                node.children?.some(
                    c => c.id === childId
                )
            ) {
                return node;
            }

            const parent = findParent(
                node.children || [],
                childId
            );

            if (parent) {
                return parent;
            }
        }

        return null;
    }

    return { getMenusByType, findParent, checkRouteActive }
}