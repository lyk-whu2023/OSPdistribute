<template>
  <div class="product-card" @click="goToDetail">
    <div class="product-image-wrapper">
      <img
        :src="product.images?.[0]?.imageUrl || '/placeholder.png'"
        class="product-image"
        alt="商品图片"
      />
      <div v-if="product.stock === 0" class="stock-out">
        <span>已售罄</span>
      </div>
      <div v-if="showSeckill && product.seckillPrice" class="seckill-tag">
        <span>秒杀</span>
      </div>
    </div>
    
    <div class="product-info">
      <h3 class="product-name">{{ product.name }}</h3>
      <p class="product-desc">{{ product.description }}</p>
      
      <div class="product-price">
        <span v-if="showSeckill && product.seckillPrice" class="seckill-price">
          ¥{{ product.seckillPrice }}
        </span>
        <span class="current-price" :class="{ 'has-seckill': showSeckill && product.seckillPrice }">
          ¥{{ product.price }}
        </span>
        <span v-if="product.originalPrice" class="original-price">
          ¥{{ product.originalPrice }}
        </span>
      </div>
      
      <div class="product-meta">
        <span class="sales">销量: {{ product.sales }}</span>
        <span v-if="product.stock <= 10 && product.stock > 0" class="low-stock">
          仅剩{{ product.stock }}件
        </span>
      </div>
      
      <div class="product-actions" v-if="showActions">
        <el-button 
          type="primary" 
          size="small" 
          @click.stop="addToCart"
          :disabled="product.stock === 0"
        >
          加入购物车
        </el-button>
        <el-button 
          type="danger" 
          size="small" 
          @click.stop="buyNow"
          :disabled="product.stock === 0"
        >
          立即购买
        </el-button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { defineProps, defineEmits } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { useUserStore } from '@/store/modules/user'

const props = defineProps({
  product: {
    type: Object,
    required: true
  },
  showActions: {
    type: Boolean,
    default: false
  },
  showSeckill: {
    type: Boolean,
    default: false
  }
})

const emit = defineEmits(['add-to-cart', 'buy-now'])
const router = useRouter()
const userStore = useUserStore()

const goToDetail = () => {
  router.push(`/product/${props.product.id}`)
}

const addToCart = () => {
  if (!userStore.isLoggedIn) {
    ElMessage.warning('请先登录')
    router.push('/auth')
    return
  }
  emit('add-to-cart', props.product)
}

const buyNow = () => {
  if (!userStore.isLoggedIn) {
    ElMessage.warning('请先登录')
    router.push('/auth')
    return
  }
  emit('buy-now', props.product)
}
</script>

<style scoped>
.product-card {
  background: white;
  border-radius: 8px;
  overflow: hidden;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
  transition: all 0.3s ease;
  cursor: pointer;
}

.product-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.15);
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
  transition: transform 0.3s ease;
}

.product-card:hover .product-image {
  transform: scale(1.05);
}

.stock-out {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.7);
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
  font-size: 18px;
  font-weight: bold;
}

.seckill-tag {
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
  font-size: 14px;
  font-weight: bold;
  margin: 0 0 8px;
  color: #333;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.product-desc {
  font-size: 12px;
  color: #999;
  margin: 0 0 10px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.product-price {
  margin-bottom: 10px;
  display: flex;
  align-items: center;
  gap: 8px;
}

.seckill-price {
  font-size: 18px;
  color: #ff4d4f;
  font-weight: bold;
}

.current-price {
  font-size: 16px;
  color: #ff4d4f;
  font-weight: bold;
}

.current-price.has-seckill {
  font-size: 14px;
  color: #999;
  text-decoration: line-through;
}

.original-price {
  font-size: 14px;
  color: #999;
  text-decoration: line-through;
}

.product-meta {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 10px;
}

.sales {
  font-size: 12px;
  color: #999;
}

.low-stock {
  font-size: 12px;
  color: #ff4d4f;
  font-weight: bold;
}

.product-actions {
  display: flex;
  gap: 8px;
}

.product-actions .el-button {
  flex: 1;
}
</style>