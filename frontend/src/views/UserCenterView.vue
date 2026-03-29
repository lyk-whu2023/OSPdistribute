<template>
  <div class="user-center">
    <el-row :gutter="20">
      <el-col :span="6">
        <el-card>
          <el-menu default-active="1" @select="handleMenuSelect">
            <el-menu-item index="1">
              <el-icon><User /></el-icon>
              <span>个人信息</span>
            </el-menu-item>
            <el-menu-item index="2">
              <el-icon><Location /></el-icon>
              <span>地址管理</span>
            </el-menu-item>
          </el-menu>
        </el-card>
      </el-col>

      <el-col :span="18">
        <!-- 个人信息 -->
        <el-card v-show="activeTab === '1'">
          <template #header>
            <span>个人信息</span>
          </template>
          <el-form
            ref="profileFormRef"
            :model="profileForm"
            :rules="profileRules"
            label-width="100px"
          >
            <el-form-item label="用户名">
              <el-input v-model="profileForm.username" disabled />
            </el-form-item>
            <el-form-item label="昵称" prop="nickname">
              <el-input v-model="profileForm.nickname" placeholder="请输入昵称" />
            </el-form-item>
            <el-form-item label="邮箱" prop="email">
              <el-input v-model="profileForm.email" placeholder="请输入邮箱" />
            </el-form-item>
            <el-form-item label="手机号" prop="phone">
              <el-input v-model="profileForm.phone" placeholder="请输入手机号" />
            </el-form-item>
            <el-form-item label="性别">
              <el-radio-group v-model="profileForm.gender">
                <el-radio :value="0">未知</el-radio>
                <el-radio :value="1">男</el-radio>
                <el-radio :value="2">女</el-radio>
              </el-radio-group>
            </el-form-item>
            <el-form-item>
              <el-button type="primary" @click="handleUpdateProfile">
                保存
              </el-button>
            </el-form-item>
          </el-form>
        </el-card>

        <!-- 地址管理 -->
        <el-card v-show="activeTab === '2'">
          <template #header>
            <div class="card-header">
              <span>地址管理</span>
              <el-button type="primary" @click="showAddressDialog = true">
                新增地址
              </el-button>
            </div>
          </template>
          <el-table :data="addressList" style="width: 100%">
            <el-table-column prop="name" label="收货人" />
            <el-table-column prop="phone" label="手机号" />
            <el-table-column prop="detail" label="详细地址" />
            <el-table-column label="是否默认">
              <template #default="{ row }">
                <el-tag v-if="row.isDefault === 1" type="success">默认</el-tag>
                <span v-else>-</span>
              </template>
            </el-table-column>
            <el-table-column label="操作" width="200">
              <template #default="{ row }">
                <el-button
                  v-if="row.isDefault !== 1"
                  size="small"
                  @click="handleSetDefault(row)"
                >
                  设为默认
                </el-button>
                <el-button size="small" @click="handleEditAddress(row)">
                  编辑
                </el-button>
                <el-button
                  size="small"
                  type="danger"
                  @click="handleDeleteAddress(row)"
                >
                  删除
                </el-button>
              </template>
            </el-table-column>
          </el-table>
        </el-card>
      </el-col>
    </el-row>

    <!-- 地址对话框 -->
    <el-dialog
      v-model="showAddressDialog"
      :title="addressForm.id ? '编辑地址' : '新增地址'"
      width="500px"
    >
      <el-form
        ref="addressFormRef"
        :model="addressForm"
        :rules="addressRules"
        label-width="80px"
      >
        <el-form-item label="收货人" prop="name">
          <el-input v-model="addressForm.name" placeholder="请输入收货人姓名" />
        </el-form-item>
        <el-form-item label="手机号" prop="phone">
          <el-input v-model="addressForm.phone" placeholder="请输入手机号" />
        </el-form-item>
        <el-form-item label="省份" prop="province">
          <el-input v-model="addressForm.province" placeholder="请输入省份" />
        </el-form-item>
        <el-form-item label="城市" prop="city">
          <el-input v-model="addressForm.city" placeholder="请输入城市" />
        </el-form-item>
        <el-form-item label="区县" prop="district">
          <el-input v-model="addressForm.district" placeholder="请输入区县" />
        </el-form-item>
        <el-form-item label="详细地址" prop="detail">
          <el-input
            v-model="addressForm.detail"
            type="textarea"
            :rows="3"
            placeholder="请输入详细地址"
          />
        </el-form-item>
        <el-form-item label="默认地址">
          <el-switch v-model="addressForm.isDefault" :active-value="1" :inactive-value="0" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="showAddressDialog = false">取消</el-button>
        <el-button type="primary" @click="handleSaveAddress">
          确定
        </el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { User, Location } from '@element-plus/icons-vue'
