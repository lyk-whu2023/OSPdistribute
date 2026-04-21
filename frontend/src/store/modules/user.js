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

export const useUserStore = defineStore('user', {
  state: () => {
    const token = getToken()
    const parsedToken = parseToken(token)
    return {
      token: token,
      userInfo: null,
      userId: parsedToken?.sub || parsedToken?.userId || null
    }
  },

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
        } else {
          // 如果响应中没有用户信息，从 token 中解析
          const parsedToken = parseToken(this.token)
          if (parsedToken) {
            this.userId = parsedToken.sub || parsedToken.userId
          }
        }
        
        return Promise.resolve(response)
      } catch (error) {
        console.error('登录失败:', error)
        return Promise.reject(error)
      }
    },

    async initUserInfo() {
      // 页面刷新时初始化用户信息
      const token = getToken()
      if (token && !this.userInfo) {
        try {
          // 先从 token 中解析用户 ID
          const parsedToken = parseToken(token)
          if (parsedToken) {
            const userId = parsedToken.sub || parsedToken.userId
            if (userId) {
              this.userId = userId
              // 调用后端接口获取完整用户信息
              const response = await getUserInfo(userId)
              if (response) {
                this.userInfo = response
                console.log('用户信息已恢复:', this.userInfo)
              }
            }
          }
        } catch (error) {
          console.error('初始化用户信息失败:', error)
          // 如果获取用户信息失败，清除 token
          this.token = null
          this.userInfo = null
          this.userId = null
          removeToken()
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