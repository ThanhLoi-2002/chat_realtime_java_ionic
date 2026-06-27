import { AppTypeEnum } from "@/types/enum.ts";
import { APP_ROUTE, ROUTE } from "@/utils/constant.ts";
import { RouteRecordRaw } from "vue-router";

export const appRoutes: RouteRecordRaw[] = [
    {
        path: '/',
        redirect: APP_ROUTE.index
    },
    {
        path: ROUTE.APP.INDEX,
        name: AppTypeEnum.APP,
        meta: { layout: "main", requiresAuth: true },
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
    {
        path: `${ROUTE.APP.JOIN_GROUP}/:code`,
        component: () => import('../views/App/JoinGroup/JoinGroup.vue'),
        meta: { layout: "noLayout", requiresAuth: false }
    },
    {
        path: `${ROUTE.APP.SCAN}`,
        component: () => import('../views/App/Scan/QrCodeScannerPage.vue'),
        meta: { layout: "noLayout", requiresAuth: false }
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
        path: ROUTE.FORBIDDEN,
        component: () => import('../views/App/Error/ForbiddenPage.vue'),
        meta: { layout: "noLayout", requiresAuth: false }
    },

    {
        path: ROUTE.NOT_FOUND,
        component: () => import('../views/App/Error/NotFoundPage.vue'),
        meta: { layout: "noLayout", requiresAuth: false }
    },

    {
        path: '/:pathMatch(.*)*',
        component: () => import('../views/App/Error/NotFoundPage.vue'),
        meta: { layout: "noLayout", requiresAuth: false }
    },
]