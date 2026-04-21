<template>
  <div class="product-detail-view" v-loading="loading">
    <!-- 面包屑导航 -->
    <el-card class="breadcrumb-card">
      <el-breadcrumb separator="/">
        <el-breadcrumb-item>首页</el-breadcrumb-item>
        <el-breadcrumb-item @click="$router.push('/products')">商品列表</el-breadcrumb-item>
        <el-breadcrumb-item>{{ product.name }}</el-breadcrumb-item>
      </el-breadcrumb>
    </el-card>

    <el-row :gutter="20">
      <!-- 左侧图片 -->
      <el-col :span="12">
        <el-card>
          <template #header>
            <span>商品图片</span>
          </template>
          <el-carousel height="500px" v-if="displayImages.length > 0">
            <el-carousel-item v-for="image in displayImages" :key="image.id || image.imageUrl">
              <img :src="image.imageUrl" class="product-image" alt="商品图片" />
            </el-carousel-item>
          </el-carousel>
          <div v-else class="no-image">
            <el-empty description="暂无图片" />
          </div>
        </el-card>
      </el-col>

      <!-- 右侧信息 -->
      <el-col :span="12">
        <el-card>
          <template #header>
            <span>商品信息</span>
          </template>
          <div class="product-info">
            <h1 class="product-title">{{ product.name }}</h1>
            <p class="product-desc">{{ product.description || '暂无描述' }}</p>
            
            <div class="price-section">
              <div class="price">
                <span class="label">价格：</span>
                <span class="current-price">¥{{ product.price }}</span>
              </div>
            </div>

            <div class="stock-section">
              <span class="label">库存：</span>
              <el-tag :type="product.stock > 0 ? 'success' : 'danger'">
                {{ product.stock > 0 ? product.stock + ' 件' : '缺货' }}
              </el-tag>
            </div>

            <div class="sales-section">
              <span class="label">销量：</span>
              <span class="sales">{{ product.sales || 0 }} 件</span>
            </div>

            <div class="category-section" v-if="product.categoryName">
              <span class="label">分类：</span>
              <el-tag>{{ product.categoryName }}</el-tag>
            </div>

            <div class="action-section" v-if="product.stock > 0">
              <el-input-number v-model="quantity" :min="1" :max="product.stock" size="large" />
              <el-button type="primary" size="large" @click="addToCart">
                加入购物车
              </el-button>
              <el-button type="danger" size="large" @click="buyNow">
                立即购买
              </el-button>
            </div>
            <el-alert
              v-else
              type="warning"
              title="该商品已缺货"
              :closable="false"
              show-icon
            />
          </div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 商品详情 -->
    <el-row :gutter="20" class="detail-section">
      <el-col :span="24">
        <el-card>
          <template #header>
            <span>商品详情</span>
          </template>
          <div class="product-detail" v-if="product.detail" v-html="product.detail"></div>
          <el-empty v-else description="暂无商品详情" />
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useProductStore } from '@/store/modules/product'
import { useUserStore } from '@/store/modules/user'
import { useCartStore } from '@/store/modules/cart'
import { generateMultipleProductImages } from '@/utils/image'
import { ElMessage } from 'element-plus'

const route = useRoute()
const router = useRouter()
const productStore = useProductStore()
const userStore = useUserStore()
const cartStore = useCartStore()

const loading = ref(false)
const quantity = ref(1)
const product = ref({})

const displayImages = computed(() => {
  if (product.value.images && product.value.images.length > 0) {
    return product.value.images
  }
  // 如果没有图片，生成随机图片
  if (product.value.id) {
    return generateMultipleProductImages(3, 800, 800, product.value.id)
  }
  return []
})

onMounted(async () => {
  loading.value = true
  const productId = route.params.id
  if (productId) {
    try {
      const response = await productStore.loadProduct(productId)
      product.value = response
    } catch (error) {
      ElMessage.error('加载商品详情失败')
    }
  }
  loading.value = false
})

const addToCart = async () => {
  if (!userStore.isLoggedIn) {
    ElMessage.warning('请先登录')
    router.push('/auth')
    return
  }
  
  try {
    await cartStore.addToCart(
      userStore.userId,
      product.value.id,
      product.value.id,
      quantity.value,
      product.value.price,
      product.value.name
    )
    ElMessage.success('已加入购物车')
  } catch (error) {
    ElMessage.error('加入购物车失败')
  }
}

const buyNow = () => {
  if (!userStore.isLoggedIn) {
    ElMessage.warning('请先登录')
    router.push('/auth')
    return
  }
  
  // 跳转到订单提交页面
  router.push({
    path: '/order/submit',
    query: {
      items: JSON.stringify([{
        productId: product.value.id,
        skuId: product.value.id,
        productName: product.value.name,
        price: product.value.price,
        quantity: quantity.value,
        image: product.value.images?.[0]?.imageUrl
      }])
    }
  })
}
</script>

<style scoped>
.product-detail-view {
  padding: 20px;
  max-width: 1400px;
  margin: 0 auto;
}

.breadcrumb-card {
  margin-bottom: 20px;
}

.product-image {
  width: 100%;
  height: 500px;
  object-fit: cover;
}

.no-image {
  display: flex;
  align-items: center;
  justify-content: center;
  height: 400px;
  background-color: #f5f5f5;
}

.product-info {
  padding: 20px;
}

.product-title {
  font-size: 24px;
  font-weight: bold;
  margin: 0 0 10px;
  color: #333;
}

.product-desc {
  font-size: 14px;
  color: #666;
  margin: 0 0 20px;
  line-height: 1.6;
}

.price-section {
  margin: 20px 0;
  padding: 20px;
  background-color: #f5f5f5;
  border-radius: 4px;
}

.price {
  display: flex;
  align-items: center;
  gap: 10px;
}

.label {
  font-weight: bold;
  color: #333;
  width: 60px;
}

.current-price {
  font-size: 24px;
  color: #ff4d4f;
  font-weight: bold;
}

.stock-section,
.sales-section,
.category-section {
  margin: 15px 0;
  display: flex;
  align-items: center;
  gap: 10px;
}

.action-section {
  margin-top: 30px;
  display: flex;
  align-items: center;
  gap: 15px;
}

.detail-section {
  margin-top: 20px;
}

.product-detail {
  line-height: 1.8;
  color: #333;
  min-height: 200px;
}

.product-detail img {
  max-width: 100%;
  height: auto;
}
</style>