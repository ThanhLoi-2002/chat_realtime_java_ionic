// stores/appMenu.ts
import { defineStore } from 'pinia';
import router from '@/router';
import { StructureType } from '@/types/entities';
import { generateRoutesFromMenu } from '@/router/dynamicRouter';
import { useAdminStructureStore } from './Admin/structure.storage';
import { AppTypeEnum } from '@/types/enum';
import { ROUTE } from '@/utils/constant';

// Mảng lưu các hàm xóa route cũ
let removeRouteCallbacks: (() => void)[] = [];

// Bảng map ánh xạ giữa loại App và Tên (Name) của Route Layout tương ứng
const layoutMap: Record<string, string> = {
    'ADMIN': AppTypeEnum.ADMIN,
    'OA': AppTypeEnum.OA,
    'APP': AppTypeEnum.APP
};

export const useMenuStore = defineStore('menu', {
    state: () => ({
        currentAppType: '' as AppTypeEnum,
        menuList: [] as StructureType[],
        isLoaded: false, // <-- Thêm cờ này để đánh dấu trạng thái đã gọi API
    }),

    actions: {
        async switchApp(appType: AppTypeEnum) {
            console.log("switchApp: ", appType)
            const structureStor = useAdminStructureStore()
            
            this.currentAppType = appType;
            this.isLoaded = true; // <-- BẬT CỜ NGAY KHI BẮT ĐẦU HOẶC KẾT THÚC XỬ LÝ

            // 1. Clear toàn bộ route của app cũ trước đó
            removeRouteCallbacks.forEach(removeFn => removeFn());
            removeRouteCallbacks = [];

            // 2. Fetch danh sách menu từ Server
            const rawMenu: StructureType = await structureStor.getMenuByUser(appType);
            
            if (!rawMenu || rawMenu.children.length === 0) {
                console.warn(`[Router] Menu của app ${appType} rỗng. Bỏ qua nạp route.`);
                this.menuList = []; 
                return; // Thoát sớm, cờ isLoaded vẫn là true nên Guard sẽ không gọi lại nữa
            }

            this.menuList = rawMenu.children;

            // 3. Phân tách cây và nạp route động
            const newRoutes = generateRoutesFromMenu(this.menuList);
            const targetLayoutName = layoutMap[appType];

            if (targetLayoutName) {
                newRoutes.forEach(route => {
                    const removeFn = router.addRoute(targetLayoutName, route);
                    removeRouteCallbacks.push(removeFn);
                });
                console.log(`[Router] Đã nạp thành công các route vào layout: ${targetLayoutName}`);
            }

            // 4. Điều hướng về trang chủ tương ứng của App
            if (appType === AppTypeEnum.ADMIN) {
                router.push(ROUTE.ADMIN_DASHBOARD.INDEX);
            } else if (appType === AppTypeEnum.OA) {
                router.push(ROUTE.OA_DASHBOARD.INDEX);
            } else if (appType === AppTypeEnum.APP) {
                router.push(ROUTE.APP.INDEX);
            }
        },

        /**
         * Thêm hàm reset này khi người dùng bấm nút chủ động đổi App từ giao diện (Click Switch App)
         */
        resetLoadState() {
            this.isLoaded = false;
        }
    }
});