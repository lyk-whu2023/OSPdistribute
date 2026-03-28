import request from '@/utils/request'

/**
 * 获取店铺列表
 * @param {Object} params - 查询参数
 * @returns {Promise}
 */
export function getStores(params = {}) {
  return request({
    url: '/api/stores',
    method: 'get',
    params
  })
}

/**
 * 获取店铺详情
 * @param {number} id - 店铺 ID
 * @returns {Promise}
 */
export function getStore(id) {
  return request({
    url: `/api/stores/${id}`,
    method: 'get'
  })
}

/**
 * 创建店铺
 * @param {Object} data - 店铺信息
 * @returns {Promise}
 */
export function createStore(data) {
  return request({
    url: '/api/stores',
    method: 'post',
    data
  })
}

/**
 * 更新店铺
 * @param {number} id - 店铺 ID
 * @param {Object} data - 店铺信息
 * @returns {Promise}
 */
export function updateStore(id, data) {
  return request({
    url: `/api/stores/${id}`,
    method: 'put',
    data
  })
}

/**
 * 删除店铺
 * @param {number} id - 店铺 ID
 * @returns {Promise}
 */
export function deleteStore(id) {
  return request({
    url: `/api/stores/${id}`,
    method: 'delete'
  })
}

/**
 * 获取店铺商品
 * @param {number} storeId - 店铺 ID
 * @param {Object} params - 查询参数
 * @returns {Promise}
 */
export function getStoreProducts(storeId, params = {}) {
  return request({
    url: `/api/stores/${storeId}/products`,
    method: 'get',
    params
  })
}

/**
 * 获取店铺分类
 * @param {number} storeId - 店铺 ID
 * @returns {Promise}
 */
export function getStoreCategories(storeId) {
  return request({
    url: `/api/stores/${storeId}/categories`,
    method: 'get'
  })
}
