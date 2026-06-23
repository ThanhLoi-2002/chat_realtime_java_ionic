import { ROUTE } from "@/utils/constant.ts";
import { RouteRecordRaw } from "vue-router";

export const appRoutes: RouteRecordRaw[] = [
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