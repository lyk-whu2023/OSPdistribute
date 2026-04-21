import axios from 'axios'
import { ElMessage } from 'element-plus'

const request = axios.create({
  baseURL: 'http://localhost:9999',
  timeout: 10000
})

let token = null

export function setToken(newToken) {
  token = newToken
  localStorage.setItem('token', newToken)
}

export function getToken() {
  if (!token) {
    token = localStorage.getItem('token')
  }
  return token
}

export function removeToken() {
  token = null
  localStorage.removeItem('token')
}

request.interceptors.request.use(
  config => {
    const currentToken = getToken()
    if (currentToken) {
      config.headers.Authorization = `Bearer ${currentToken}`
    }
    return config
  },
  error => {
    return Promise.reject(error)
  }
)

request.interceptors.response.use(
  response => {
    return response.data
  },
  error => {
    if (error.response) {
      switch (error.response.status) {
        case 401:
          console.error('=== 401 错误 ===')
          console.error('请求 URL:', error.config.url)
          console.error('请求方法:', error.config.method)
          console.error('请求头:', error.config.headers)
          console.error('响应数据:', error.response.data)
          ElMessage.error('未授权，请登录')
          // 清除 token 并跳转到登录页
          removeToken()
          // 发送 token 失效事件
          if (typeof window !== 'undefined') {
            window.dispatchEvent(new CustomEvent('token-expired'))
          }
          break
        case 403:
          ElMessage.error('拒绝访问')
          break
        case 404:
          ElMessage.error('请求错误，未找到该资源')
          break
        case 500:
          ElMessage.error('服务器错误')
          break
        default:
          ElMessage.error(error.response.data.message || '请求失败')
      }
    } else {
      ElMessage.error('网络错误')
    }
    return Promise.reject(error)
  }
)

export default request