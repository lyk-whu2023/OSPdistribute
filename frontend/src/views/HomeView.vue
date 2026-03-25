<template>
  <div class="home-view">
    <!-- 轮播图 -->
    <el-carousel height="400px" class="banner-carousel">
      <el-carousel-item>
        <img src="/banner1.jpg" alt="轮播图1" />
      </el-carousel-item>
      <el-carousel-item>
        <img src="/banner2.jpg" alt="轮播图2" />
      </el-carousel-item>
      <el-carousel-item>
        <img src="/banner3.jpg" alt="轮播图3" />
      </el-carousel-item>
    </el-carousel>

    <!-- 分类导航 -->
    <div class="category-section">
      <el-row :gutter="20">
        <el-col :span="6" v-for="category in topCategories" :key="category.id">
          <el-card class="category-card" @click="goToCategory(category.id)">
            <img :src="category.image || '/category-placeholder.png'" :alt="category.name" />
            <h3>{{ category.name }}</h3>
          </el-card>
        </el-col>
      </el-row>
    </div>

    <!-- 秒杀专区 -->
    <div class="seckill-section" v-if="seckillProducts.length > 0">
      <div class="section-header">
        <h2>限时秒杀</h2>
        <el-button type="primary" link @click="goToSeckill">查看更多</el-button>
      </div>
      <el-row :gutter="20">
        <el-col :span="6" v-for="product in seckillProducts" :key="product.id">
          <ProductCard
            :product="product"
            :show-seckill="true"
            @add-to-cart="addToCart"
            @buy-now="buyNow"
          />
        </el-col>
      </el-row>
    </div>

    <!-- 推荐商品 -->
    <div class="recommend-section">
      <div class="section-header">
        <h2>热门推荐</h2>
        <el-button type="primary" link @click="goToProducts">查看更多</el-button>
      </div>
      <el-row :gutter="20">
        <el-col :span="6" v-for="product in recommendProducts" :key="product.id">
          <ProductCard
            :product="product"
            @add-to-cart="addToCart"
            @buy-now="buyNow"
          />
        </el-col>
      </el-row>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useProductStore } from '@/store/modules/product'
import { useUserStore } from '@/store/modules/user'
import ProductCard from '@/components/product/ProductCard.vue'
import { ElMessage } from 'element-plus'

const router = useRouter()
const productStore = useProductStore()
const userStore = useUserStore()

const topCategories = ref([])
const seckillProducts = ref([])
const recommendProducts = ref([])

onMounted(async () => {
  await loadCategories()
  await loadRecommendProducts()
  await loadSeckillProducts()
})

const loadCategories = async () => {
  try {
    await productStore.loadCategories()
    topCategories.value = productStore.categories.slice(0, 4)
  } catch (error) {
    console.error('加载分类失败', error)
  }
}

const loadRecommendProducts = async () => {
  try {
    const response = await productStore.loadProducts({
      page: 1,
      size: 8,
      sort: 'sales',
      order: 'desc'
    })
    recommendProducts.value = productStore.products
  } catch (error) {
    console.error('加载推荐商品失败', error)
  }
}

const loadSeckillProducts = async () => {
  try {
    await productStore.loadSeckillActivities()
    if (productStore.seckillActivities.length > 0) {
      await productStore.loadSeckillProducts(productStore.seckillActivities[0].id)
      seckillProducts.value = productStore.seckillProducts.slice(0, 4)
    }
  } catch (error) {
    console.error('加载秒杀商品失败', error)
  }
}

const goToCategory = (categoryId) => {
  router.push({
    path: '/products',
    query: { categoryId }
  })
}

const goToSeckill = () => {
  router.push('/seckill')
}

const goToProducts = () => {
  router.push('/products')
}

const addToCart = (product) => {
  if (!userStore.isLoggedIn) {
    ElMessage.warning('请先登录')
    router.push('/auth')
    return
  }
  // 这里应该调用购物车API
  ElMessage.success(`已将 ${product.name} 加入购物车`)
}

const buyNow = (product) => {
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
        id: product.id,
        name: product.name,
        price: product.seckillPrice || product.price,
        quantity: 1,
        image: product.images?.[0]?.imageUrl
      }])
    }
  })
}
</script>

<style scoped>
.home-view {
  padding: 20px;
  max-width: 1200px;
  margin: 0 auto;
}

.banner-carousel {
  margin-bottom: 30px;
}

.banner-carousel img {
  width: 100%;
  height: 400px;
  object-fit: cover;
}

.category-section {
  margin-bottom: 40px;
}

.category-card {
  text-align: center;
  cursor: pointer;
  transition: all 0.3s ease;
}

.category-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.1);
}

.category-card img {
  width: 100%;
  height: 150px;
  object-fit: cover;
  margin-bottom: 10px;
}

.category-card h3 {
  margin: 0;
  font-size: 16px;
  color: #333;
}

.seckill-section,
.recommend-section {
  margin-bottom: 40px;
}

.section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.section-header h2 {
  margin: 0;
  font-size: 24px;
  color: #333;
}
</style>