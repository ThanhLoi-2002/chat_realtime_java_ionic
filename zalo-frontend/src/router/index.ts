import { useDevice } from '@/composables/useDevice';
import { useUserStore } from '@/stores/App/user.storage.ts';
import { ACCESS_TOKEN, ROUTE } from '@/utils/constant';
import { getKey } from '@/utils/local';
import { isPlatform } from '@ionic/vue';
import { createRouter, createWebHistory } from '@ionic/vue-router';
import { RouteRecordRaw } from 'vue-router';

export const modules = import.meta.glob(
  '../views/**/*.vue'
)

const routes: Array<RouteRecordRaw> = [
  {
    path: '/',
    redirect: ROUTE.CHATS
  },
  {
    path: ROUTE.CHATS,
    component: () => import('../views/App/Chat/ChatPage.vue'),
    meta: { layout: "main", requiresAuth: true }
  },
  {
    path: `${ROUTE.JOIN_GROUP}/:code`,
    component: () => import('../views/App/JoinGroup/JoinGroup.vue'),
    meta: { layout: "noLayout", requiresAuth: false }
  },
  {
    path: `${ROUTE.SCAN}`,
    component: () => import('../views/App/Scan/QrCodeScannerPage.vue'),
    meta: { layout: "noLayout", requiresAuth: false }
  },
  {
    path: ROUTE.FRIENDS,
    component: () => import('../views/App/Friend/FriendPage.vue'),
    meta: { layout: "main", requiresAuth: true },
  },

  // -------------------- OA ROUTES --------------------
  {
    path: ROUTE.OA_DASHBOARD.INDEX,
    meta: { layout: "oa", requiresAuth: true },
    children: [
      {
        path: ROUTE.OA_DASHBOARD.HOME,
        component: () => import('../views/OA/Home/Home.vue')
      },
      {
        path: ROUTE.OA_DASHBOARD.CHATBOT,
        component: () => import('../views/OA/Chatbot/Chatbot.vue')
      },
      {
        path: ROUTE.OA_DASHBOARD.MANAGEMENT,
        component: () => import('../views/OA/Management/Management.vue')
      },
    ]
  },

  // -------------------- ADMIN ROUTES --------------------
  {
    path: ROUTE.ADMIN_DASHBOARD.INDEX,
    meta: { layout: "admin", requiresAuth: true },
    children: [
      {
        path: '',
        component: () => import('../views/Admin/Index.vue')
      },
      {
        path: ROUTE.ADMIN_DASHBOARD.SYSTEM,
        children: [
          {
            path: ROUTE.ADMIN_DASHBOARD.STRUCTURE,
            component: () => import('../views/Admin/System/Structure/Structure.vue')
          },
          {
            path: ROUTE.ADMIN_DASHBOARD.ROLE,
            component: () => import('../views/Admin/System/Role/Role.vue')
          },
          {
            path: ROUTE.ADMIN_DASHBOARD.USER,
            component: () => import('../views/Admin/System/User/User.vue')
          },
          {
            path: ROUTE.ADMIN_DASHBOARD.USER_ROLE,
            component: () => import('../views/Admin/System/UserRole/UserRole.vue')
          },
          {
            path: ROUTE.ADMIN_DASHBOARD.LANG,
            children: [
              {
                path: "",
                component: () => import('../views/Admin/System/Lang/LangPage.vue')
              },
              {
                path: ":id",
                component: () => import('../views/Admin/System/Lang/EditLang.vue')
              },
              {
                path: "add",
                component: () => import('../views/Admin/System/Lang/AddLang.vue')
              }
            ]
          },
        ],
      },
    ]
  },

  // -------------------- AUTH ROUTES --------------------
  {
    path: "",
    meta: { layout: "auth" },
    children: [
      {
        path: ROUTE.LOGIN,
        component: () => import('../views/App/Auth/LoginPage.vue'),
        meta: { guestOnly: true }
      },
      {
        path: ROUTE.REGISTER,
        component: () => import('../views/App/Auth/RegisterPage.vue'),
        meta: { guestOnly: true }
      }
    ]
  },

  {
    path: ROUTE.NOT_FOUND,
    name: 'NotFound',
    component: () => import('../views/App/Error/NotFoundPage.vue'),
    meta: { layout: "noLayout", requiresAuth: false }
  },

  {
    path: '/:pathMatch(.*)*',
    name: 'NotFoundAll',
    component: () => import('../views/App/Error/NotFoundPage.vue'),
    meta: { layout: "noLayout", requiresAuth: false }
  },
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

