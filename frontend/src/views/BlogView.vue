<template>
  <div class="blog-view">
    <el-card class="blog-header">
      <h1>博客文章</h1>
      <p>分享购物心得、产品评测和使用技巧</p>
    </el-card>

    <el-row :gutter="20" class="blog-list">
      <el-col
        v-for="blog in blogList"
        :key="blog.id"
        :xs="24"
        :sm="12"
        :md="8"
      >
        <el-card class="blog-card" shadow="hover" @click="goToDetail(blog.id)">
          <div class="blog-image">
            <img :src="blog.coverImage || generateBlogImage(blog.id)" :alt="blog.title" />
          </div>
          <div class="blog-content">
            <h3 class="blog-title">{{ blog.title }}</h3>
            <p class="blog-summary">{{ blog.summary || blog.content?.substring(0, 100) }}...</p>
            <div class="blog-meta">
              <span>
                <el-icon><User /></el-icon>
                {{ blog.authorName || '管理员' }}
              </span>
              <span>
                <el-icon><View /></el-icon>
                {{ blog.viewCount || 0 }}
              </span>
              <span>
                <el-icon><ChatDotRound /></el-icon>
                {{ blog.commentCount || 0 }}
              </span>
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
        :page-sizes="[9, 18, 27]"
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
import { getBlogs } from '@/api/blog'
import { generateBlogImage } from '@/utils/image'
import { ElMessage } from 'element-plus'

const router = useRouter()

const blogList = ref([])
const currentPage = ref(1)
const pageSize = ref(9)
const total = ref(0)

onMounted(async () => {
  await loadBlogs()
})

const loadBlogs = async () => {
  try {
    const response = await getBlogs({
      page: currentPage.value,
      size: pageSize.value
    })
    blogList.value = response.records || response.content || []
    total.value = response.total || 0
  } catch (error) {
    console.error('加载博客列表失败', error)
    ElMessage.warning('博客服务暂时不可用，显示示例数据')
    // 显示示例数据
    blogList.value = generateSampleBlogs()
    total.value = blogList.value.length
  }
}

const generateSampleBlogs = () => {
  return Array.from({ length: 6 }, (_, index) => ({
    id: index + 1,
    title: `精选博客文章 ${index + 1}`,
    summary: '这是一篇精彩的博客文章，介绍了最新的产品信息和使用技巧。欢迎大家阅读并留言讨论。',
    content: '博客内容详情...',
    authorName: '管理员',
    viewCount: Math.floor(Math.random() * 1000),
    commentCount: Math.floor(Math.random() * 50),
    coverImage: generateBlogImage(index + 1)
  }))
}

const handleSizeChange = (val) => {
  pageSize.value = val
  loadBlogs()
}

const handleCurrentChange = (val) => {
  currentPage.value = val
  loadBlogs()
}

const goToDetail = (id) => {
  router.push(`/blog/${id}`)
}
</script>

<style scoped>
.blog-view {
  padding: 20px;
  max-width: 1400px;
  margin: 0 auto;
}

.blog-header {
  text-align: center;
  margin-bottom: 30px;
}

.blog-header h1 {
  margin: 0 0 10px;
  color: #333;
}

.blog-header p {
  margin: 0;
  color: #666;
}

.blog-list {
  margin-bottom: 30px;
}

.blog-card {
  margin-bottom: 20px;
  cursor: pointer;
  transition: all 0.3s;
}

.blog-card:hover {
  transform: translateY(-5px);
}

.blog-image {
  width: 100%;
  height: 200px;
  overflow: hidden;
}

.blog-image img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  transition: transform 0.3s;
}

.blog-card:hover .blog-image img {
  transform: scale(1.1);
}

.blog-content {
  padding: 15px 0 0;
}

.blog-title {
  font-size: 16px;
  font-weight: bold;
  margin: 0 0 10px;
  color: #333;
  overflow: hidden;
  text-overflow: ellipsis;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
}

.blog-summary {
  font-size: 14px;
  color: #666;
  margin: 0 0 15px;
  overflow: hidden;
  text-overflow: ellipsis;
  display: -webkit-box;
  -webkit-line-clamp: 3;
  -webkit-box-orient: vertical;
  line-height: 1.6;
}

.blog-meta {
  display: flex;
  gap: 15px;
  font-size: 13px;
  color: #999;
}

.blog-meta span {
  display: flex;
  align-items: center;
  gap: 5px;
}

.pagination {
  display: flex;
  justify-content: center;
  margin-top: 20px;
}
</style>
