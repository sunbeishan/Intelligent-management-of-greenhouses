<template>
  <div class="expert-profile">
    <div class="app-container">
      <!-- 侧边栏 -->
      <aside class="sidebar">
        <div class="sidebar-header">
          <h1 class="logo">专家控制台</h1>
        </div>
        <nav class="sidebar-menu">
          <router-link to="/expert/dashboard" class="menu-item">
            <el-icon><chat-dot-round /></el-icon>
            <span>回复模块</span>
          </router-link>
          <router-link to="/expert/environment" class="menu-item">
            <el-icon><monitor /></el-icon>
            <span>环境监测</span>
          </router-link>
          <router-link to="/expert/farm" class="menu-item">
            <el-icon><location /></el-icon>
            <span>农田信息管理</span>
          </router-link>
          <router-link to="/expert/profile" class="menu-item">
            <el-icon><user /></el-icon>
            <span>个人中心</span>
          </router-link>
        </nav>
      </aside>
      
      <!-- 主内容区 -->
      <main class="main-content">
        <!-- 顶部导航 -->
        <header class="expert-header">
          <div class="header-left">
            <h2>个人中心</h2>
          </div>
          <div class="header-right">
            <el-dropdown>
              <span class="user-info">
                <el-avatar :size="32" :src="expertAvatar"></el-avatar>
                <span>{{ expertName }}</span>
              </span>
              <template #dropdown>
                <el-dropdown-menu>
                  <el-dropdown-item @click="goToProfile">个人中心</el-dropdown-item>
                  <el-dropdown-item @click="logout">退出登录</el-dropdown-item>
                </el-dropdown-menu>
              </template>
            </el-dropdown>
          </div>
        </header>

        <!-- 个人信息卡片 -->
        <div class="profile-content">
          <el-card shadow="hover" class="profile-card">
            <div class="profile-header">
              <el-avatar :size="120" :src="expertAvatar" class="profile-avatar"></el-avatar>
              <div class="profile-info">
                <h2>{{ expertName }}</h2>
                <p class="profile-title">专家</p>
                <p class="profile-specialty">{{ expertInfo.specialty }}</p>
              </div>
            </div>
            
            <div class="profile-details">
              <h3>基本信息</h3>
              <div class="detail-row">
                <span class="detail-label">用户名</span>
                <span class="detail-value">{{ expertInfo.username }}</span>
              </div>
              <div class="detail-row">
                <span class="detail-label">姓名</span>
                <span class="detail-value">{{ expertInfo.name }}</span>
              </div>
              <div class="detail-row">
                <span class="detail-label">电话</span>
                <span class="detail-value">{{ expertInfo.phone || '未设置' }}</span>
              </div>
              <div class="detail-row">
                <span class="detail-label">邮箱</span>
                <span class="detail-value">{{ expertInfo.email || '未设置' }}</span>
              </div>
              <div class="detail-row">
                <span class="detail-label">专业领域</span>
                <span class="detail-value">{{ expertInfo.specialty }}</span>
              </div>
              <div class="detail-row">
                <span class="detail-label">状态</span>
                <span class="detail-value">
                  <el-tag :type="expertInfo.status === '启用' ? 'success' : 'danger'">{{ expertInfo.status }}</el-tag>
                </span>
              </div>
              <div class="detail-row">
                <span class="detail-label">创建时间</span>
                <span class="detail-value">{{ expertInfo.createTime }}</span>
              </div>
            </div>
            
            <div class="profile-actions">
              <el-button type="primary" @click="openEditDialog">编辑资料</el-button>
              <el-button @click="changePassword">修改密码</el-button>
            </div>
          </el-card>
        </div>
      </main>
    </div>

    <!-- 编辑资料对话框 -->
    <el-dialog v-model="editDialogVisible" title="编辑资料" width="500px">
      <el-form :model="editForm" :rules="editRules" ref="editFormRef" label-width="80px">
        <el-form-item label="姓名" prop="name">
          <el-input v-model="editForm.name"></el-input>
        </el-form-item>
        <el-form-item label="电话" prop="phone">
          <el-input v-model="editForm.phone"></el-input>
        </el-form-item>
        <el-form-item label="邮箱" prop="email">
          <el-input v-model="editForm.email"></el-input>
        </el-form-item>
        <el-form-item label="专业领域" prop="specialty">
          <el-input v-model="editForm.specialty"></el-input>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="editDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitEdit">保存修改</el-button>
      </template>
    </el-dialog>

    <!-- 修改密码对话框 -->
    <el-dialog v-model="passwordDialogVisible" title="修改密码" width="500px">
      <el-form :model="passwordForm" :rules="passwordRules" ref="passwordFormRef" label-width="100px">
        <el-form-item label="原密码" prop="oldPassword">
          <el-input v-model="passwordForm.oldPassword" type="password"></el-input>
        </el-form-item>
        <el-form-item label="新密码" prop="newPassword">
          <el-input v-model="passwordForm.newPassword" type="password"></el-input>
        </el-form-item>
        <el-form-item label="确认密码" prop="confirmPassword">
          <el-input v-model="passwordForm.confirmPassword" type="password"></el-input>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="passwordDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitPassword">确认修改</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import {
  ChatDotRound,
  Monitor,
  Location,
  User
} from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'

