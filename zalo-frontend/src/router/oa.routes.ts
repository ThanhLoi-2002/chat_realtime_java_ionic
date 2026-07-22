import type { RouteRecordRaw } from 'vue-router'
import { ROUTE } from '@/utils/constant'
import { AppTypeEnum } from '@/types/enum.ts'

export const oaRoutes: RouteRecordRaw = {
    path: ROUTE.OA_DASHBOARD.INDEX,
    name: AppTypeEnum.OA,
    meta: { layout: "oa", requiresAuth: true },
    children: [
        {
            path: ROUTE.OA_DASHBOARD.DASHBOARD,
            component: () => import('../views/OA/Dashboard/Dashboard.vue')
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