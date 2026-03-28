<template>
  <div class="store-view">
    <el-card class="store-header">
      <h1>精选店铺</h1>
      <p>探索优质商家，发现更多好物</p>
    </el-card>

    <el-row :gutter="20" class="store-list">
      <el-col
        v-for="store in storeList"
        :key="store.id"
        :xs="24"
        :sm="12"
        :md="8"
        :lg="6"
      >
        <el-card class="store-card" shadow="hover" @click="goToDetail(store.id)">
          <div class="store-logo">
            <img :src="store.logo || generateStoreImage(store.id)" :alt="store.name" />
          </div>
          <div class="store-info">
            <h3 class="store-name">{{ store.name }}</h3>
            <p class="store-description">{{ store.description || '优质店铺，值得信赖' }}</p>
            <div class="store-stats">
              <div class="stat-item">
                <el-icon><Shop /></el-icon>
                <span>商品：{{ store.productCount || 0 }}</span>
              </div>
              <div class="stat-item">
                <el-icon><Star /></el-icon>
                <span>评分：{{ store.rating || 5.0 }}</span>
              </div>
            </div>
            <div class="store-meta">
              <span>粉丝：{{ store.followerCount || 0 }}</span>
              <span>销量：{{ store.salesCount || 0 }}</span>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <div class="pagination" v-if="total > pageSize">
      <el-pagination
        v-model:current-page="currentPage"
        v-model:page-size="pageSize"
        :total="total"
        :page-sizes="[8, 16, 24, 32]"
        layout="total, sizes, prev, pager, next, jumper"
        @size-change="handleSizeChange"
        @current-change="handleCurrentChange"
      />
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { getStores } from '@/api/store'
import { generateStoreImage } from '@/utils/image'
import { ElMessage } from 'element-plus'

const router = useRouter()

const storeList = ref([])
const currentPage = ref(1)
const pageSize = ref(8)
const total = ref(0)

onMounted(async () => {
  await loadStores()
})

const loadStores = async () => {
  try {
    const response = await getStores({
      page: currentPage.value,
      size: pageSize.value
    })
    storeList.value = response.records || response.content || []
    total.value = response.total || 0
  } catch (error) {
    console.error('加载店铺列表失败', error)
    ElMessage.warning('店铺服务暂时不可用，显示示例数据')
    storeList.value = generateSampleStores()
    total.value = storeList.value.length
  }
}

const generateSampleStores = () => {
  return Array.from({ length: 8 }, (_, index) => ({
    id: index + 1,
    name: `优选店铺 ${index + 1}`,
    description: '精选优质商品，提供优质服务',
    logo: generateStoreImage(index + 1),
    productCount: Math.floor(Math.random() * 500) + 50,
    rating: (Math.random() * 1 + 4).toFixed(1),
    followerCount: Math.floor(Math.random() * 10000),
    salesCount: Math.floor(Math.random() * 50000)
  }))
}

const handleSizeChange = (val) => {
  pageSize.value = val
  loadStores()
}

const handleCurrentChange = (val) => {
  currentPage.value = val
  loadStores()
}

const goToDetail = (id) => {
  router.push(`/store/${id}`)
}
</script>

<style scoped>
.store-view {
  padding: 20px;
  max-width: 1400px;
  margin: 0 auto;
}

.store-header {
  text-align: center;
  margin-bottom: 30px;
}

.store-header h1 {
  margin: 0 0 10px;
  color: #333;
}

.store-header p {
  margin: 0;
  color: #666;
}

.store-list {
  margin-bottom: 30px;
}

.store-card {
  margin-bottom: 20px;
  cursor: pointer;
  transition: all 0.3s;
}

.store-card:hover {
  transform: translateY(-5px);
}

.store-logo {
  width: 100%;
  height: 150px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: #f5f7fa;
  border-radius: 4px;
  overflow: hidden;
}

.store-logo img {
  max-width: 80%;
  max-height: 80%;
  object-fit: contain;
}

.store-info {
  padding: 15px 0 0;
}

.store-name {
  font-size: 16px;
  font-weight: bold;
  margin: 0 0 10px;
  color: #333;
}

.store-description {
  font-size: 13px;
  color: #666;
  margin: 0 0 15px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.store-stats {
  display: flex;
  gap: 15px;
  margin-bottom: 10px;
}

.stat-item {
  display: flex;
  align-items: center;
  gap: 5px;
  font-size: 13px;
  color: #666;
}

.store-meta {
  display: flex;
  justify-content: space-between;
  font-size: 12px;
  color: #999;
}

.pagination {
  display: flex;
  justify-content: center;
  margin-top: 20px;
}
</style>
