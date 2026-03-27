<template>
  <el-tag :type="getStatusType(status)">
    {{ getStatusText(status) }}
  </el-tag>
</template>

<script setup>
defineProps({
  status: {
    type: String,
    required: true,
    default: 'pending'
  }
})

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
</script>
