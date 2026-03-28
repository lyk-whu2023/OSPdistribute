import { defineStore } from 'pinia'
import { getCategories, getProducts, getProduct, getSeckillActivities, getSeckillProducts } from '@/api/product'

export const useProductStore = defineStore('product', {
  state: () => ({
    categories: [],
    products: [],
    currentProduct: null,
    searchKeyword: '',
    selectedCategory: null,
    seckillActivities: [],
    seckillProducts: []
  }),

  getters: {
    categoryTree: (state) => {
      const buildTree = (parentId = null) => {
        return state.categories
          .filter(c => c.parentId === parentId)
          .map(c => ({
            ...c,
            children: buildTree(c.id)
          }))
      }
      return buildTree()
    }
  },

  actions: {
    async loadCategories() {
      try {
        const response = await getCategories()
        this.categories = response
      } catch (error) {
        console.error('加载分类失败', error)
      }
    },

    async loadProducts(params = {}) {
      try {
        const response = await getProducts(params)
        this.products = response.records || response
        return response
      } catch (error) {
        console.error('加载商品失败', error)
        throw error
      }
    },

    async loadProduct(id) {
      try {
        const response = await getProduct(id)
        this.currentProduct = response
        return response
      } catch (error) {
        console.error('加载商品详情失败', error)
        throw error
      }
    },

    setSearchKeyword(keyword) {
      this.searchKeyword = keyword
    },

    setSelectedCategory(category) {
      this.selectedCategory = category
    },

    async loadSeckillActivities() {
      try {
        const response = await getSeckillActivities()
        this.seckillActivities = response
      } catch (error) {
        console.error('加载秒杀活动失败', error)
      }
    },

    async loadSeckillProducts(activityId) {
      try {
        const response = await getSeckillProducts(activityId)
        this.seckillProducts = response
      } catch (error) {
        console.error('加载秒杀商品失败', error)
      }
    }
  }
})