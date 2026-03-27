<template>
  <div class="order-detail-view">
    <el-card v-loading="loading">
      <template #header>
        <div class="header">
          <span>订单详情</span>
          <el-button @click="goBack">返回</el-button>
        </div>
      </template>

      <div v-if="order" class="order-detail">
        <div class="order-info-section">
          <h3>订单信息</h3>
          <el-descriptions :column="2" border>
            <el-descriptions-item label="订单号">{{ order.orderNo }}</el-descriptions-item>
            <el-descriptions-item label="订单状态">
              <el-tag :type="getStatusType(order.status)">
                {{ getStatusText(order.status) }}
              </el-tag>
            </el-descriptions-item>
            <el-descriptions-item label="创建时间">{{ formatTime(order.createTime) }}</el-descriptions-item>
            <el-descriptions-item label="支付时间">{{ formatTime(order.payTime) || '-' }}</el-descriptions-item>
            <el-descriptions-item label="发货时间">{{ formatTime(order.shipTime) || '-' }}</el-descriptions-item>
            <el-descriptions-item label="完成时间">{{ formatTime(order.completeTime) || '-' }}</el-descriptions-item>
          </el-descriptions>
        </div>

        <div class="address-section">
          <h3>收货地址</h3>
          <div class="address-info" v-if="order.address">
            <p>
              <span class="name">{{ order.address.name }}</span>
              <span class="phone">{{ order.address.phone }}</span>
            </p>
            <p class="detail">{{ order.address.detail }}</p>
          </div>
        </div>

        <div class="items-section">
          <h3>商品清单</h3>
          <el-table :data="order.items" style="width: 100%">
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

        <div class="amount-section">
          <h3>订单金额</h3>
          <div class="amount-info">
            <div class="amount-row">
              <span>商品总额:</span>
              <span>¥{{ order.totalAmount }}</span>
            </div>
            <div class="amount-row">
              <span>运费:</span>
              <span>¥{{ order.shippingFee || 0 }}</span>
            </div>
            <el-divider />
            <div class="amount-row total">
              <span>实付款:</span>
              <span class="total-amount">¥{{ order.totalAmount }}</span>
            </div>
          </div>
        </div>

        <div class="action-section">
          <el-button v-if="order.status === 'pending'" type="primary" @click="handlePay">
            立即支付
          </el-button>
          <el-button v-if="order.status === 'pending'" @click="handleCancel">
            取消订单
          </el-button>
          <el-button v-if="order.status === 'paid'" type="success" @click="handleShip">
            确认发货
          </el-button>
          <el-button v-if="order.status === 'shipping'" type="success" @click="handleComplete">
            确认收货
          </el-button>
        </div>
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getOrder, cancelOrder, updateOrderStatus } from '@/api/order'

const route = useRoute()
const router = useRouter()

const order = ref(null)
const loading = ref(true)

onMounted(async () => {
  await loadOrderDetail()
})

const loadOrderDetail = async () => {
  try {
    loading.value = true
    const response = await getOrder(route.params.id)
    order.value = response
  } catch (error) {
    ElMessage.error('加载订单详情失败')
  } finally {
    loading.value = false
  }
}

const formatTime = (time) => {
  if (!time) return ''
  const date = new Date(time)
  return date.toLocaleString('zh-CN')
}

const getStatusType = (status) => {
  const types = {
    pending: 'warning',
    paid: 'success',
    shipping: 'primary',
    completed: 'success',
    cancelled: 'info'
  }
  return types[status] || 'info'
}

const getStatusText = (status) => {
  const texts = {
    pending: '待支付',
    paid: '已支付',
    shipping: '配送中',
    completed: '已完成',
    cancelled: '已取消'
  }
  return texts[status] || status
}

const handlePay = () => {
  ElMessage.info('支付功能开发中')
}

const handleCancel = async () => {
  try {
    await ElMessageBox.confirm('确定取消该订单吗？', '提示', {
      type: 'warning'
    })
    await cancelOrder(order.value.id)
    ElMessage.success('订单已取消')
    await loadOrderDetail()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('取消订单失败')
    }
  }
}

const handleShip = async () => {
  try {
    await updateOrderStatus(order.value.id, 'shipping')
    ElMessage.success('已确认发货')
    await loadOrderDetail()
  } catch (error) {
    ElMessage.error('操作失败')
  }
}

const handleComplete = async () => {
  try {
    await updateOrderStatus(order.value.id, 'completed')
    ElMessage.success('已确认收货')
    await loadOrderDetail()
  } catch (error) {
    ElMessage.error('操作失败')
  }
}

const goBack = () => {
  router.back()
}
</script>

<style scoped>
.order-detail-view {
  padding: 20px;
}

.header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.order-detail {
  display: flex;
  flex-direction: column;
  gap: 30px;
}

.order-info-section h3,
.address-section h3,
.items-section h3,
.amount-section h3 {
  margin-bottom: 15px;
  font-size: 16px;
  color: #333;
}

.address-info {
  padding: 15px;
  background: #f5f7fa;
  border-radius: 4px;
}

.address-info p {
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

.amount-info {
  padding: 20px;
  background: #f5f7fa;
  border-radius: 4px;
}

.amount-row {
  display: flex;
  justify-content: space-between;
  margin-bottom: 10px;
  font-size: 14px;
}

.amount-row.total {
  font-size: 18px;
  font-weight: bold;
}

.total-amount {
  color: #ff4d4f;
  font-size: 24px;
}

.action-section {
  display: flex;
  gap: 10px;
  justify-content: flex-end;
  padding-top: 20px;
  border-top: 1px solid #ebeef5;
}
</style>
