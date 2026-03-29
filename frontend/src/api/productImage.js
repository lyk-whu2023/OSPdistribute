import request from '@/utils/request'

/**
 * 获取商品图片列表
 * @param {number} productId - 商品 ID
 * @returns {Promise}
 */
export function getProductImages(productId) {
  return request({
    url: `/api/product-images/${productId}`,
    method: 'get'
  })
}

/**
 * 获取图片详情
 * @param {number} id - 图片 ID
 * @returns {Promise}
 */
export function getProductImage(id) {
  return request({
    url: `/api/product-images/${id}/detail`,
    method: 'get'
  })
}

/**
 * 添加商品图片
 * @param {Object} data - 图片信息
 * @param {number} data.productId - 商品 ID
 * @param {string} data.imageUrl - 图片 URL
 * @param {boolean} data.isMain - 是否为主图
 * @returns {Promise}
 */
export function addProductImage(data) {
  return request({
    url: '/api/product-images',
    method: 'post',
    data
  })
}

/**
 * 更新商品图片
 * @param {number} id - 图片 ID
 * @param {Object} data - 图片信息
 * @returns {Promise}
 */
export function updateProductImage(id, data) {
  return request({
    url: `/api/product-images/${id}`,
    method: 'put',
    data
  })
}

/**
 * 删除商品图片
 * @param {number} id - 图片 ID
 * @returns {Promise}
 */
export function deleteProductImage(id) {
  return request({
    url: `/api/product-images/${id}`,
    method: 'delete'
  })
}

/**
 * 设置商品主图
 * @param {number} productId - 商品 ID
 * @param {number} imageId - 图片 ID
 * @returns {Promise}
 */
export function setMainImage(productId, imageId) {
  return request({
    url: `/api/product-images/${productId}/main/${imageId}`,
    method: 'put'
  })
}
