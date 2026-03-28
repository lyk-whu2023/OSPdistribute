<template>
  <div class="product-detail-view">
    <el-row :gutter="20">
      <!-- 左侧图片 -->
      <el-col :span="12">
        <el-card>
          <el-carousel height="500px" v-if="product.images?.length > 0 || product.id">
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
          <div class="product-info">
            <h1 class="product-title">{{ product.name }}</h1>
            <p class="product-desc">{{ product.description }}</p>
            
            <div class="price-section">
              <div class="price">
                <span class="label">价格：</span>
                <span class="current-price">¥{{ product.price }}</span>
                <span v-if="product.originalPrice" class="original-price">¥{{ product.originalPrice }}</span>
              </div>
            </div>

            <div class="stock-section">
              <span class="label">库存：</span>
              <span class="stock">{{ product.stock }} 件</span>
            </div>

            <div class="sales-section">
              <span class="label">销量：</span>
              <span class="sales">{{ product.sales }} 件</span>
            </div>

            <div class="category-section">
              <span class="label">分类：</span>
              <el-tag>{{ product.categoryName }}</el-tag>
            </div>

            <div class="action-section">
              <el-input-number v-model="quantity" :min="1" :max="product.stock" />
              <el-button type="primary" size="large" @click="addToCart" :disabled="product.stock === 0">
                加入购物车
              </el-button>
              <el-button type="danger" size="large" @click="buyNow" :disabled="product.stock === 0">
                立即购买
              </el-button>
            </div>
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
          <div class="product-detail" v-html="product.detail"></div>
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
import { generateMultipleProductImages } from '@/utils/image'
import { ElMessage } from 'element-plus'

const route = useRoute()
const router = useRouter()
const productStore = useProductStore()
const userStore = useUserStore()

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
  const productId = route.params.id
  if (productId) {
    try {
      const response = await productStore.loadProduct(productId)
      product.value = response
    } catch (error) {
      ElMessage.error('加载商品详情失败')
    }
  }
})

const addToCart = () => {
  if (!userStore.isLoggedIn) {
    ElMessage.warning('请先登录')
    router.push('/auth')
    return
  }
  
  // 这里应该调用购物车API
  ElMessage.success('已加入购物车')
}

const buyNow = () => {
  if (!userStore.isLoggedIn) {
    ElMessage.warning('请先登录')
    router.push('/auth')
    return
  }
  
  // 跳转到订单提交页面
  router.push({
    name: 'OrderSubmit',
    query: {
      products: JSON.stringify([{
        id: product.value.id,
        name: product.value.name,
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
  max-width: 1200px;
  margin: 0 auto;
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

.current-price {
  font-size: 24px;
  color: #ff4d4f;
  font-weight: bold;
}

.original-price {
  font-size: 16px;
  color: #999;
  text-decoration: line-through;
}

.stock-section,
.sales-section,
.category-section {
  margin: 15px 0;
  display: flex;
  align-items: center;
  gap: 10px;
}

.label {
  font-weight: bold;
  color: #333;
  width: 60px;
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
}

.product-detail img {
  max-width: 100%;
  height: auto;
}
</style>