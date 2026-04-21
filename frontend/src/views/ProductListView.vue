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
            :expand-on-click-node="false"
          >
            <template #default="{ node, data }">
              <span class="category-node">{{ data.name }}</span>
            </template>
          </el-tree>
        </el-card>
      </el-col>

      <!-- 右侧商品列表 -->
      <el-col :span="20">
        <!-- 面包屑导航 -->
        <el-card class="breadcrumb-card">
          <el-breadcrumb separator="/">
            <el-breadcrumb-item>首页</el-breadcrumb-item>
            <el-breadcrumb-item>商品列表</el-breadcrumb-item>
            <el-breadcrumb-item v-if="selectedCategoryName">{{ selectedCategoryName }}</el-breadcrumb-item>
          </el-breadcrumb>
        </el-card>

        <!-- 搜索栏 -->
        <el-card class="search-card">
          <el-row :gutter="20" align="middle">
            <el-col :span="10">
              <el-input
                v-model="searchKeyword"
                placeholder="搜索商品名称或描述"
                clearable
                @keyup.enter="handleSearch"
              >
                <template #append>
                  <el-button :icon="Search" @click="handleSearch">搜索</el-button>
                </template>
              </el-input>
            </el-col>
            <el-col :span="4">
              <el-button type="primary" @click="handleSearch" :loading="loading">
                查询
              </el-button>
              <el-button @click="handleReset">重置</el-button>
            </el-col>
          </el-row>
        </el-card>

        <!-- 商品列表 -->
        <div v-loading="loading" class="product-list-container">
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
                <div class="product-image-wrapper">
                  <img
                    :src="getProductImageUrl(product)"
                    class="product-image"
                    :alt="product.name"
                  />
                </div>
                <div class="product-info">
                  <h3 class="product-name" :title="product.name">{{ product.name }}</h3>
                  <p class="product-desc" :title="product.description">{{ product.description || '暂无描述' }}</p>
                  <div class="product-price">
                    <span class="current-price">¥{{ product.price }}</span>
                  </div>
                  <div class="product-meta">
                    <span>销量：{{ product.sales || 0 }}</span>
                    <span :class="{ 'out-of-stock': product.stock === 0 }">
                      库存：{{ product.stock || 0 }}
                    </span>
                  </div>
                </div>
              </el-card>
            </el-col>
          </el-row>

          <!-- 空状态 -->
          <el-empty v-if="!loading && productList.length === 0" description="暂无商品" />
        </div>

        <!-- 分页 -->
        <div class="pagination" v-if="total > 0">
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
import { getProductImageBySeed } from '@/utils/image'
import { ElMessage } from 'element-plus'

const router = useRouter()
const route = useRoute()
const productStore = useProductStore()

const loading = ref(false)
const searchKeyword = ref('')
const currentPage = ref(1)
const pageSize = ref(12)
const total = ref(0)
const categoryId = ref(null)
const selectedCategoryName = ref('')

const treeProps = {
  children: 'children',
  label: 'name'
}

const categoryTree = computed(() => productStore.categoryTree)
const productList = computed(() => productStore.products)

const getProductImageUrl = (product) => {
  if (product.images && product.images.length > 0) {
    return product.images[0].imageUrl
  }
  return getProductImageBySeed(product.id, 400, 400)
}

onMounted(async () => {
  await productStore.loadCategories()
  
  if (route.query.categoryId) {
    categoryId.value = Number(route.query.categoryId)
  }
  
  await loadProducts()
})

const loadProducts = async () => {
  loading.value = true
  try {
    const hasSearchParam = searchKeyword.value || categoryId.value
    
    const params = {
      page: currentPage.value,
      size: pageSize.value
    }
    
    let response
    if (hasSearchParam) {
      response = await productStore.searchProducts({
        ...params,
        keyword: searchKeyword.value,
        categoryId: categoryId.value
      })
    } else {
      response = await productStore.loadProducts(params)
    }
    
    total.value = response.total || 0
  } catch (error) {
    ElMessage.error('加载商品失败')
  } finally {
    loading.value = false
  }
}

const handleNodeClick = (data) => {
  categoryId.value = data.id
  selectedCategoryName.value = data.name
  currentPage.value = 1
  loadProducts()
}

const handleSearch = () => {
  currentPage.value = 1
  loadProducts()
}

const handleReset = () => {
  searchKeyword.value = ''
  categoryId.value = null
  selectedCategoryName.value = ''
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

.breadcrumb-card {
  margin-bottom: 20px;
}

.search-card {
  margin-bottom: 20px;
}

.product-list-container {
  min-height: 400px;
}

.product-grid {
  margin-top: 20px;
}

.product-card {
  margin-bottom: 20px;
  cursor: pointer;
  transition: all 0.3s;
  height: 100%;
}

.product-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
}

.product-image-wrapper {
  width: 100%;
  height: 250px;
  overflow: hidden;
  border-radius: 4px;
  background-color: #f5f5f5;
}

.product-image {
  width: 100%;
  height: 100%;
  object-fit: cover;
  transition: transform 0.3s;
}

.product-card:hover .product-image {
  transform: scale(1.1);
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

.out-of-stock {
  color: #f56c6c;
  font-weight: bold;
}

.category-node {
  cursor: pointer;
  padding: 2px 5px;
  border-radius: 3px;
}

.category-node:hover {
  background-color: #f5f7fa;
}

.pagination {
  display: flex;
  justify-content: center;
  margin-top: 20px;
}
</style>