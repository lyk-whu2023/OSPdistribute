import request from '@/utils/request'

/**
 * 用户登录
 * @param {Object} data - 登录信息
 * @param {string} data.username - 用户名
 * @param {string} data.password - 密码
 * @returns {Promise}
 */
export function login(data) {
  return request({
    url: '/api/auth/login',
    method: 'post',
    data
  })
}

/**
 * 用户注册
 * @param {Object} data - 注册信息
 * @param {string} data.username - 用户名
 * @param {string} data.password - 密码
 * @param {string} data.phone - 手机号
 * @param {string} data.email - 邮箱
 * @returns {Promise}
 */
export function register(data) {
  return request({
    url: '/api/auth/register',
    method: 'post',
    data
  })
}

/**
 * 用户登出
 * @returns {Promise}
 */
export function logout() {
  return request({
    url: '/api/auth/logout',
    method: 'post'
  })
}

/**
 * 获取用户信息
 * @param {number} id - 用户 ID
 * @returns {Promise}
 */
export function getUserInfo(id) {
  return request({
    url: `/api/users/${id}`,
    method: 'get'
  })
}

/**
 * 更新用户信息
 * @param {number} id - 用户 ID
 * @param {Object} data - 用户信息
 * @returns {Promise}
 */
export function updateUserInfo(id, data) {
  return request({
    url: `/api/users/${id}`,
    method: 'put',
    data
  })
}

/**
 * 获取用户地址列表
 * @param {number} userId - 用户 ID
 * @returns {Promise}
 */
export function getUserAddresses(userId) {
  return request({
    url: `/api/addresses/user/${userId}`,
    method: 'get'
  })
}

/**
 * 获取地址详情
 * @param {number} id - 地址 ID
 * @returns {Promise}
 */
export function getAddress(id) {
  return request({
    url: `/api/addresses/${id}`,
    method: 'get'
  })
}

/**
 * 创建地址
 * @param {number} userId - 用户 ID
 * @param {Object} data - 地址信息
 * @returns {Promise}
 */
export function createAddress(userId, data) {
  return request({
    url: `/api/addresses/user/${userId}`,
    method: 'post',
    data
  })
}

/**
 * 更新地址
 * @param {number} id - 地址 ID
 * @param {Object} data - 地址信息
 * @returns {Promise}
 */
export function updateAddress(id, data) {
  return request({
    url: `/api/addresses/${id}`,
    method: 'put',
    data
  })
}

/**
 * 删除地址
 * @param {number} id - 地址 ID
 * @returns {Promise}
 */
export function deleteAddress(id) {
  return request({
    url: `/api/addresses/${id}`,
    method: 'delete'
  })
}

/**
 * 设置默认地址
 * @param {number} userId - 用户 ID
 * @param {number} addressId - 地址 ID
 * @returns {Promise}
 */
export function setDefaultAddress(userId, addressId) {
  return request({
    url: `/api/addresses/user/${userId}/default/${addressId}`,
    method: 'put'
  })
}