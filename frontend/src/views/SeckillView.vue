<template>
  <div class="seckill-view">
    <!-- 秒杀活动选择 -->
    <el-card class="activity-card">
      <template #header>
        <div class="activity-header">
          <span>限时秒杀</span>
          <div class="activity-timer" v-if="currentActivity">
            <span>距离结束：</span>
            <span class="timer">{{ formatTime(countdown) }}</span>
          </div>
        </div>
      </template>
      
      <div class="activity-tabs">
        <el-tabs v-model="activeActivityId" @tab-click="handleActivityChange">
          <el-tab-pane
            v-for="activity in seckillActivities"
            :key="activity.id"
            :label="activity.name"
            :name="activity.id"
          >
            <div class="activity-info">
              <span>开始时间：{{ formatDateTime(activity.startTime) }}</span>
              <span>结束时间：{{ formatDateTime(activity.endTime) }}</span>
            </div>
          </el-tab-pane>
        </el-tabs>
      </div>
    </el-card>

    <!-- 秒杀商品列表 -->
    <el-card class="seckill-products">
      <el-row :gutter="20">
        <el-col
          v-for="product in seckillProducts"
          :key="product.id"
          :xs="12"
          :sm="8"
          :md="6"
          :lg="4"
        >
          <el-card class="seckill-product-card" shadow="hover">
            <div class="product-image-wrapper">
              <img
                :src="product.images?.[0]?.imageUrl || '/placeholder.png'"
                class="product-image"
                alt="商品图片"
              />
              <div class="seckill-badge">秒杀</div>
            </div>
            
            <div class="product-info">
              <h3 class="product-name">{{ product.name }}</h3>
              <p class="product-desc">{{ product.description }}</p>
              
              <div class="price-section">
                <div class="seckill-price">
                  <span class="label">秒杀价：</span>
                  <span class="price">¥{{ product.seckillPrice }}</span>
                </div>
                <div class="original-price">
                  <span class="label">原价：</span>
                  <span class="price">¥{{ product.originalPrice }}</span>
                </div>
                <div class="discount">
                  <span class="label">折扣：</span>
                  <span class="discount-rate">{{ calculateDiscount(product) }}</span>
                </div>
              </div>
              
              <div class="stock-section">
                <span class="label">剩余：</span>
                <span class="stock">{{ product.seckillStock }} 件</span>
              </div>
              
              <div class="progress-section">
                <el-progress
                  :percentage="calculateProgress(product)"
                  :color="progressColor"
                  :stroke-width="6"
                />
                <span class="progress-text">已售{{ product.sales }}件</span>
              </div>
              
              <div class="action-section">
                <el-button
                  type="danger"
                  size="large"
                  @click="handlePurchaseSeckill(product)"
                  :disabled="product.seckillStock === 0 || !canPurchase"
                  :loading="purchasing[product.id]"
                >
                  {{ product.seckillStock === 0 ? '已售罄' : '立即抢购' }}
                </el-button>
              </div>
            </div>
          </el-card>
        </el-col>
      </el-row>
      
      <!-- 空状态 -->
      <el-empty
        v-if="seckillProducts.length === 0"
        description="暂无秒杀商品"
        class="empty-state"
      />
    </el-card>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, onUnmounted } from 'vue'
import { useProductStore } from '@/store/modules/product'
import { useUserStore } from '@/store/modules/user'
import { purchaseSeckill } from '@/api/product'
import { ElMessage } from 'element-plus'

const productStore = useProductStore()
const userStore = useUserStore()

const activeActivityId = ref('')
const seckillProducts = ref([])
const purchasing = ref({})
const countdown = ref(0)
const countdownTimer = ref(null)

const seckillActivities = computed(() => productStore.seckillActivities)
const currentActivity = computed(() => {
  return seckillActivities.value.find(a => a.id === activeActivityId.value)
})

const canPurchase = computed(() => {
  if (!currentActivity.value) return false
  const now = new Date().getTime()
  const startTime = new Date(currentActivity.value.startTime).getTime()
  const endTime = new Date(currentActivity.value.endTime).getTime()
  return now >= startTime && now <= endTime
})

const progressColor = computed(() => {
  return (percentage) => {
    if (percentage >= 80) return '#ff4d4f'
    if (percentage >= 50) return '#faad14'
    return '#52c41a'
  }
})

onMounted(async () => {
  await loadSeckillActivities()
  if (seckillActivities.value.length > 0) {
    activeActivityId.value = seckillActivities.value[0].id
    await loadSeckillProducts()
    startCountdown()
  }
})

onUnmounted(() => {
  if (countdownTimer.value) {
    clearInterval(countdownTimer.value)
  }
})

const loadSeckillActivities = async () => {
  try {
    await productStore.loadSeckillActivities()
  } catch (error) {
    ElMessage.error('加载秒杀活动失败')
  }
}

