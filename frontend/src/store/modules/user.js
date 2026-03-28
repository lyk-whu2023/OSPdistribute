import { defineStore } from 'pinia'
import { login, register, logout, getUserInfo } from '@/api/user'
import { setToken, removeToken, getToken } from '@/utils/request'

export const useUserStore = defineStore('user', {
  state: () => ({
    token: getToken(),
    userInfo: null,
    userId: null
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
        // 响应现在包含 token 和 user 对象
        this.token = response.token || response
        setToken(this.token)
        
        // 从响应中获取用户信息
        if (response.user) {
          this.userInfo = response.user
          this.userId = response.user.id
        }
        
        return Promise.resolve(response)
      } catch (error) {
        return Promise.reject(error)
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