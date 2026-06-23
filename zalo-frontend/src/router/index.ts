import { useDevice } from '@/composables/useDevice';
import { useUserStore } from '@/stores/App/user.storage.ts';
import { ACCESS_TOKEN, ROUTE } from '@/utils/constant';
import { getKey } from '@/utils/local';
import { isPlatform } from '@ionic/vue';
import { createRouter, createWebHistory } from '@ionic/vue-router';
import { RouteRecordRaw } from 'vue-router';
import { adminRoutes } from './admin.routes.ts';
import { oaRoutes } from './oa.routes.ts';
import { appRoutes } from './app.routes.ts';

// export const modules = import.meta.glob(
//   '../views/**/*.vue'
// )

const routes: Array<RouteRecordRaw> = [
  ...appRoutes,

  // -------------------- OA ROUTES --------------------
  oaRoutes,

  // -------------------- ADMIN ROUTES --------------------
  adminRoutes,
]

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes
})

// -------------------- GLOBAL GUARD --------------------
router.beforeEach(async (to) => {
  console.log(routes)
  const userStore = useUserStore()
  const accessToken = getKey(ACCESS_TOKEN)
  // const { isSmartDevice } = useDevice()

  if (!userStore.user && isPlatform('hybrid')) await userStore.loadUserLocal()

  if (accessToken && !userStore.user) {
    await userStore.getMe();
  }

  // 1. Guest only (signin/signup)
  if (to.meta.guestOnly && userStore.user) {
    return { path: ROUTE.CHATS };
  }

  // 2. Requires login
  if (to.meta.requiresAuth && !userStore.user) {
    return { path: ROUTE.LOGIN };
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
});

router.onError((error) => {
  console.log(error.message)
});

export default router;

