<template>
  <div class="cart-item">
    <div class="product-info">
      <img :src="item.image || '/placeholder.png'" class="product-image" />
      <div class="product-detail">
        <p class="product-name">{{ item.productName }}</p>
        <p class="product-sku">{{ item.skuName }}</p>
      </div>
    </div>
    <div class="item-details">
      <span class="price">¥{{ item.price }}</span>
      <el-input-number
        v-model="quantity"
        :min="1"
        :max="100"
        size="small"
        @change="handleQuantityChange"
      />
      <span class="subtotal">¥{{ (item.price * quantity).toFixed(2) }}</span>
      <el-button
        type="danger"
        size="small"
        @click="handleRemove"
      >
        删除
      </el-button>
    </div>
  </div>
</template>

<script setup>
import { ref, watch } from 'vue'
import { ElMessage } from 'element-plus'
import { updateCartItem, removeCartItem } from '@/api/order'

const props = defineProps({
  item: {
    type: Object,
    required: true
  }
})

const emit = defineEmits(['remove', 'update'])

const quantity = ref(props.item.quantity)

watch(() => props.item.quantity, (newVal) => {
  quantity.value = newVal
})

const handleQuantityChange = async (newQuantity) => {
  try {
    await updateCartItem(props.item.id, newQuantity)
    ElMessage.success('更新成功')
    emit('update', { ...props.item, quantity: newQuantity })
  } catch (error) {
    ElMessage.error('更新失败')
    quantity.value = props.item.quantity
  }
}

const handleRemove = async () => {
  try {
    await removeCartItem(props.item.id)
    ElMessage.success('删除成功')
    emit('remove', props.item.id)
  } catch (error) {
    ElMessage.error('删除失败')
  }
}
</script>

<style scoped>
.cart-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 15px;
  border-bottom: 1px solid #f0f0f0;
}

.product-info {
  display: flex;
  align-items: center;
  gap: 10px;
  flex: 1;
}

.product-image {
  width: 80px;
  height: 80px;
  object-fit: cover;
  border-radius: 4px;
}

.product-detail {
  flex: 1;
}

.product-name {
  font-size: 14px;
  margin: 0 0 5px;
}

.product-sku {
  font-size: 12px;
  color: #999;
}

.item-details {
  display: flex;
  align-items: center;
  gap: 15px;
}

.price {
  font-size: 14px;
  color: #666;
  width: 80px;
}

.subtotal {
  font-size: 16px;
  color: #ff4d4f;
  font-weight: bold;
  width: 100px;
  text-align: right;
}
</style>
