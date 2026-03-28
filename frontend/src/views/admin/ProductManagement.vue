<template>
  <div class="product-management">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>商品管理</span>
          <el-button type="primary" @click="showAddDialog">
            <el-icon><Plus /></el-icon>
            新增商品
          </el-button>
        </div>
      </template>

      <!-- 搜索栏 -->
      <div class="search-bar">
        <el-input
          v-model="searchKeyword"
          placeholder="搜索商品名称"
          clearable
          style="width: 300px"
          @clear="loadProducts"
        >
          <template #append>
            <el-button :icon="Search" @click="loadProducts" />
          </template>
        </el-input>
        <el-select v-model="categoryId" placeholder="选择分类" clearable style="width: 200px; margin-left: 10px" @change="loadProducts">
          <el-option
            v-for="cat in categories"
            :key="cat.id"
            :label="cat.name"
            :value="cat.id"
          />
        </el-select>
      </div>

      <!-- 商品列表 -->
      <el-table :data="productList" style="width: 100%; margin-top: 20px" v-loading="loading">
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column label="商品图片" width="100">
          <template #default="{ row }">
            <el-image
              :src="row.images?.[0]?.imageUrl || getProductImage(row.id)"
              :preview-src-list="[row.images?.[0]?.imageUrl || getProductImage(row.id)]"
              style="width: 60px; height: 60px; border-radius: 4px"
              fit="cover"
            />
          </template>
        </el-table-column>
        <el-table-column prop="name" label="商品名称" min-width="200" />
        <el-table-column prop="description" label="描述" min-width="200" show-overflow-tooltip />
        <el-table-column prop="price" label="价格" width="100">
          <template #default="{ row }">
            <span class="price">¥{{ row.price }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="stock" label="库存" width="80" />
        <el-table-column prop="sales" label="销量" width="80" />
        <el-table-column label="状态" width="80">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'info'">
              {{ row.status === 1 ? '上架' : '下架' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="250" fixed="right">
          <template #default="{ row }">
            <el-button size="small" @click="showEditDialog(row)">
              编辑
            </el-button>
            <el-button size="small" type="danger" @click="handleDelete(row)">
              删除
            </el-button>
          </template>
        </el-table-column>
      </el-table>

      <!-- 分页 -->
      <div class="pagination">
        <el-pagination
          v-model:current-page="currentPage"
          v-model:page-size="pageSize"
          :total="total"
          :page-sizes="[10, 20, 50, 100]"
          layout="total, sizes, prev, pager, next, jumper"
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
        />
      </div>
    </el-card>

    <!-- 新增/编辑商品对话框 -->
    <el-dialog
      v-model="dialogVisible"
      :title="dialogTitle"
      width="800px"
      @close="handleDialogClose"
    >
      <el-form
        ref="productFormRef"
        :model="productForm"
        :rules="productRules"
        label-width="100px"
      >
        <el-form-item label="商品名称" prop="name">
          <el-input v-model="productForm.name" placeholder="请输入商品名称" />
        </el-form-item>
        <el-form-item label="商品描述" prop="description">
          <el-input
            v-model="productForm.description"
            type="textarea"
            :rows="3"
            placeholder="请输入商品描述"
          />
        </el-form-item>
        <el-form-item label="商品价格" prop="price">
          <el-input-number v-model="productForm.price" :min="0" :precision="2" :step="0.1" />
        </el-form-item>
        <el-form-item label="库存数量" prop="stock">
          <el-input-number v-model="productForm.stock" :min="0" />
        </el-form-item>
        <el-form-item label="商品分类" prop="categoryId">
          <el-select v-model="productForm.categoryId" placeholder="请选择分类" style="width: 100%">
            <el-option
              v-for="cat in categories"
              :key="cat.id"
              :label="cat.name"
              :value="cat.id"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="商品图片">
          <div class="image-upload">
            <el-upload
              list-type="picture-card"
              :file-list="imageList"
              :on-change="handleImageChange"
              :on-remove="handleImageRemove"
              :limit="5"
              action="#"
              :auto-upload="false"
            >
              <el-icon><Plus /></el-icon>
            </el-upload>
            <div class="image-tip">
              支持生成随机图片，也可以上传图片（暂未实现上传功能）
            </div>
          </div>
        </el-form-item>
        <el-form-item label="商品详情">
          <el-input
            v-model="productForm.detail"
            type="textarea"
            :rows="5"
            placeholder="请输入商品详情"
          />
        </el-form-item>
        <el-form-item label="状态">
          <el-switch v-model="productForm.status" :active-value="1" :inactive-value="0" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmit" :loading="submitting">
          确定
        </el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, computed } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Search, Plus } from '@element-plus/icons-vue'
import { getProducts, createProduct, updateProduct, deleteProduct } from '@/api/product'
import { getCategories } from '@/api/product'
import { getProductImageBySeed, generateMultipleProductImages } from '@/utils/image'

const loading = ref(false)
const submitting = ref(false)
const productList = ref([])
const categories = ref([])
const currentPage = ref(1)
const pageSize = ref(10)
const total = ref(0)
const searchKeyword = ref('')
const categoryId = ref(null)
const dialogVisible = ref(false)
const dialogTitle = ref('新增商品')
const productFormRef = ref(null)
const imageList = ref([])

const productForm = reactive({
  id: null,
  name: '',
  description: '',
  price: 0,
  stock: 0,
  categoryId: null,
  detail: '',
  status: 1,
  images: []
})

const productRules = {
  name: [{ required: true, message: '请输入商品名称', trigger: 'blur' }],
  price: [{ required: true, message: '请输入商品价格', trigger: 'blur' }],
  stock: [{ required: true, message: '请输入库存数量', trigger: 'blur' }],
  categoryId: [{ required: true, message: '请选择商品分类', trigger: 'change' }]
}

const getProductImage = (productId) => {
  return getProductImageBySeed(productId, 100, 100)
}

onMounted(async () => {
  await loadCategories()
  await loadProducts()
})

const loadCategories = async () => {
  try {
    const response = await getCategories()
    categories.value = response || []
  } catch (error) {
    console.error('加载分类失败', error)
  }
}

const loadProducts = async () => {
  loading.value = true
  try {
    const response = await getProducts({
      page: currentPage.value,
      size: pageSize.value,
      keyword: searchKeyword.value,
      categoryId: categoryId.value
    })
    productList.value = response.records || response.content || []
    total.value = response.total || 0
  } catch (error) {
    ElMessage.error('加载商品列表失败')
  } finally {
    loading.value = false
  }
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

const showAddDialog = () => {
  dialogTitle.value = '新增商品'
  dialogVisible.value = true
}

const showEditDialog = (row) => {
  dialogTitle.value = '编辑商品'
  Object.assign(productForm, row)
  if (row.images && row.images.length > 0) {
    imageList.value = row.images.map((img, index) => ({
      url: img.imageUrl,
      name: `图片${index + 1}`
    }))
  }
  dialogVisible.value = true
}

const handleDialogClose = () => {
  productFormRef.value?.resetFields()
  Object.keys(productForm).forEach(key => {
    productForm[key] = key === 'status' ? 1 : (key === 'images' ? [] : null)
  })
  imageList.value = []
}

const handleImageChange = (file) => {
  // 这里可以处理图片上传逻辑
  // 暂时只是添加到列表显示
  if (!imageList.value.find(item => item.url === file.url)) {
    imageList.value.push(file)
  }
}

const handleImageRemove = (file) => {
  imageList.value = imageList.value.filter(item => item.url !== file.url)
}

const handleSubmit = async () => {
  try {
    await productFormRef.value.validate()
    submitting.value = true
    
    const data = {
      ...productForm,
      images: imageList.value.map(img => ({ imageUrl: img.url || img.imageUrl }))
    }
    
    if (productForm.id) {
      await updateProduct(productForm.id, data)
      ElMessage.success('更新成功')
    } else {
      await createProduct(data)
      ElMessage.success('创建成功')
    }
    
    dialogVisible.value = false
    loadProducts()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('操作失败')
    }
  } finally {
    submitting.value = false
  }
}

const handleDelete = async (row) => {
  try {
    await ElMessageBox.confirm('确定要删除该商品吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    await deleteProduct(row.id)
    ElMessage.success('删除成功')
    loadProducts()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('删除失败')
    }
  }
}
</script>

<style scoped>
.product-management {
  padding: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.search-bar {
  display: flex;
  align-items: center;
}

.price {
  color: #f56c6c;
  font-weight: bold;
}

.pagination {
  display: flex;
  justify-content: center;
  margin-top: 20px;
}

.image-upload {
  width: 100%;
}

.image-tip {
  margin-top: 10px;
  font-size: 12px;
  color: #999;
}
</style>
