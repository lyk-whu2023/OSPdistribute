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
  },
  {
    path: '/products',
    name: 'ProductList',
    component: () => import('@/views/ProductListView.vue')
  },
  {
    path: '/product/:id',
    name: 'ProductDetail',
    component: () => import('@/views/ProductDetailView.vue')
  },
  {
    path: '/seckill',
    name: 'Seckill',
    component: () => import('@/views/SeckillView.vue')
  },
  {
    path: '/cart',
    name: 'Cart',
    component: () => import('@/views/CartView.vue'),
    meta: { requiresAuth: true }
  },
  {
    path: '/order/submit',
    name: 'OrderSubmit',
    component: () => import('@/views/OrderSubmitView.vue'),
    meta: { requiresAuth: true }
  },
  {
    path: '/orders',
    name: 'OrderList',
    component: () => import('@/views/OrderListView.vue'),
    meta: { requiresAuth: true }
  },
  {
    path: '/order/:id',
    name: 'OrderDetail',
    component: () => import('@/views/OrderDetailView.vue'),
    meta: { requiresAuth: true }
  },
  {
    path: '/blog',
    name: 'Blog',
    component: () => import('@/views/BlogView.vue')
  },
  {
    path: '/blog/:id',
    name: 'BlogDetail',
    component: () => import('@/views/BlogDetailView.vue')
  },
  {
    path: '/blog/create',
    name: 'BlogCreate',
    component: () => import('@/views/BlogCreateView.vue'),
    meta: { requiresAuth: true }
  },
  {
    path: '/stores',
    name: 'Stores',
    component: () => import('@/views/StoreView.vue')
  },
  {
    path: '/store/:id',
    name: 'StoreDetail',
    component: () => import('@/views/StoreDetailView.vue')
  },
  {
    path: '/favorites',
    name: 'Favorites',
    component: () => import('@/views/FavoritesView.vue'),
    meta: { requiresAuth: true }
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

router.beforeEach(async (to, from, next) => {
  const userStore = useUserStore()
  
  console.log('=== 路由守卫 ===')
  console.log('to.path:', to.path)
  console.log('to.meta.requiresAuth:', to.meta.requiresAuth)
  console.log('userStore.isLoggedIn:', userStore.isLoggedIn)
  console.log('userStore.userId:', userStore.userId)
  console.log('userStore.token:', userStore.token ? '存在' : '不存在')
  
  // 如果有 token 但没有用户信息，尝试初始化
  if (userStore.token && !userStore.userInfo) {
    console.log('正在初始化用户信息...')
    await userStore.initUserInfo()
  }
  
  if (to.meta.requiresAuth && !userStore.isLoggedIn) {
    console.log('未登录，跳转到登录页')
    next({ name: 'Auth', query: { redirect: to.fullPath } })
  } else {
    console.log('允许访问')
    next()
  }
})

// 监听 token 失效事件，处理页面跳转
if (typeof window !== 'undefined') {
  window.addEventListener('token-expired', () => {
    const currentRoute = router.currentRoute.value
    if (currentRoute && currentRoute.meta.requiresAuth) {
      router.push({
        name: 'Auth',
        query: { redirect: currentRoute.fullPath }
      })
    }
  })
}

export default router