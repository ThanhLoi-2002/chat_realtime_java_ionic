import { useUserStore } from "@/stores/App/user.storage";
import { useMenuStore } from "@/stores/menu.storage";
import { StructureType } from "@/types/entities";
import { useRoute } from "vue-router";

export const useStructure = () => {
    const menuStor = useMenuStore()
    const userStor = useUserStore()
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

    const getMenusByType = (
        type: number
    ): StructureType[] => {
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

        traverse(menuStor.menuList || []);
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

    const findCurrentMenuChildren = (
        currentList: StructureType[] = menuStor.menuList,
        visited = new Set<any>()
    ): StructureType[] => {

        if (!Array.isArray(currentList) || currentList.length === 0) return [];

        // Bước 1: Tìm xem trong tầng hiện tại có phần tử nào trùng khớp khít với route.path không
        const matchedMenu = currentList.find(item => item && item.path === route.path);

        if (matchedMenu) {
            // Nếu tìm thấy menu trùng path, trả về mảng children của chính nó (nếu không có con thì trả về mảng rỗng)
            return matchedMenu.children || [];
        }

        // Bước 2: Nếu tầng hiện tại không có, tiến hành đào sâu xuống các nhánh con của từng phần tử
        for (const item of currentList) {
            if (!item || !item.children || item.children.length === 0) continue;

            // Cơ chế bảo vệ: Tránh loop dữ liệu tuần hoàn từ backend gây tràn stack
            if (visited.has(item)) {
                console.warn("[Menu] Phát hiện vòng lặp tuần hoàn tại node:", item.code);
                continue;
            }
            visited.add(item);

            // Đệ quy xuống tầng con sâu hơn
            const result = findCurrentMenuChildren(item.children, visited);

            // Nếu nhánh con tìm thấy kết quả thì lập tức trả ngược lên trên luôn
            if (result.length > 0) {
                return result;
            }
        }

        return []; // Trả về mảng rỗng nếu toàn bộ cây menu không có ai trùng path
    }

    const getPagePath = (code: string): string => {
        // 1. Lấy ra danh sách các menu con của trang hiện tại
        const list = findCurrentMenuChildren();

        // 2. Tìm kiếm phần tử có code trùng khớp
        const targetPage = list.find(i => i.code === code);

        // 3. Nếu tìm thấy thì trả về path, không thấy thì trả về chuỗi rỗng '' (hoặc undefined tùy bạn)
        return targetPage ? targetPage.path : '';
    }

    const getPermissionByCode = (code: string) => {
        const userPermissions: string[] = userStor.user?.permissions || [];

        if (userPermissions.length === 0) return false;

        const pathParts = route.path.split('/').filter(part => part.trim() !== '');

        if (pathParts.length === 0) return false;

        const currentEndpoint = pathParts[pathParts.length - 1]; // Lấy phần tử cuối cùng (ví dụ: 'lang')

        // 3. Ghép chuỗi quyền mong muốn theo định dạng 'endpoint:action' (ví dụ: 'lang:edit')
        const requiredPermission = `${currentEndpoint}:${code}`;

        // 4. Kiểm tra xem quyền yêu cầu có nằm trong mảng quyền của user không
        return userPermissions.includes(requiredPermission);
    }

    //Lấy route đầu tiên của menu
    const buildFirstRoute = (node: StructureType) => {
        console.log(node.children.length > 0 ? node.children[0].path : '')
        return node.children.length > 0 ? node.children[0].path : ''
    }

    return { getMenusByType, findParent, checkRouteActive, findCurrentMenuChildren, getPagePath, getPermissionByCode, buildFirstRoute }
}