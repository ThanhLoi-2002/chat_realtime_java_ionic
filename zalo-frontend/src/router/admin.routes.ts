import type { RouteRecordRaw } from 'vue-router'
import { ROUTE } from '@/utils/constant'

export const adminRoutes: RouteRecordRaw = {
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
                    children: [
                        {
                            path: "",
                            component: () => import('../views/Admin/System/UserRole/UserRole.vue')
                        },
                        {
                            path: ROUTE.ADMIN_DASHBOARD.USER_ROLE_EDIT,
                            component: () => import('../views/Admin/System/UserRole/Edit.vue')
                        },
                    ]
                },
                {
                    path: ROUTE.ADMIN_DASHBOARD.PERMISSION,
                    children: [
                        {
                            path: "",
                            component: () => import('../views/Admin/System/Permission/Permission.vue')
                        },
                        {
                            path: ROUTE.ADMIN_DASHBOARD.SET_ACCESS,
                            component: () => import('../views/Admin/System/Permission/SetAccess.vue')
                        },
                    ]
                },
                {
                    path: ROUTE.ADMIN_DASHBOARD.LANG,
                    children: [
                        {
                            path: "",
                            component: () => import('../views/Admin/System/Lang/Lang.vue')
                        },
                        {
                            path: ":id",
                            component: () => import('../views/Admin/System/Lang/Edit.vue')
                        },
                        {
                            path: "add",
                            component: () => import('../views/Admin/System/Lang/Add.vue')
                        }
                    ]
                },
            ],
        },
    ]
}