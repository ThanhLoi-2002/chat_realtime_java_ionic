import type { RouteRecordRaw } from 'vue-router'
import { ROUTE } from '@/utils/constant'

export const oaRoutes: RouteRecordRaw = {
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
}