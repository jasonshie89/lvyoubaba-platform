import { createRouter, createWebHistory } from 'vue-router'
import Layout from '../components/Layout.vue'
import Dashboard from '../views/Dashboard.vue'
import UserManage from '../views/UserManage.vue'

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: '/',
      component: Layout,
      redirect: '/dashboard',
      children: [
        {
          path: 'dashboard',
          name: 'Dashboard',
          component: Dashboard
        },
        {
          path: 'user-manage',
          name: 'UserManage',
          component: UserManage
        },
        {
          path: 'scenic-manage',
          name: 'ScenicManage',
          component: () => import('../views/ScenicManage.vue')
        },
        {
          path: 'team-manage',
          name: 'TeamManage',
          component: () => import('../views/TeamManage.vue')
        },
        {
          path: 'activity-manage',
          name: 'ActivityManage',
          component: () => import('../views/ActivityManage.vue')
        }
      ]
    }
  ],
})

export default router
