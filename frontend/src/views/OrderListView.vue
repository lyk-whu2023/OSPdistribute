<template>
  <div class="order-list-view">
    <el-card>
      <template #header>
        <span>我的订单</span>
      </template>

      <div class="filter-tabs">
        <el-tabs v-model="statusFilter" @tab-change="handleStatusChange">
          <el-tab-pane label="全部" name="" />
          <el-tab-pane label="待支付" name="pending" />
          <el-tab-pane label="已支付" name="paid" />
          <el-tab-pane label="配送中" name="shipping" />
          <el-tab-pane label="已完成" name="completed" />
          <el-tab-pane label="已取消" name="cancelled" />
        </el-tabs>
      </div>

      <div v-for="order in orderList" :key="order.id" class="order-item">
        <div class="order-header">
          <span>订单号：{{ order.orderNo }}</span>
          <span>创建时间：{{ formatTime(order.createTime) }}</span>
          <el-tag :type="getStatusType(order.status)">
            {{ getStatusText(order.status) }}
          </el-tag>
        </div>

        <el-divider />

        <div class="order-content">
          <div class="order-items">
            <div v-for="item in order.items" :key="item.id" class="order-product">
              <img :src="item.image || '/placeholder.png'" class="product-image" />
              <div class="product-info">
                <p class="product-name">{{ item.productName }}</p>
                <p class="product-sku">{{ item.skuName }}</p>
              </div>
              <div class="product-detail">
                <p>单价：¥{{ item.price }}</p>
                <p>数量：{{ item.quantity }}</p>
                <p class="subtotal">小计：¥{{ (item.price * item.quantity).toFixed(2) }}</p>
              </div>
            </div>
          </div>

          <div class="order-actions">
            <div class="order-total">
              <span>共 {{ order.items.length }} 件商品</span>
              <span class="total-amount">合计：¥{{ order.totalAmount }}</span>
            </div>
            <div class="action-buttons">
              <el-button
                v-if="order.status === 'pending'"
                type="primary"
                size="small"
                @click="handlePay(order)"
              >
                立即支付
              </el-button>
              <el-button
                v-if="order.status === 'pending'"
                size="small"
                @click="handleCancel(order)"
              >
                取消订单
              </el-button>
              <el-button
                v-if="order.status === 'paid'"
                type="success"
                size="small"
                @click="handleShip(order)"
              >
                确认发货
              </el-button>
              <el-button
                v-if="order.status === 'shipping'"
                type="success"
                size="small"
                @click="handleComplete(order)"
              >
                确认收货
              </el-button>
              <el-button
                size="small"
                @click="viewDetail(order)"
              >
                查看详情
              </el-button>
            </div>
          </div>
        </div>
      </div>

      <el-empty v-if="orderList.length === 0" description="暂无订单" />
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { useUserStore } from '@/store/modules/user'
import { getUserOrders, cancelOrder, updateOrderStatus } from '@/api/order'

const router = useRouter()
const userStore = useUserStore()

const statusFilter = ref('')
const orderList = ref([])

onMounted(async () => {
  await loadOrders()
})

const loadOrders = async (status = '') => {
  try {
    // 确保 userId 存在
    if (!userStore.userId) {
      ElMessage.error('用户未登录')
      orderList.value = []
      // 跳转到登录页
      setTimeout(() => {
        router.push({ name: 'Auth', query: { redirect: '/orders' } })
      }, 1000)
      return
    }
    const params = status ? { status } : {}
    const response = await getUserOrders(userStore.userId, params)
    orderList.value = response || []
  } catch (error) {
    console.error('加载订单列表失败', error)
    ElMessage.error('加载订单列表失败')
    orderList.value = []
  }
}

const handleStatusChange = (status) => {
  loadOrders(status)
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

const handlePay = (order) => {
  ElMessage.info('支付功能开发中')
}

const handleCancel = async (order) => {
  try {
    await ElMessageBox.confirm('确定取消该订单吗？', '提示', {
      type: 'warning'
    })
    await cancelOrder(order.id)
    ElMessage.success('订单已取消')
    await loadOrders(statusFilter.value)
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('取消订单失败')
    }
  }
}

const handleShip = async (order) => {
  try {
    await updateOrderStatus(order.id, 'shipping')
    ElMessage.success('已确认发货')
    await loadOrders(statusFilter.value)
  } catch (error) {
    ElMessage.error('操作失败')
  }
}

const handleComplete = async (order) => {
  try {
    await updateOrderStatus(order.id, 'completed')
    ElMessage.success('已确认收货')
    await loadOrders(statusFilter.value)
  } catch (error) {
    ElMessage.error('操作失败')
  }
}

const viewDetail = (order) => {
  router.push(`/order/${order.id}`)
}
</script>

<style scoped>
.order-list-view {
  padding: 20px;
}

.filter-tabs {
  margin-bottom: 20px;
}

.order-item {
  margin-bottom: 20px;
  padding: 20px;
  border: 1px solid #ebeef5;
  border-radius: 4px;
}

.order-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 15px;
}

.order-content {
  margin-top: 15px;
}

.order-items {
  margin-bottom: 20px;
}

.order-product {
  display: flex;
  align-items: center;
  gap: 15px;
  margin-bottom: 15px;
  padding-bottom: 15px;
  border-bottom: 1px solid #f0f0f0;
}

.product-image {
  width: 80px;
  height: 80px;
  object-fit: cover;
  border-radius: 4px;
}

.product-info {
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

.product-detail {
  width: 200px;
  text-align: right;
}

.product-detail p {
  margin: 5px 0;
  font-size: 13px;
}

.subtotal {
  font-size: 16px;
  color: #ff4d4f;
  font-weight: bold;
}

.order-actions {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.order-total {
  display: flex;
  gap: 20px;
  font-size: 14px;
}

.total-amount {
  font-size: 18px;
  color: #ff4d4f;
  font-weight: bold;
}

.action-buttons {
  display: flex;
  gap: 10px;
}
</style>
