<template>
  <div class="cart-view">
    <el-card v-if="cartItems.length > 0">
      <template #header>
        <div class="cart-header">
          <span>购物车</span>
          <el-button type="danger" size="small" @click="handleClearCart">
            清空购物车
          </el-button>
        </div>
      </template>

      <el-table :data="cartItems" style="width: 100%">
        <el-table-column label="商品信息" width="400">
          <template #default="{ row }">
            <div class="product-info">
              <img :src="row.image || '/placeholder.png'" class="product-image" />
              <div class="product-detail">
                <p class="product-name">{{ row.productName }}</p>
                <p class="product-sku">{{ row.skuName }}</p>
              </div>
            </div>
          </template>
        </el-table-column>

        <el-table-column label="单价" width="150">
          <template #default="{ row }">
            ¥{{ row.price }}
          </template>
        </el-table-column>

        <el-table-column label="数量" width="200">
          <template #default="{ row }">
            <el-input-number
              v-model="row.quantity"
              :min="1"
              :max="100"
              size="small"
              @change="handleQuantityChange(row)"
            />
          </template>
        </el-table-column>

        <el-table-column label="小计" width="150">
          <template #default="{ row }">
            <span class="subtotal">¥{{ (row.price * row.quantity).toFixed(2) }}</span>
          </template>
        </el-table-column>

        <el-table-column label="操作" width="100">
          <template #default="{ row }">
            <el-button
              type="danger"
              size="small"
              @click="handleRemove(row)"
            >
              删除
            </el-button>
          </template>
        </el-table-column>
      </el-table>

      <div class="cart-footer">
        <div class="total-info">
          <span>共 {{ cartItems.length }} 件商品</span>
          <span class="total-price">合计：¥{{ totalPrice.toFixed(2) }}</span>
        </div>
        <el-button type="primary" size="large" @click="handleCheckout">
          去结算
        </el-button>
      </div>
    </el-card>

    <el-empty v-else description="购物车空空如也">
      <el-button type="primary" @click="goToProducts">去逛逛</el-button>
    </el-empty>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { useUserStore } from '@/store/modules/user'
import { getCartItems, updateCartItem, removeCartItem, clearCart } from '@/api/order'

const router = useRouter()
const userStore = useUserStore()

const cartItems = ref([])

const totalPrice = computed(() => {
  return cartItems.value.reduce((sum, item) => {
    return sum + item.price * item.quantity
  }, 0)
})

onMounted(async () => {
  await loadCart()
})

const loadCart = async () => {
  try {
    const response = await getCartItems(userStore.userId)
    cartItems.value = response || []
  } catch (error) {
    ElMessage.error('加载购物车失败')
    cartItems.value = []
  }
}

const handleQuantityChange = async (row) => {
  try {
    await updateCartItem(row.id, row.quantity)
    ElMessage.success('更新成功')
  } catch (error) {
    ElMessage.error('更新失败')
  }
}

const handleRemove = async (row) => {
  try {
    await ElMessageBox.confirm('确定删除该商品吗？', '提示', {
      type: 'warning'
    })
    await removeCartItem(row.id)
    ElMessage.success('删除成功')
    await loadCart()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('删除失败')
    }
  }
}

const handleClearCart = async () => {
  try {
    await ElMessageBox.confirm('确定清空购物车吗？', '提示', {
      type: 'warning'
    })
    await clearCart(userStore.userId)
    ElMessage.success('清空成功')
    await loadCart()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('清空失败')
    }
  }
}

const handleCheckout = () => {
  if (cartItems.value.length === 0) {
    ElMessage.warning('购物车为空')
    return
  }
  
  router.push({
    path: '/order/submit',
    query: {
      items: JSON.stringify(cartItems.value.map(item => ({
        productId: item.productId,
        skuId: item.skuId,
        quantity: item.quantity,
        price: item.price,
        productName: item.productName,
        skuName: item.skuName,
        image: item.image
      })))
    }
  })
}

const goToProducts = () => {
  router.push('/products')
}
</script>

<style scoped>
.cart-view {
  padding: 20px;
}

.cart-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.product-info {
  display: flex;
  align-items: center;
  gap: 10px;
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

.subtotal {
  font-size: 16px;
  color: #ff4d4f;
  font-weight: bold;
}

.cart-footer {
  margin-top: 20px;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.total-info {
  display: flex;
  gap: 20px;
  font-size: 16px;
}

.total-price {
  font-size: 20px;
  color: #ff4d4f;
  font-weight: bold;
}
</style>