const router = useRouter()

const expertName = ref('')
const expertAvatar = ref('')
const expertInfo = reactive({
  id: 0,
  username: '',
  name: '',
  phone: '',
  email: '',
  specialty: '',
  status: '',
  createTime: ''
})

const editDialogVisible = ref(false)
const passwordDialogVisible = ref(false)
const editFormRef = ref(null)
const passwordFormRef = ref(null)

const editForm = reactive({
  name: '',
  phone: '',
  email: '',
  specialty: ''
})

const passwordForm = reactive({
  oldPassword: '',
  newPassword: '',
  confirmPassword: ''
})

const editRules = {
  name: [{ required: true, message: '请输入姓名', trigger: 'blur' }],
  specialty: [{ required: true, message: '请输入专业领域', trigger: 'blur' }]
}

const passwordRules = {
  oldPassword: [{ required: true, message: '请输入原密码', trigger: 'blur' }],
  newPassword: [{ required: true, message: '请输入新密码', trigger: 'blur' }],
  confirmPassword: [{ required: true, message: '请确认新密码', trigger: 'blur' }]
}

const loadExpertInfo = async () => {
  const expertStr = localStorage.getItem('expert')
  if (expertStr) {
    const expert = JSON.parse(expertStr)
    expertName.value = expert.name || expert.username
    expertAvatar.value = expert.avatar || 'https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=professional%20expert%20portrait&image_size=square'
    expertInfo.id = expert.id
    expertInfo.username = expert.username
    expertInfo.name = expert.name
    expertInfo.phone = expert.phone
    expertInfo.email = expert.email
    expertInfo.specialty = expert.specialty
    expertInfo.status = expert.status
    expertInfo.createTime = expert.createTime
  }
}

const goToProfile = () => {
  router.push('/expert/profile')
}

const logout = () => {
  localStorage.removeItem('expert')
  router.push('/expert/login')
  ElMessage.success('已退出登录')
}

const openEditDialog = () => {
  editForm.name = expertInfo.name
  editForm.phone = expertInfo.phone || ''
  editForm.email = expertInfo.email || ''
  editForm.specialty = expertInfo.specialty
  editDialogVisible.value = true
}

const submitEdit = async () => {
  try {
    await editFormRef.value.validate()
    const response = await fetch(`/api/experts/${expertInfo.id}`, {
      method: 'PUT',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({
        id: expertInfo.id,
        username: expertInfo.username,
        name: editForm.name,
        phone: editForm.phone,
        email: editForm.email,
        specialty: editForm.specialty,
        status: expertInfo.status
      })
    })
    if (response.ok) {
      ElMessage.success('修改成功')
      editDialogVisible.value = false
      expertInfo.name = editForm.name
      expertInfo.phone = editForm.phone
      expertInfo.email = editForm.email
      expertInfo.specialty = editForm.specialty
      expertName.value = editForm.name
      const expertStr = localStorage.getItem('expert')
      if (expertStr) {
        const expert = JSON.parse(expertStr)
        expert.name = editForm.name
        expert.phone = editForm.phone
        expert.email = editForm.email
        expert.specialty = editForm.specialty
        localStorage.setItem('expert', JSON.stringify(expert))
      }
    } else {
      ElMessage.error('修改失败')
    }
  } catch (error) {
    console.error(error)
  }
}

