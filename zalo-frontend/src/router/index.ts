import { useDevice } from '@/composables/useDevice';
import { useUserStore } from '@/stores/App/user.storage.ts';
import { ACCESS_TOKEN, APP_ROUTE, ROUTE } from '@/utils/constant';
import { getKey } from '@/utils/local';
import { isPlatform } from '@ionic/vue';
import { createRouter, createWebHistory } from '@ionic/vue-router';
import { RouteRecordRaw } from 'vue-router';
import { adminRoutes } from './admin.routes.ts';
import { oaRoutes } from './oa.routes.ts';
import { appRoutes } from './app.routes.ts';
import { AppTypeEnum } from '@/types/enum.ts';
import { useMenuStore } from '@/stores/menu.storage.ts';

const routes: Array<RouteRecordRaw> = [
  ...appRoutes,

  // -------------------- OA ROUTES --------------------
  // oaRoutes,
  {
    path: ROUTE.OA_DASHBOARD.INDEX,
    name: AppTypeEnum.OA,
    component: () => import('../layouts/OALayout.vue'),
    meta: { requiresAuth: true },
    children: []
  },

  // -------------------- ADMIN ROUTES --------------------
  // adminRoutes,
  {
    path: ROUTE.ADMIN_DASHBOARD.INDEX,
    name: AppTypeEnum.ADMIN,
    component: () => import('../layouts/AdminLayout.vue'),
    meta: { requiresAuth: true },
    children: [
      {
        path: '',
        name: 'adminIndex',
        component: () => import('../views/Admin/Index.vue')
      },
    ]
  }
]

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes
})

// -------------------- GLOBAL GUARD --------------------
router.beforeEach(async (to) => {
  console.log(router.getRoutes())
  const userStore = useUserStore()
  const menuStor = useMenuStore()
  const accessToken = getKey(ACCESS_TOKEN)
  // const { isSmartDevice } = useDevice()

  if (!userStore.user && isPlatform('hybrid')) await userStore.loadUserLocal()

  if (accessToken && !userStore.user) {
    await userStore.getMe();
  }

  // 1. Guest only (signin/signup)
  if (to.meta.guestOnly && userStore.user) {
    return { path: APP_ROUTE.index };
  }

  // 2. Requires login
  if (to.meta.requiresAuth && !userStore.user) {
    return { path: ROUTE.LOGIN };
  }

  // ==========================================================
  // 3. CHECK QUYỀN TRUY CẬP (PERMISSION CHECK)
  // ==========================================================
  // Chỉ kiểm tra khi route yêu cầu phân quyền
  if (to.meta.requiresAuth && userStore.user && to.meta.permissions) {
    if (!userStore.user.permissions.includes(to.meta.permissions as string)) {
      console.warn(`[Guard] User không có quyền vào đường dẫn: ${to.path}. Đẩy sang 403.`);
      return { path: ROUTE.FORBIDDEN }; // Hoặc ROUTE.FORBIDDEN (Trang 403) của bạn
    }
  }

  // Nếu F5 làm trống Store menuList
  if (!menuStor.isLoaded && userStore.user) {
    console.log("refresh")
    try {
      // 1. Phân tích URL hiện tại để nhận diện App tương ứng
      let currentApp = AppTypeEnum.ADMIN; // mặc định
      if (to.path.startsWith(ROUTE.APP.INDEX)) currentApp = AppTypeEnum.APP;
      if (to.path.startsWith(ROUTE.OA_DASHBOARD.INDEX)) currentApp = AppTypeEnum.OA;

      // 2. Kích hoạt nạp lại đúng layout và danh sách route con
      await menuStor.switchApp(currentApp);

      // 3. Đi tiếp
      return { ...to, replace: true };
    } catch (e) {
      console.error(e);
      return { path: ROUTE.LOGIN };
    }
  }

  // // 3. Role check
  // if (to.meta.role) {
  //   const requiredRole = to.meta.role;

  //   if (user.value?.role == RoleEnum.ADMIN && requiredRole == RoleEnum.USER) {
  //     return { name: "admin-statistics" }
  //   }
  // }

  // // 4. Safety: admin path but not admin
  // if (to.path.startsWith("/dashboard") && user.value?.role !== RoleEnum.ADMIN) {
  //   return { name: "home" };
  // }

  // // 5. Safety: user path but not user
  // if (to.path == "/" && user.value?.role == RoleEnum.ADMIN) {
  //   return { name: "admin-statistics" };
  // }
  return true
});

router.onError((error) => {
  console.log(error.message)
});

export default router;

