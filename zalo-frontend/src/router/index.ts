import { useUserStore } from '@/stores/user.storage';
import { ACCESS_TOKEN, ROUTE } from '@/utils/constant';
import { getKey } from '@/utils/local';
import { createRouter, createWebHistory } from '@ionic/vue-router';
import { RouteRecordRaw } from 'vue-router';

const routes: Array<RouteRecordRaw> = [
  {
    path: '/',
    redirect: ROUTE.CHATS
  },
  {
    path: ROUTE.CHATS,
    component: () => import('../views/Chat/ChatPage.vue'),
    meta: { layout: "main", requiresAuth: true }
  },
  {
    path: ROUTE.LANGUAGES,
    meta: { layout: "main", requiresAuth: true },
    children: [
      {
        path: "",
        component: () => import('../views/Lang/LangPage.vue')
      },
      {
        path: ":id",
        component: () => import('../views/Lang/EditLang.vue')
      },
      {
        path: "add",
        component: () => import('../views/Lang/AddLang.vue')
      }
    ]
  },
  {
    path: ROUTE.FRIENDS,
    component: () => import('../views/Friend/FriendPage.vue'),
    meta: { layout: "main", requiresAuth: true },
  },

  // -------------------- AUTH ROUTES --------------------
  {
    path: "",
    meta: { layout: "auth" },
    children: [
      {
        path: ROUTE.LOGIN,
        component: () => import('../views/Auth/LoginPage.vue'),
        meta: { guestOnly: true }
      },
      {
        path: ROUTE.REGISTER,
        component: () => import('../views/Auth/RegisterPage.vue'),
        meta: { guestOnly: true }
      }
    ]
  },
]

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes
})

// -------------------- GLOBAL GUARD --------------------
router.beforeEach(async (to) => {
  const userStore = useUserStore()
  const accessToken = getKey(ACCESS_TOKEN)

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

export default router;