import { useUserStore } from '@/store/modules/user'
import {
  getUserInfo,
  updateUserInfo,
  getUserAddresses,
  createAddress,
  updateAddress,
  deleteAddress,
  setDefaultAddress
} from '@/api/user'

const userStore = useUserStore()

const activeTab = ref('1')
const showAddressDialog = ref(false)

const profileForm = reactive({
  id: null,
  username: '',
  nickname: '',
  email: '',
  phone: '',
  gender: 0
})

const addressList = ref([])
const addressForm = reactive({
  id: null,
  userId: null,
  name: '',
  phone: '',
  province: '',
  city: '',
  district: '',
  detail: '',
  isDefault: 0
})

const profileFormRef = ref(null)
const addressFormRef = ref(null)

const profileRules = {
  nickname: [{ required: true, message: '请输入昵称', trigger: 'blur' }],
  email: [
    { required: true, message: '请输入邮箱', trigger: 'blur' },
    { type: 'email', message: '邮箱格式不正确', trigger: 'blur' }
  ],
  phone: [
    { required: true, message: '请输入手机号', trigger: 'blur' },
    { pattern: /^1[3-9]\d{9}$/, message: '手机号格式不正确', trigger: 'blur' }
  ]
}

const addressRules = {
  name: [{ required: true, message: '请输入收货人姓名', trigger: 'blur' }],
  phone: [
    { required: true, message: '请输入手机号', trigger: 'blur' },
    { pattern: /^1[3-9]\d{9}$/, message: '手机号格式不正确', trigger: 'blur' }
  ],
  province: [{ required: true, message: '请输入省份', trigger: 'blur' }],
  city: [{ required: true, message: '请输入城市', trigger: 'blur' }],
  district: [{ required: true, message: '请输入区县', trigger: 'blur' }],
  detail: [{ required: true, message: '请输入详细地址', trigger: 'blur' }]
}

const handleMenuSelect = (index) => {
  activeTab.value = index
}

onMounted(async () => {
  await loadUserInfo()
  await loadAddresses()
})

const loadUserInfo = async () => {
  try {
    const userInfo = await getUserInfo(userStore.userId)
    Object.assign(profileForm, userInfo)
  } catch (error) {
    ElMessage.error('加载用户信息失败')
  }
}

const loadAddresses = async () => {
  try {
    addressList.value = await getUserAddresses(userStore.userId)
  } catch (error) {
    ElMessage.error('加载地址列表失败')
  }
}

const handleUpdateProfile = async () => {
  try {
    await profileFormRef.value.validate()
    await updateUserInfo(profileForm.id, profileForm)
    ElMessage.success('保存成功')
    userStore.setUserInfo(profileForm)
  } catch (error) {
    ElMessage.error('保存失败')
  }
}

const handleSetDefault = async (address) => {
  try {
    await setDefaultAddress(userStore.userId, address.id)
    ElMessage.success('设置成功')
    await loadAddresses()
  } catch (error) {
    ElMessage.error('设置失败')
  }
}

const handleEditAddress = (address) => {
  Object.assign(addressForm, address)
  addressForm.userId = userStore.userId
  showAddressDialog.value = true
}

const handleDeleteAddress = async (address) => {
  try {
    await ElMessageBox.confirm('确定要删除该地址吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    await deleteAddress(address.id)
    ElMessage.success('删除成功')
    await loadAddresses()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('删除失败')
    }
  }
}

const handleSaveAddress = async () => {
  try {
    await addressFormRef.value.validate()
    if (addressForm.id) {
      await updateAddress(addressForm.id, addressForm)
    } else {
      await createAddress(userStore.userId, addressForm)
    }
    ElMessage.success('保存成功')
    showAddressDialog.value = false
    resetAddressForm()
    await loadAddresses()
  } catch (error) {
    ElMessage.error('保存失败')
  }
}

const resetAddressForm = () => {
  Object.assign(addressForm, {
    id: null,
    userId: null,
    name: '',
    phone: '',
    province: '',
    city: '',
    district: '',
    detail: '',
    isDefault: 0
  })
}
</script>

<style scoped>
.user-center {
  padding: 20px;
  max-width: 1200px;
  margin: 0 auto;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.el-menu {
  border-right: none;
}

.el-card {
  margin-bottom: 20px;
}
</style>