const changePassword = () => {
  passwordForm.oldPassword = ''
  passwordForm.newPassword = ''
  passwordForm.confirmPassword = ''
  passwordDialogVisible.value = true
}

const submitPassword = async () => {
  if (passwordForm.newPassword !== passwordForm.confirmPassword) {
    ElMessage.error('两次输入的密码不一致')
    return
  }
  
  try {
    await passwordFormRef.value.validate()
    const response = await fetch(`/api/experts/${expertInfo.id}`, {
      method: 'PUT',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({
        id: expertInfo.id,
        username: expertInfo.username,
        password: passwordForm.newPassword,
        name: expertInfo.name,
        phone: expertInfo.phone,
        email: expertInfo.email,
        specialty: expertInfo.specialty,
        status: expertInfo.status
      })
    })
    if (response.ok) {
      ElMessage.success('密码修改成功')
      passwordDialogVisible.value = false
    } else {
      ElMessage.error('密码修改失败')
    }
  } catch (error) {
    console.error(error)
  }
}

onMounted(() => {
  loadExpertInfo()
})
</script>

<style scoped>
.expert-profile {
  height: 100vh;
  overflow: hidden;
}

.app-container {
  display: flex;
  height: 100vh;
  overflow: hidden;
}

.sidebar {
  width: 240px;
  background-color: #2c3e50;
  color: #fff;
  display: flex;
  flex-direction: column;
}

.sidebar-header {
  display: flex;
  align-items: center;
  padding: 20px;
  border-bottom: 1px solid rgba(255, 255, 255, 0.1);
}

.logo {
  font-size: 18px;
  font-weight: bold;
  margin: 0;
}

.sidebar-menu {
  flex: 1;
  padding: 20px 0;
}

.menu-item {
  display: flex;
  align-items: center;
  padding: 12px 20px;
  color: #ecf0f1;
  text-decoration: none;
  transition: all 0.3s ease;
}

.menu-item:hover {
  background-color: rgba(255, 255, 255, 0.1);
}

.menu-item.router-link-active {
  background-color: #3498db;
  color: #fff;
}

.menu-item span {
  margin-left: 12px;
}

.main-content {
  flex: 1;
  display: flex;
  flex-direction: column;
  background-color: #f5f7fa;
  overflow: hidden;
}

.expert-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 20px;
  height: 64px;
  background-color: #fff;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
  flex-shrink: 0;
}

.header-left h2 {
  font-size: 18px;
  font-weight: 600;
  margin: 0;
}

.user-info {
  display: flex;
  align-items: center;
  cursor: pointer;
}

.user-info span {
  margin-left: 8px;
}

.profile-content {
  flex: 1;
  padding: 20px;
  overflow: auto;
}

.profile-card {
  max-width: 600px;
  margin: 0 auto;
}

.profile-header {
  display: flex;
  align-items: center;
  gap: 24px;
  padding: 20px;
  border-bottom: 1px solid #ebeef5;
}

.profile-avatar {
  border: 4px solid #f0f0f0;
}

.profile-info h2 {
  margin: 0 0 8px 0;
  font-size: 24px;
}

.profile-title {
  margin: 0 0 8px 0;
  color: #606266;
  font-size: 14px;
}

.profile-specialty {
  margin: 0;
  color: #909399;
  font-size: 14px;
}

.profile-details {
  padding: 20px;
}

.profile-details h3 {
  margin: 0 0 16px 0;
  font-size: 16px;
  font-weight: 600;
}

.detail-row {
  display: flex;
  justify-content: space-between;
  padding: 12px 0;
  border-bottom: 1px dashed #ebeef5;
}

.detail-label {
  color: #909399;
}

.detail-value {
  font-weight: 500;
}

.profile-actions {
  padding: 20px;
  display: flex;
  gap: 12px;
  justify-content: center;
}
</style>