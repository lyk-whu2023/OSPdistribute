<template>
  <div class="order-submit-view">
    <el-card>
      <template #header>
        <span>确认订单</span>
      </template>

      <el-row :gutter="20">
        <el-col :span="16">
          <div class="section">
            <h3>收货地址</h3>
            <el-radio-group v-model="selectedAddressId" class="address-list">
              <el-radio
                v-for="address in addressList"
                :key="address.id"
                :value="address.id"
                class="address-item"
              >
                <div class="address-content">
                  <p>
                    <span class="name">{{ address.name }}</span>
                    <span class="phone">{{ address.phone }}</span>
                    <el-tag v-if="address.isDefault" size="small" type="success">
                      默认
                    </el-tag>
                  </p>
                  <p class="detail">{{ address.detail }}</p>
                </div>
              </el-radio>
            </el-radio-group>
            <el-button link type="primary" @click="showAddressDialog = true">
              管理地址
            </el-button>
          </div>

          <div class="section">
            <h3>商品清单</h3>
            <el-table :data="orderItems" style="width: 100%">
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
              <el-table-column label="数量" width="100">
                <template #default="{ row }">
                  {{ row.quantity }}
                </template>
              </el-table-column>
              <el-table-column label="小计" width="150">
                <template #default="{ row }">
                  <span class="subtotal">¥{{ (row.price * row.quantity).toFixed(2) }}</span>
                </template>
              </el-table-column>
            </el-table>
          </div>
        </el-col>

        <el-col :span="8">
          <div class="order-summary">
            <h3>订单金额</h3>
            <div class="summary-item">
              <span>商品总额</span>
              <span>¥{{ totalAmount.toFixed(2) }}</span>
            </div>
            <div class="summary-item">
              <span>运费</span>
              <span>¥0.00</span>
            </div>
            <el-divider />
            <div class="summary-item total">
              <span>实付款</span>
              <span class="total-price">¥{{ totalAmount.toFixed(2) }}</span>
            </div>
            <el-button type="primary" size="large" :loading="submitting" @click="handleSubmitOrder">
              提交订单
            </el-button>
          </div>
        </el-col>
      </el-row>
    </el-card>

    <el-dialog v-model="showAddressDialog" title="管理地址" width="600px">
      <AddressView />
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { useUserStore } from '@/store/modules/user'
import { getUserAddresses } from '@/api/user'
import { createOrder } from '@/api/order'
import AddressView from './AddressView.vue'

const route = useRoute()
const router = useRouter()
const userStore = useUserStore()

const selectedAddressId = ref(null)
const addressList = ref([])
const orderItems = ref([])
const showAddressDialog = ref(false)
const submitting = ref(false)

const totalAmount = computed(() => {
  return orderItems.value.reduce((sum, item) => {
    return sum + item.price * item.quantity
  }, 0)
})

onMounted(async () => {
  await loadAddresses()
  loadOrderItems()
})

const loadAddresses = async () => {
  try {
    // 确保 userId 存在
    if (!userStore.userId) {
      ElMessage.error('用户未登录')
      addressList.value = []
      // 跳转到登录页
      setTimeout(() => {
        router.push({ name: 'Auth', query: { redirect: '/order/submit' } })
      }, 1000)
      return
    }
    addressList.value = await getUserAddresses(userStore.userId)
    if (addressList.value.length > 0) {
      const defaultAddress = addressList.value.find(a => a.isDefault === 1)
      selectedAddressId.value = defaultAddress ? defaultAddress.id : addressList.value[0].id
    }
  } catch (error) {
    console.error('加载地址失败', error)
    ElMessage.error('加载地址失败')
  }
}

const loadOrderItems = () => {
  const itemsParam = route.query.items
  if (itemsParam) {
    orderItems.value = JSON.parse(itemsParam)
  }
}

const handleSubmitOrder = async () => {
  if (!selectedAddressId.value) {
    ElMessage.warning('请选择收货地址')
    return
  }

  if (orderItems.value.length === 0) {
    ElMessage.warning('商品清单为空')
    return
  }

  try {
    submitting.value = true
    
    const orderData = {
      userId: userStore.userId,
      addressId: selectedAddressId.value,
      storeId: orderItems.value[0]?.storeId || 1,
      items: orderItems.value.map(item => ({
        productId: item.productId,
        skuId: item.skuId,
        productName: item.productName,
        price: item.price,
        quantity: item.quantity
      }))
    }

    console.log('提交订单数据:', JSON.stringify(orderData, null, 2))
    
    const order = await createOrder(orderData)
    
    ElMessage.success('订单创建成功')
    router.push(`/order/${order.id}`)
  } catch (error) {
    ElMessage.error(error.response?.data?.message || '订单创建失败')
  } finally {
    submitting.value = false
  }
}
</script>

<style scoped>
.order-submit-view {
  padding: 20px;
}

.section {
  margin-bottom: 30px;
}

.section h3 {
  margin-bottom: 15px;
}

.address-list {
  display: block;
}

.address-item {
  display: block;
  margin-bottom: 10px;
  width: 100%;
}

.address-content {
  padding: 10px;
}

.address-content p {
  margin: 5px 0;
}

.name {
  font-weight: bold;
  margin-right: 10px;
}

.phone {
  color: #666;
}

.detail {
  color: #999;
  font-size: 13px;
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

.order-summary {
  position: sticky;
  top: 20px;
  padding: 20px;
  background: #f5f5f5;
  border-radius: 8px;
}

.summary-item {
  display: flex;
  justify-content: space-between;
  margin-bottom: 10px;
}

.summary-item.total {
  font-size: 18px;
  font-weight: bold;
}

.total-price {
  color: #ff4d4f;
  font-size: 24px;
}

.order-summary button {
  width: 100%;
  margin-top: 20px;
}
</style>
