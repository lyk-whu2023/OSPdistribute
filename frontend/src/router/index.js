import { createRouter, createWebHistory } from 'vue-router'
import { useUserStore } from '@/store/modules/user'

const routes = [
  {
    path: '/',
    name: 'Home',
    component: () => import('@/views/HomeView.vue')
  },
  {
    path: '/auth',
    name: 'Auth',
    component: () => import('@/views/AuthView.vue'),
    meta: { requiresAuth: false }
  },
  {
    path: '/user',
    name: 'UserCenter',
    component: () => import('@/views/UserCenterView.vue'),
    meta: { requiresAuth: true }
  },
  {
    path: '/address',
    name: 'Address',
    component: () => import('@/views/AddressView.vue'),
    meta: { requiresAuth: true }
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

router.beforeEach((to, from, next) => {
  const userStore = useUserStore()
  
  if (to.meta.requiresAuth && !userStore.isLoggedIn) {
    next({ name: 'Auth', query: { redirect: to.fullPath } })
  } else {
    next()
  }
})

export default router