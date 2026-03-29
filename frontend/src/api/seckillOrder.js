import request from '@/utils/request'

/**
 * 创建秒杀订单
 * @param {Object} params - 订单参数
 * @param {number} params.seckillProductId - 秒杀商品 ID
 * @param {number} params.userId - 用户 ID
 * @param {number} params.addressId - 地址 ID
 * @returns {Promise}
 */
export function createSeckillOrder(params) {
  return request({
    url: '/api/seckill-orders',
    method: 'post',
    params
  })
}

/**
 * 获取秒杀订单详情
 * @param {number} id - 订单 ID
 * @returns {Promise}
 */
export function getSeckillOrder(id) {
  return request({
    url: `/api/seckill-orders/${id}`,
    method: 'get'
  })
}

/**
 * 更新秒杀订单状态
 * @param {number} id - 订单 ID
 * @param {string} status - 订单状态
 * @returns {Promise}
 */
export function updateSeckillOrderStatus(id, status) {
  return request({
    url: `/api/seckill-orders/${id}/status`,
    method: 'put',
    params: { status }
  })
}
