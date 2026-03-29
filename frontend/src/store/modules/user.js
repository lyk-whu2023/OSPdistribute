import { defineStore } from 'pinia'
import { login, register, logout, getUserInfo } from '@/api/user'
import { setToken, removeToken, getToken } from '@/utils/request'

// 解析 JWT token 中的用户信息
function parseToken(token) {
  try {
    if (!token) return null
    const base64Url = token.split('.')[1]
    const base64 = base64Url.replace(/-/g, '+').replace(/_/g, '/')
    const jsonPayload = decodeURIComponent(atob(base64).split('').map(c => {
      return '%' + ('00' + c.charCodeAt(0).toString(16)).slice(-2)
    }).join(''))
    return JSON.parse(jsonPayload)
  } catch (e) {
    console.error('解析 token 失败:', e)
    return null
  }
}

const parsedToken = parseToken(getToken())

export const useUserStore = defineStore('user', {
  state: () => ({
    token: getToken(),
    userInfo: null,
    userId: parsedToken?.sub || parsedToken?.userId || null
  }),

  getters: {
    isLoggedIn: (state) => !!state.token,
    username: (state) => state.userInfo?.username || '',
    nickname: (state) => state.userInfo?.nickname || ''
  },

  actions: {
    async login(loginForm) {
      try {
        const response = await login(loginForm)
        console.log('登录响应:', response)
        
        // 响应现在包含 token 和 user 对象
        this.token = response.token || response
        setToken(this.token)
        console.log('Token 已保存:', this.token)
        
        // 从响应中获取用户信息
        if (response.user) {
          this.userInfo = response.user
          this.userId = response.user.id
          console.log('用户信息已设置:', this.userInfo)
        }
        
        return Promise.resolve(response)
      } catch (error) {
        console.error('登录失败:', error)
        return Promise.reject(error)
      }
    },

    async initUserInfo() {
      // 如果已有 token 但没有用户信息，尝试获取
      if (this.token && !this.userId) {
        try {
          // 从 token 中解析用户信息或调用 API 获取
          // 这里假设 token 中包含了用户信息，或者调用后端接口获取
          const response = await getUserInfo(this.userId)
          if (response) {
            this.userInfo = response
            this.userId = response.id
          }
        } catch (error) {
          console.error('初始化用户信息失败:', error)
        }
      }
    },

    async register(registerForm) {
      try {
        const response = await register(registerForm)
        return Promise.resolve(response)
      } catch (error) {
        return Promise.reject(error)
      }
    },

    async logout() {
      try {
        await logout()
      } finally {
        this.token = null
        this.userInfo = null
        this.userId = null
        removeToken()
      }
    },

    async getUserInfo(userId) {
      try {
        const response = await getUserInfo(userId)
        this.userInfo = response
        this.userId = userId || response.id
        return Promise.resolve(response)
      } catch (error) {
        return Promise.reject(error)
      }
    },

    setUserInfo(userInfo) {
      this.userInfo = userInfo
    }
  }
})