const loadSeckillProducts = async () => {
  if (!activeActivityId.value) return
  
  try {
    await productStore.loadSeckillProducts(activeActivityId.value)
    seckillProducts.value = productStore.seckillProducts
  } catch (error) {
    ElMessage.error('加载秒杀商品失败')
  }
}

const handleActivityChange = async () => {
  await loadSeckillProducts()
  startCountdown()
}

const startCountdown = () => {
  if (countdownTimer.value) {
    clearInterval(countdownTimer.value)
  }
  
  if (!currentActivity.value) return
  
  const endTime = new Date(currentActivity.value.endTime).getTime()
  
  countdownTimer.value = setInterval(() => {
    const now = new Date().getTime()
    const remaining = Math.max(0, endTime - now)
    countdown.value = remaining
    
    if (remaining === 0) {
      clearInterval(countdownTimer.value)
      ElMessage.info('秒杀活动已结束')
    }
  }, 1000)
}

const handlePurchaseSeckill = async (product) => {
  if (!userStore.isLoggedIn) {
    ElMessage.warning('请先登录')
    return
  }
  
  if (!canPurchase.value) {
    ElMessage.warning('秒杀活动未开始或已结束')
    return
  }
  
  if (product.seckillStock === 0) {
    ElMessage.warning('商品已售罄')
    return
  }
  
  purchasing.value[product.id] = true
  
  try {
    await purchaseSeckill(product.id, userStore.userId)
    ElMessage.success('抢购成功')
    await loadSeckillProducts() // 刷新库存
  } catch (error) {
    ElMessage.error(error.response?.data?.message || '抢购失败')
  } finally {
    purchasing.value[product.id] = false
  }
}

const calculateDiscount = (product) => {
  const discount = (product.seckillPrice / product.originalPrice) * 100
  return discount.toFixed(1) + '折'
}

const calculateProgress = (product) => {
  const total = product.seckillStock + product.sales
  return total > 0 ? Math.round((product.sales / total) * 100) : 0
}

const formatTime = (milliseconds) => {
  const seconds = Math.floor(milliseconds / 1000)
  const minutes = Math.floor(seconds / 60)
  const hours = Math.floor(minutes / 60)
  
  const h = hours.toString().padStart(2, '0')
  const m = (minutes % 60).toString().padStart(2, '0')
  const s = (seconds % 60).toString().padStart(2, '0')
  
  return `${h}:${m}:${s}`
}

const formatDateTime = (dateString) => {
  const date = new Date(dateString)
  return date.toLocaleString('zh-CN')
}
</script>

<style scoped>
.seckill-view {
  padding: 20px;
  max-width: 1200px;
  margin: 0 auto;
}

.activity-card {
  margin-bottom: 20px;
}

.activity-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.activity-header span {
  font-size: 18px;
  font-weight: bold;
}

.activity-timer {
  display: flex;
  align-items: center;
  gap: 10px;
}

.timer {
  font-size: 20px;
  font-weight: bold;
  color: #ff4d4f;
  background: #fff;
  padding: 4px 8px;
  border-radius: 4px;
}

.activity-info {
  display: flex;
  gap: 20px;
  padding: 10px 0;
}

.activity-info span {
  color: #666;
}

.seckill-products {
  margin-top: 20px;
}

.seckill-product-card {
  margin-bottom: 20px;
  border: 2px solid #ff4d4f;
  position: relative;
}

.product-image-wrapper {
  position: relative;
  width: 100%;
  height: 200px;
  overflow: hidden;
}

.product-image {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.seckill-badge {
  position: absolute;
  top: 10px;
  left: 10px;
  background: #ff4d4f;
  color: white;
  padding: 4px 8px;
  border-radius: 4px;
  font-size: 12px;
  font-weight: bold;
}

.product-info {
  padding: 15px;
}

.product-name {
  font-size: 16px;
  font-weight: bold;
  margin: 0 0 8px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.product-desc {
  font-size: 12px;
  color: #999;
  margin: 0 0 15px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.price-section {
  margin-bottom: 15px;
}

.price-section > div {
  display: flex;
  align-items: center;
  margin-bottom: 5px;
}

.label {
  font-weight: bold;
  color: #333;
  width: 60px;
}

.seckill-price .price {
  font-size: 20px;
  color: #ff4d4f;
  font-weight: bold;
}

.original-price .price {
  font-size: 14px;
  color: #999;
  text-decoration: line-through;
}

.discount-rate {
  background: #ff4d4f;
  color: white;
  padding: 2px 6px;
  border-radius: 4px;
  font-size: 12px;
}

.stock-section {
  margin-bottom: 15px;
  display: flex;
  align-items: center;
}

.stock {
  color: #ff4d4f;
  font-weight: bold;
}

.progress-section {
  margin-bottom: 15px;
}

.progress-text {
  font-size: 12px;
  color: #999;
  margin-left: 10px;
}

.action-section {
  text-align: center;
}

.empty-state {
  margin-top: 50px;
}
</style>