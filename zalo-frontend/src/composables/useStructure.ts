import { useAdminStructureStore } from "@/stores/Admin/structure.storage"
import { useLangStore } from "@/stores/App/lang.storage"
import { StructureType } from "@/types/entities";

export const useStructure = () => {
    const structureStor = useAdminStructureStore()

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

        traverse(structureStor.menu);
        console.log(result)
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

    return { getMenusByType, findParent }
}