import { defineStore } from 'pinia'
import { getCartItems, addToCart, updateCartItem, removeCartItem, clearCart } from '@/api/order'

export const useCartStore = defineStore('cart', {
  state: () => ({
    items: [],
    total: 0
  }),

  getters: {
    itemCount: (state) => state.items.length,
    totalPrice: (state) => {
      return state.items.reduce((sum, item) => {
        return sum + item.price * item.quantity
      }, 0)
    }
  },

  actions: {
    async loadCart(userId) {
      try {
        const response = await getCartItems(userId)
        this.items = response || []
        this.updateTotal()
      } catch (error) {
        console.error('加载购物车失败', error)
        this.items = []
      }
    },

    async addToCart(userId, productId, skuId, quantity, price) {
      try {
        await addToCart(userId, productId, skuId, quantity, price)
        await this.loadCart(userId)
      } catch (error) {
        throw error
      }
    },

    async updateQuantity(cartItemId, quantity) {
      try {
        await updateCartItem(cartItemId, quantity)
        await this.loadCart()
      } catch (error) {
        throw error
      }
    },

    async removeItem(cartItemId) {
      try {
        await removeCartItem(cartItemId)
        this.items = this.items.filter(item => item.id !== cartItemId)
        this.updateTotal()
      } catch (error) {
        throw error
      }
    },

    async clear(userId) {
      try {
        await clearCart(userId)
        this.items = []
        this.total = 0
      } catch (error) {
        throw error
      }
    },

    updateTotal() {
      this.total = this.items.reduce((sum, item) => {
        return sum + item.price * item.quantity
      }, 0)
    }
  }
})
