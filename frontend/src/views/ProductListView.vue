<template>
  <div class="product-list-view">
    <el-row :gutter="20">
      <!-- 左侧分类 -->
      <el-col :span="4">
        <el-card>
          <template #header>
            <span>商品分类</span>
          </template>
          <el-tree
            :data="categoryTree"
            :props="treeProps"
            @node-click="handleNodeClick"
            node-key="id"
            default-expand-all
          >
            <template #default="{ node, data }">
              <span>{{ data.name }}</span>
            </template>
          </el-tree>
        </el-card>
      </el-col>

      <!-- 右侧商品列表 -->
      <el-col :span="20">
        <!-- 搜索栏 -->
        <el-card class="search-card">
          <el-row :gutter="20">
            <el-col :span="8">
              <el-input
                v-model="searchKeyword"
                placeholder="搜索商品"
                clearable
                @clear="handleSearch"
              >
                <template #append>
                  <el-button :icon="Search" @click="handleSearch" />
                </template>
              </el-input>
            </el-col>
          </el-row>
        </el-card>

        <!-- 商品列表 -->
        <el-row :gutter="20" class="product-grid">
          <el-col
            v-for="product in productList"
            :key="product.id"
            :xs="12"
            :sm="8"
            :md="6"
            :lg="4"
          >
            <el-card class="product-card" shadow="hover" @click="goToDetail(product.id)">
              <img
                :src="product.images?.[0]?.imageUrl || '/placeholder.png'"
                class="product-image"
                alt="商品图片"
              />
              <div class="product-info">
                <h3 class="product-name">{{ product.name }}</h3>
                <p class="product-desc">{{ product.description }}</p>
                <div class="product-price">
                  <span class="current-price">¥{{ product.price }}</span>
                </div>
                <div class="product-meta">
                  <span>销量: {{ product.sales }}</span>
                  <span>库存: {{ product.stock }}</span>
                </div>
              </div>
            </el-card>
          </el-col>
        </el-row>

        <!-- 分页 -->
        <div class="pagination">
          <el-pagination
            v-model:current-page="currentPage"
            v-model:page-size="pageSize"
            :total="total"
            :page-sizes="[12, 24, 36, 48]"
            layout="total, sizes, prev, pager, next, jumper"
            @size-change="handleSizeChange"
            @current-change="handleCurrentChange"
          />
        </div>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, watch } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { Search } from '@element-plus/icons-vue'
import { useProductStore } from '@/store/modules/product'
import { ElMessage } from 'element-plus'

const router = useRouter()
const route = useRoute()
const productStore = useProductStore()

const searchKeyword = ref('')
const currentPage = ref(1)
const pageSize = ref(12)
const total = ref(0)
const categoryId = ref(null)

const treeProps = {
  children: 'children',
  label: 'name'
}

const categoryTree = computed(() => productStore.categoryTree)
const productList = computed(() => productStore.products)

onMounted(async () => {
  await productStore.loadCategories()
  
  if (route.query.categoryId) {
    categoryId.value = Number(route.query.categoryId)
  }
  
  await loadProducts()
})

const loadProducts = async () => {
  try {
    const params = {
      page: currentPage.value,
      size: pageSize.value,
      keyword: searchKeyword.value,
      categoryId: categoryId.value
    }
    
    const response = await productStore.loadProducts(params)
    total.value = response.total || 0
  } catch (error) {
    ElMessage.error('加载商品失败')
  }
}

const handleNodeClick = (data) => {
  categoryId.value = data.id
  currentPage.value = 1
  loadProducts()
}

const handleSearch = () => {
  currentPage.value = 1
  loadProducts()
}

const handleSizeChange = (val) => {
  pageSize.value = val
  currentPage.value = 1
  loadProducts()
}

const handleCurrentChange = (val) => {
  currentPage.value = val
  loadProducts()
}

const goToDetail = (id) => {
  router.push(`/product/${id}`)
}

watch(() => route.query.categoryId, (newVal) => {
  if (newVal) {
    categoryId.value = Number(newVal)
    loadProducts()
  }
})
</script>

<style scoped>
.product-list-view {
  padding: 20px;
}

.search-card {
  margin-bottom: 20px;
}

.product-grid {
  margin-top: 20px;
}

.product-card {
  margin-bottom: 20px;
  cursor: pointer;
  transition: transform 0.3s;
}

.product-card:hover {
  transform: translateY(-5px);
}

.product-image {
  width: 100%;
  height: 200px;
  object-fit: cover;
}

.product-info {
  padding: 10px 0;
}

.product-name {
  font-size: 14px;
  font-weight: bold;
  margin: 0 0 5px;
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
  margin-bottom: 5px;
}

.current-price {
  font-size: 18px;
  color: #ff4d4f;
  font-weight: bold;
}

.product-meta {
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