<template>
  <div class="address-view">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>收货地址</span>
          <el-button type="primary" @click="showDialog = true">
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
            <el-button size="small" @click="handleEdit(row)">
              编辑
            </el-button>
            <el-button
              size="small"
              type="danger"
              @click="handleDelete(row)"
            >
              删除
            </el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <el-dialog
      v-model="showDialog"
      :title="addressForm.id ? '编辑地址' : '新增地址'"
      width="500px"
    >
      <el-form ref="formRef" :model="addressForm" :rules="rules" label-width="80px">
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
          <el-input v-model="addressForm.detail" type="textarea" :rows="3" placeholder="请输入详细地址" />
        </el-form-item>
        <el-form-item label="默认地址">
          <el-switch v-model="addressForm.isDefault" :active-value="1" :inactive-value="0" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="showDialog = false">取消</el-button>
        <el-button type="primary" @click="handleSave">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { useUserStore } from '@/store/modules/user'
import {
  getUserAddresses,
  createAddress,
  updateAddress,
  deleteAddress,
  setDefaultAddress
} from '@/api/user'

const userStore = useUserStore()
const showDialog = ref(false)
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

const formRef = ref(null)

const rules = {
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

onMounted(() => {
  loadAddresses()
})

const loadAddresses = async () => {
  try {
    addressList.value = await getUserAddresses(userStore.userId)
  } catch (error) {
    ElMessage.error('加载地址列表失败')
  }
}

const handleSetDefault = async (row) => {
  try {
    await setDefaultAddress(userStore.userId, row.id)
    ElMessage.success('设置成功')
    loadAddresses()
  } catch (error) {
    ElMessage.error('设置失败')
  }
}

const handleEdit = (row) => {
  Object.assign(addressForm, row)
  showDialog.value = true
}

const handleDelete = async (row) => {
  try {
    await ElMessageBox.confirm('确定删除该地址吗？', '提示', {
      type: 'warning'
    })
    await deleteAddress(row.id)
    ElMessage.success('删除成功')
    loadAddresses()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('删除失败')
    }
  }
}

const handleSave = async () => {
  try {
    await formRef.value.validate()
    if (addressForm.id) {
      await updateAddress(addressForm.id, addressForm)
    } else {
      await createAddress(userStore.userId, addressForm)
    }
    ElMessage.success('保存成功')
    showDialog.value = false
    resetForm()
    loadAddresses()
  } catch (error) {
    ElMessage.error('保存失败')
  }
}

const resetForm = () => {
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
.address-view {
  padding: 20px;
  max-width: 1200px;
  margin: 0 auto;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
</style>