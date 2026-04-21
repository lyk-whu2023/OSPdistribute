import request from '@/utils/request'

/**
 * 获取商品分类列表
 * @returns {Promise}
 */
export function getCategories() {
  return request({
    url: '/api/categories',
    method: 'get'
  })
}

/**
 * 获取指定级别分类
 * @param {number} level - 分类级别
 * @returns {Promise}
 */
export function getCategoriesByLevel(level) {
  return request({
    url: `/api/categories/level/${level}`,
    method: 'get'
  })
}

/**
 * 获取子分类
 * @param {number} parentId - 父分类 ID
 * @returns {Promise}
 */
export function getChildCategories(parentId) {
  return request({
    url: `/api/categories/parent/${parentId}`,
    method: 'get'
  })
}

/**
 * 获取分类详情
 * @param {number} id - 分类 ID
 * @returns {Promise}
 */
export function getCategory(id) {
  return request({
    url: `/api/categories/${id}`,
    method: 'get'
  })
}

/**
 * 创建分类
 * @param {Object} data - 分类信息
 * @returns {Promise}
 */
export function createCategory(data) {
  return request({
    url: '/api/categories',
    method: 'post',
    data
  })
}

/**
 * 更新分类
 * @param {number} id - 分类 ID
 * @param {Object} data - 分类信息
 * @returns {Promise}
 */
export function updateCategory(id, data) {
  return request({
    url: `/api/categories/${id}`,
    method: 'put',
    data
  })
}

/**
 * 删除分类
 * @param {number} id - 分类 ID
 * @returns {Promise}
 */
export function deleteCategory(id) {
  return request({
    url: `/api/categories/${id}`,
    method: 'delete'
  })
}

/**
 * 获取商品列表（分页）
 * @param {Object} params - 查询参数 {page, size}
 * @returns {Promise}
 */
export function getProducts(params) {
  return request({
    url: '/api/products',
    method: 'get',
    params: {
      page: params.page || 1,
      size: params.size || 10
    }
  })
}

/**
 * 搜索商品（支持关键词和分类过滤）
 * @param {Object} params - 查询参数 {page, size, keyword, categoryId}
 * @returns {Promise}
 */
export function searchProducts(params) {
  return request({
    url: '/api/products/search',
    method: 'get',
    params: {
      page: params.page || 1,
      size: params.size || 10,
      keyword: params.keyword || '',
      categoryId: params.categoryId || null
    }
  })
}

/**
 * 获取商品详情
 * @param {number} id - 商品 ID
 * @returns {Promise}
 */
export function getProduct(id) {
  return request({
    url: `/api/products/${id}`,
    method: 'get'
  })
}

/**
 * 创建商品
 * @param {Object} data - 商品信息
 * @returns {Promise}
 */
export function createProduct(data) {
  return request({
    url: '/api/products',
    method: 'post',
    data
  })
}

/**
 * 更新商品
 * @param {number} id - 商品 ID
 * @param {Object} data - 商品信息
 * @returns {Promise}
 */
export function updateProduct(id, data) {
  return request({
    url: `/api/products/${id}`,
    method: 'put',
    data
  })
}

/**
 * 删除商品
 * @param {number} id - 商品 ID
 * @returns {Promise}
 */
export function deleteProduct(id) {
  return request({
    url: `/api/products/${id}`,
    method: 'delete'
  })
}

/**
 * 更新商品库存
 * @param {number} productId - 商品 ID
 * @param {number} quantity - 库存变化数量
 * @returns {Promise}
 */
export function updateProductStock(productId, quantity) {
  return request({
    url: `/api/products/${productId}/stock/${quantity}`,
    method: 'put'
  })
}

/**
 * 更新商品状态（上下架）
 * @param {number} id - 商品 ID
 * @param {number} status - 状态 (1: 上架，0: 下架)
 * @returns {Promise}
 */
export function updateProductStatus(id, status) {
  return request({
    url: `/api/products/${id}/status`,
    method: 'put',
    params: { status }
  })
}

/**
 * 获取秒杀活动列表
 * @returns {Promise}
 */
export function getSeckillActivities() {
  return request({
    url: '/api/seckill/activities',
    method: 'get'
  })
}

/**
 * 获取秒杀活动商品
 * @param {number} activityId - 活动 ID
 * @returns {Promise}
 */
export function getSeckillProducts(activityId) {
  return request({
    url: `/api/seckill/activities/${activityId}/products`,
    method: 'get'
  })
}

/**
 * 获取秒杀商品详情
 * @param {number} id - 秒杀商品 ID
 * @returns {Promise}
 */
export function getSeckillProduct(id) {
  return request({
    url: `/api/seckill/products/${id}`,
    method: 'get'
  })
}

/**
 * 秒杀抢购
 * @param {number} id - 秒杀商品 ID
 * @param {number} userId - 用户 ID
 * @returns {Promise}
 */
export function purchaseSeckill(id, userId) {
  return request({
    url: `/api/seckill/products/${id}/purchase`,
    method: 'post',
    params: { userId }
  })
}

/**
 * 创建秒杀商品
 * @param {Object} data - 秒杀商品信息
 * @returns {Promise}
 */
export function createSeckillProduct(data) {
  return request({
    url: '/api/seckill/products',
    method: 'post',
    data
  })
}