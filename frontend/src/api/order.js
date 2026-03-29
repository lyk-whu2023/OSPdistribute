import request from '@/utils/request'

export function getCartItems(userId) {
  return request({
    url: `/api/cart/${userId}/items`,
    method: 'get'
  })
}

export function addToCart(userId, productId, skuId, quantity, price, productName = '') {
  return request({
    url: `/api/cart/${userId}/items`,
    method: 'post',
    data: { productId, skuId, quantity, price, productName }
  })
}

export function updateCartItem(cartItemId, quantity) {
  return request({
    url: `/api/cart/items/${cartItemId}`,
    method: 'put',
    data: { quantity }
  })
}

export function removeCartItem(cartItemId) {
  return request({
    url: `/api/cart/items/${cartItemId}`,
    method: 'delete'
  })
}

export function clearCart(userId) {
  return request({
    url: `/api/cart/${userId}`,
    method: 'delete'
  })
}

export function getUserOrders(userId, params = {}) {
  return request({
    url: `/api/orders/user/${userId}`,
    method: 'get',
    params: {
      page: params.page || 1,
      size: params.size || 10,
      ...params
    }
  })
}

export function getOrder(id) {
  return request({
    url: `/api/orders/${id}`,
    method: 'get'
  })
}

export function getOrderByOrderNo(orderNo) {
  return request({
    url: `/api/orders/no/${orderNo}`,
    method: 'get'
  })
}

export function createOrder(data) {
  console.log('=== 调用 createOrder API ===')
  console.log('请求数据:', data)
  return request({
    url: '/api/orders',
    method: 'post',
    data: {
      userId: data.userId,
      addressId: data.addressId,
      storeId: data.storeId,
      items: data.items
    }
  }).then(response => {
    console.log('订单创建成功:', response)
    return response
  }).catch(error => {
    console.error('=== 订单创建失败 ===')
    console.error('错误状态码:', error.response?.status)
    console.error('错误信息:', error.response?.data)
    console.error('请求配置:', error.config)
    throw error
  })
}

export function updateOrderStatus(id, status) {
  return request({
    url: `/api/orders/${id}/status`,
    method: 'put',
    data: { status }
  })
}

export function cancelOrder(id) {
  return request({
    url: `/api/orders/${id}/cancel`,
    method: 'post'
  })
}

export function getOrderDetail(id) {
  return request({
    url: `/api/orders/${id}/detail`,
    method: 'get'
  })
}
