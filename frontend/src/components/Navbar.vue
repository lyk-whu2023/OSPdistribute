<template>
  <el-header class="navbar">
    <div class="navbar-content">
      <div class="navbar-left">
        <h1 class="logo" @click="goToHome">购物商城</h1>
      </div>
      
      <div class="navbar-center">
        <el-menu
          mode="horizontal"
          :ellipsis="false"
          @select="handleMenuSelect"
          class="nav-menu"
        >
          <el-menu-item index="home">
            <el-icon><House /></el-icon>
            <span>首页</span>
          </el-menu-item>
          <el-menu-item index="products">
            <el-icon><ShoppingCart /></el-icon>
            <span>商品</span>
          </el-menu-item>
          <el-menu-item index="seckill">
            <el-icon><Timer /></el-icon>
            <span>秒杀</span>
          </el-menu-item>
          <el-menu-item index="blog">
            <el-icon><Document /></el-icon>
            <span>博客</span>
          </el-menu-item>
          <el-menu-item index="stores">
            <el-icon><Shop /></el-icon>
            <span>店铺</span>
          </el-menu-item>
        </el-menu>
      </div>
      
      <div class="navbar-right">
        <div class="nav-actions">
          <el-button link @click="goToCart" class="cart-btn">
            <el-icon><ShoppingCart /></el-icon>
            <span class="badge">购物车</span>
            <span v-if="cartCount > 0" class="cart-count">{{ cartCount }}</span>
          </el-button>
          
          <template v-if="userStore.isLoggedIn">
            <el-dropdown @command="handleUserCommand">
              <span class="user-info">
                <el-avatar :size="32" :src="userAvatar">
                  <el-icon><User /></el-icon>
                </el-avatar>
                <span class="username">{{ userStore.userInfo?.nickname || userStore.userInfo?.username }}</span>
              </span>
              <template #dropdown>
                <el-dropdown-menu>
                  <el-dropdown-item command="userCenter">
                    <el-icon><User /></el-icon>
                    用户中心
                  </el-dropdown-item>
                  <el-dropdown-item command="orders">
                    <el-icon><List /></el-icon>
                    我的订单
                  </el-dropdown-item>
                  <el-dropdown-item command="favorites">
                    <el-icon><Star /></el-icon>
                    我的收藏
                  </el-dropdown-item>
                  <el-dropdown-item divided command="logout">
                    <el-icon><SwitchButton /></el-icon>
                    退出登录
                  </el-dropdown-item>
                </el-dropdown-menu>
              </template>
            </el-dropdown>
          </template>
          
          <template v-else>
            <el-button type="primary" @click="goToAuth">登录</el-button>
            <el-button @click="goToAuth">注册</el-button>
          </template>
        </div>
      </div>
    </div>
  </el-header>
</template>

<script setup>
import { computed } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { useUserStore } from '@/store/modules/user'
import { useCartStore } from '@/store/modules/cart'
import { getUserAvatar } from '@/utils/image'

const router = useRouter()
const route = useRoute()
const userStore = useUserStore()
const cartStore = useCartStore()

const cartCount = computed(() => cartStore.items.length)
const userAvatar = computed(() => {
  if (userStore.userInfo?.id) {
    return getUserAvatar(userStore.userInfo.id)
  }
  return ''
})

const handleMenuSelect = (index) => {
  switch (index) {
    case 'home':
      goToHome()
      break
    case 'products':
      goToProducts()
      break
    case 'seckill':
      goToSeckill()
      break
    case 'blog':
      goToBlog()
      break
    case 'stores':
      goToStores()
      break
  }
}

const handleUserCommand = (command) => {
  switch (command) {
    case 'userCenter':
      goToUserCenter()
      break
    case 'orders':
      goToOrders()
      break
    case 'favorites':
      goToFavorites()
      break
    case 'logout':
      handleLogout()
      break
  }
}

const goToHome = () => {
  router.push('/')
}

const goToProducts = () => {
  router.push('/products')
}

const goToSeckill = () => {
  router.push('/seckill')
}

const goToBlog = () => {
  router.push('/blog')
}

const goToStores = () => {
  router.push('/stores')
}

const goToCart = () => {
  if (!userStore.isLoggedIn) {
    router.push({ name: 'Auth', query: { redirect: '/cart' } })
    return
  }
  router.push('/cart')
}

const goToAuth = () => {
  router.push('/auth')
}

const goToUserCenter = () => {
  router.push('/user')
}

const goToOrders = () => {
  router.push('/orders')
}

const goToFavorites = () => {
  router.push('/favorites')
}

const handleLogout = async () => {
  await userStore.logout()
  router.push('/')
}
</script>

<style scoped>
.navbar {
  background-color: #fff;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  padding: 0;
  height: 60px;
  position: sticky;
  top: 0;
  z-index: 1000;
}

.navbar-content {
  display: flex;
  justify-content: space-between;
  align-items: center;
  height: 100%;
  max-width: 1400px;
  margin: 0 auto;
  padding: 0 20px;
}

.navbar-left {
  display: flex;
  align-items: center;
}

.logo {
  font-size: 24px;
  font-weight: bold;
  color: #409eff;
  cursor: pointer;
  margin: 0;
  transition: color 0.3s;
}

.logo:hover {
  color: #66b1ff;
}

.navbar-center {
  flex: 1;
  display: flex;
  justify-content: center;
}

.nav-menu {
  border-bottom: none;
  background-color: transparent;
}

.nav-menu .el-menu-item {
  border-bottom: none;
}

.navbar-right {
  display: flex;
  align-items: center;
}

.nav-actions {
  display: flex;
  align-items: center;
  gap: 15px;
}

.cart-btn {
  position: relative;
  font-size: 16px;
}

.cart-count {
  position: absolute;
  top: -8px;
  right: -8px;
  background-color: #f56c6c;
  color: #fff;
  border-radius: 50%;
  padding: 2px 6px;
  font-size: 12px;
  min-width: 18px;
  text-align: center;
}

.user-info {
  display: flex;
  align-items: center;
  gap: 8px;
  cursor: pointer;
}

.username {
  font-size: 14px;
  color: #606266;
}

.badge {
  margin-left: 5px;
}
</style>
