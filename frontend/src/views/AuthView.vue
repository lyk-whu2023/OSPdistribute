<template>
  <div class="auth-container">
    <el-card class="auth-card">
      <template #header>
        <div class="card-header">
          <span>{{ isLogin ? '用户登录' : '用户注册' }}</span>
          <el-button link @click="toggleMode">
            {{ isLogin ? '没有账号？去注册' : '已有账号？去登录' }}
          </el-button>
        </div>
      </template>

      <!-- 登录表单 -->
      <el-form
        v-if="isLogin"
        ref="loginFormRef"
        :model="loginForm"
        :rules="loginRules"
        label-width="80px"
      >
        <el-form-item label="用户名" prop="username">
          <el-input v-model="loginForm.username" placeholder="请输入用户名" />
        </el-form-item>
        <el-form-item label="密码" prop="password">
          <el-input
            v-model="loginForm.password"
            type="password"
            placeholder="请输入密码"
            show-password
          />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" :loading="loading" @click="handleLogin">
            登录
          </el-button>
        </el-form-item>
      </el-form>

      <!-- 注册表单 -->
      <el-form
        v-else
        ref="registerFormRef"
        :model="registerForm"
        :rules="registerRules"
        label-width="80px"
      >
        <el-form-item label="用户名" prop="username">
          <el-input v-model="registerForm.username" placeholder="请输入用户名" />
        </el-form-item>
        <el-form-item label="密码" prop="password">
          <el-input
            v-model="registerForm.password"
            type="password"
            placeholder="请输入密码"
            show-password
          />
        </el-form-item>
        <el-form-item label="手机号" prop="phone">
          <el-input v-model="registerForm.phone" placeholder="请输入手机号" />
        </el-form-item>
        <el-form-item label="邮箱" prop="email">
          <el-input v-model="registerForm.email" placeholder="请输入邮箱" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" :loading="loading" @click="handleRegister">
            注册
          </el-button>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive, computed } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { useUserStore } from '@/store/modules/user'

const router = useRouter()
const userStore = useUserStore()

const isLogin = ref(true)
const loading = ref(false)

const loginFormRef = ref(null)
const registerFormRef = ref(null)

const loginForm = reactive({
  username: '',
  password: ''
})

const registerForm = reactive({
  username: '',
  password: '',
  phone: '',
  email: ''
})

const loginRules = {
  username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
  password: [{ required: true, message: '请输入密码', trigger: 'blur' }]
}

const registerRules = {
  username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 6, message: '密码长度至少 6 位', trigger: 'blur' }
  ],
  phone: [
    { required: true, message: '请输入手机号', trigger: 'blur' },
    { pattern: /^1[3-9]\d{9}$/, message: '手机号格式不正确', trigger: 'blur' }
  ],
  email: [
    { required: true, message: '请输入邮箱', trigger: 'blur' },
    { type: 'email', message: '邮箱格式不正确', trigger: 'blur' }
  ]
}

const toggleMode = () => {
  isLogin.value = !isLogin.value
}

const handleLogin = async () => {
  try {
    await loginFormRef.value.validate()
    loading.value = true
    const response = await userStore.login(loginForm)
    ElMessage.success('登录成功')
    
    // 获取用户信息
    const userId = response.userId || response.id
    if (userId) {
      await userStore.getUserInfo(userId)
    }
    
    // 跳转到首页或重定向页面
    const redirect = router.currentRoute.value.query.redirect
    router.push(redirect || '/')
  } catch (error) {
    ElMessage.error(error.response?.data?.message || '登录失败')
  } finally {
    loading.value = false
  }
}

const handleRegister = async () => {
  try {
    await registerFormRef.value.validate()
    loading.value = true
    await userStore.register(registerForm)
    ElMessage.success('注册成功，请登录')
    isLogin.value = true
    // 清空注册表单
    Object.assign(registerForm, {
      username: '',
      password: '',
      phone: '',
      email: ''
    })
  } catch (error) {
    ElMessage.error(error.response?.data?.message || '注册失败')
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.auth-container {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 100vh;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
}

.auth-card {
  width: 450px;
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.1);
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  font-size: 18px;
  font-weight: bold;
}

.el-button {
  width: 100%;
}
</style>