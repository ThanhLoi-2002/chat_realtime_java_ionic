import { AppTypeEnum } from "@/types/enum.ts";
import { APP_ROUTE, OA_ROUTE, ROUTE } from "@/utils/constant.ts";
import { RouteRecordRaw } from "vue-router";

export const appRoutes: RouteRecordRaw[] = [
    {
        path: '',
        redirect: APP_ROUTE.index
    },
    {
        path: ROUTE.OA_DASHBOARD.INDEX,
        redirect: OA_ROUTE.home
    },
    // {
    //     path: ROUTE.ADMIN_DASHBOARD.INDEX,
    //     redirect: ADMIN_ROUTE.home
    // },
    {
        path: ROUTE.APP.INDEX,
        name: AppTypeEnum.APP,
        component: () => import('../layouts/MainLayout.vue'),
        meta: { requiresAuth: true },
        children: [
            {
                path: ROUTE.APP.CHATS,
                component: () => import('../views/App/Chat/ChatPage.vue'),
            },
            {
                path: ROUTE.APP.FRIENDS,
                component: () => import('../views/App/Friend/FriendPage.vue'),
            },
            {
                path: ROUTE.APP.AI_STICKER,
                component: () => import('../views/App/AISticker/AISticker.vue'),
            },
        ]
    },

    // -------------------- AUTH ROUTES --------------------
    {
        path: "/auth",
        component: () => import('../layouts/AuthLayout.vue'),
        meta: { guestOnly: true },
        children: [
            {
                path: 'login',
                component: () => import('../views/App/Auth/LoginPage.vue'),
            },
            {
                path: 'register',
                component: () => import('../views/App/Auth/RegisterPage.vue'),
            }
        ]
    },

    {
        path: '',
        component: () => import('../layouts/NoLayout.vue'),
        meta: { requiresAuth: false },
        children: [
            {
                path: `${ROUTE.APP.JOIN_GROUP}/:code`,
                component: () => import('../views/App/JoinGroup/JoinGroup.vue'),
                meta: { requiresAuth: false }
            },
            {
                path: `${ROUTE.APP.SCAN}`,
                component: () => import('../views/App/Scan/QrCodeScannerPage.vue'),
                meta: { requiresAuth: false }
            },
            {
                path: ROUTE.FORBIDDEN,
                component: () => import('../views/App/Error/ForbiddenPage.vue')
            },
            {
                path: ROUTE.NOT_FOUND,
                component: () => import('../views/App/Error/NotFoundPage.vue')
            },
            {
                path: ':pathMatch(.*)*',
                component: () => import('../views/App/Error/NotFoundPage.vue')
            }
        ]
    }
]