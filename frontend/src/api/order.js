import request from '@/utils/request'

export function getCartItems(userId) {
  return request({
    url: `/api/cart/user/${userId}`,
    method: 'get'
  })
}

export function addToCart(userId, productId, skuId, quantity, price) {
  return request({
    url: `/api/cart/user/${userId}/items`,
    method: 'post',
    params: { productId, skuId, quantity, price }
  })
}

export function updateCartItem(cartItemId, quantity) {
  return request({
    url: `/api/cart/items/${cartItemId}`,
    method: 'put',
    params: { quantity }
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
    url: `/api/cart/user/${userId}`,
    method: 'delete'
  })
}

export function getUserOrders(userId, params = {}) {
  return request({
    url: `/api/orders/user/${userId}`,
    method: 'get',
    params
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
  return request({
    url: '/api/orders',
    method: 'post',
    params: {
      userId: data.userId,
      addressId: data.addressId,
      storeId: data.storeId
    },
    data: data.items
  })
}

export function updateOrderStatus(id, status) {
  return request({
    url: `/api/orders/${id}/status`,
    method: 'put',
    params: { status }
  })
}

export function cancelOrder(id) {
  return request({
    url: `/api/orders/${id}/cancel`,
    method: 'put'
  })
}
