<template>
  <div class="profile">
    <el-card shadow="hover" class="profile-card">
      <template #header>
        <div class="card-header">
          <span>个人中心</span>
        </div>
      </template>
      
      <div class="profile-content">
        <!-- 个人信息 -->
        <div class="profile-section">
          <h3>基本信息</h3>
          <div class="profile-form">
            <el-form :model="userForm" label-width="100px">
              <el-form-item label="用户名">
                <el-input v-model="userForm.username" readonly></el-input>
              </el-form-item>
              <el-form-item label="姓名">
                <el-input v-model="userForm.name"></el-input>
              </el-form-item>
              <el-form-item label="邮箱">
                <el-input v-model="userForm.email"></el-input>
              </el-form-item>
              <el-form-item label="手机号">
                <el-input v-model="userForm.phone"></el-input>
              </el-form-item>
              <el-form-item label="角色">
                <el-input v-model="userForm.role" readonly></el-input>
              </el-form-item>
              <el-form-item label="最后登录">
                <el-input v-model="userForm.lastLogin" readonly></el-input>
              </el-form-item>
            </el-form>
          </div>
        </div>

        <!-- 密码修改 -->
        <div class="profile-section">
          <h3>密码修改</h3>
          <div class="profile-form">
            <el-form :model="passwordForm" label-width="100px">
              <el-form-item label="原密码">
                <el-input v-model="passwordForm.oldPassword" type="password"></el-input>
              </el-form-item>
              <el-form-item label="新密码">
                <el-input v-model="passwordForm.newPassword" type="password"></el-input>
              </el-form-item>
              <el-form-item label="确认密码">
                <el-input v-model="passwordForm.confirmPassword" type="password"></el-input>
              </el-form-item>
            </el-form>
          </div>
        </div>

        <!-- 操作按钮 -->
        <div class="profile-actions">
          <el-button type="primary" @click="saveProfile">保存修改</el-button>
          <el-button type="info" @click="resetForm">重置</el-button>
          <el-button type="danger" @click="logout">退出登录</el-button>
        </div>
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { useAppStore } from '../stores/index.js'

const router = useRouter()
const store = useAppStore()

const userForm = ref({
  username: '',
  name: '',
  email: '',
  phone: '',
  role: '',
  lastLogin: ''
})

const passwordForm = ref({
  oldPassword: '',
  newPassword: '',
  confirmPassword: ''
})

const loadUserInfo = () => {
  // 从store获取用户信息
  const user = store.user
  
  if (user) {
    userForm.value = {
      username: user.username || 'admin',
      name: user.name || '管理员',
      email: user.email || 'admin@example.com',
      phone: user.phone || '13800138000',
      role: user.role || '管理员',
      lastLogin: user.lastLogin || new Date().toLocaleString('zh-CN')
    }
  }
}

const saveProfile = () => {
  // 模拟保存操作
  ElMessage.success('个人信息保存成功')
  
  // 更新store中的用户信息
  store.setUser({
    ...store.user,
    name: userForm.value.name,
    email: userForm.value.email,
    phone: userForm.value.phone
  })
}

const resetForm = () => {
  loadUserInfo()
  passwordForm.value = {
    oldPassword: '',
    newPassword: '',
    confirmPassword: ''
  }
  ElMessage.info('表单已重置')
}

const logout = () => {
  // 退出登录
  store.logout()
  router.push('/login')
  ElMessage.success('已退出登录')
}

onMounted(() => {
  loadUserInfo()
})
</script>

<style scoped>
.profile {
  height: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 20px;
  box-sizing: border-box;
}

.profile-card {
  width: 100%;
  max-width: 600px;
  margin: 0 auto;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.profile-content {
  display: flex;
  flex-direction: column;
  gap: 30px;
}

.profile-section h3 {
  margin: 0 0 20px 0;
  font-size: 16px;
  font-weight: 600;
  color: #303133;
  border-bottom: 1px solid #e4e7ed;
  padding-bottom: 10px;
}

.profile-form {
  margin-bottom: 20px;
}

.profile-actions {
  display: flex;
  justify-content: center;
  gap: 10px;
  margin-top: 20px;
}

@media (max-width: 768px) {
  .profile-card {
    width: 100%;
  }
}
</style>
