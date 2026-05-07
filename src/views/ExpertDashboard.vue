<template>
  <div class="expert-dashboard">
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
        </nav>
      </aside>
      
      <!-- 主内容区 -->
      <main class="main-content">
        <!-- 顶部导航 -->
        <header class="expert-header">
          <div class="header-left">
            <h2>回复模块</h2>
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

        <!-- 咨询列表 -->
        <el-card shadow="hover" class="consultation-list-card">
          <template #header>
            <div class="card-header">
              <span>待回复咨询</span>
            </div>
          </template>
          <el-table :data="expertConsultations" style="width: 100%" v-loading="loading" @row-click="viewConsultationDetail">
            <el-table-column prop="id" label="咨询编号" width="120"></el-table-column>
            <el-table-column prop="userName" label="用户" width="150"></el-table-column>
            <el-table-column prop="subject" label="咨询主题"></el-table-column>
            <el-table-column prop="status" label="状态" width="100">
              <template #default="scope">
                <el-tag :type="getStatusTag(scope.row.status)">{{ scope.row.status }}</el-tag>
              </template>
            </el-table-column>
            <el-table-column prop="createTime" label="创建时间" width="180"></el-table-column>
          </el-table>
        </el-card>

        <!-- 咨询详情 -->
        <el-dialog
          v-model="dialogVisible"
          :title="currentConsultation.subject || '咨询详情'"
          width="800px"
          top="5vh"
        >
          <div class="consultation-detail">
            <div class="detail-header">
              <p><strong>咨询编号：</strong>{{ currentConsultation.id }}</p>
              <p><strong>用户：</strong>{{ currentConsultation.userName }}</p>
              <p><strong>创建时间：</strong>{{ currentConsultation.createTime }}</p>
            </div>
            
            <div class="message-area">
              <h4>聊天记录</h4>
              <div class="message-list" ref="messageList">
                <div v-for="(message, index) in currentConsultation.messages" :key="index" class="message-item" :class="{ 'user-message': message.sender === 'user', 'expert-message': message.sender === 'expert' }">
                  <div class="message-sender">{{ message.sender === 'user' ? '用户' : '专家' }}</div>
                  <img v-if="message.sender === 'user'" src="https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=user%20avatar%20portrait&image_size=square" alt="用户头像" class="message-avatar">
                  <div class="message-content">
                    <div class="message-text">{{ message.content }}</div>
                    <div class="message-time">{{ message.time }}</div>
                  </div>
                  <img v-if="message.sender === 'expert'" :src="expertAvatar" alt="专家头像" class="message-avatar">
                </div>
              </div>
            </div>
            
            <div class="reply-area">
              <el-input
                v-model="replyContent"
                type="textarea"
                :rows="4"
                placeholder="请输入回复内容"
              ></el-input>
              <div class="reply-actions">
                <el-button @click="dialogVisible = false">取消</el-button>
                <el-button type="primary" @click="sendReply">发送回复</el-button>
              </div>
            </div>
          </div>
        </el-dialog>
      </main>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, onUnmounted, nextTick, watch } from 'vue'
import { useRouter } from 'vue-router'
import {
  ChatDotRound,
  Monitor,
  Location
} from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'

const router = useRouter()
const dialogVisible = ref(false)
const replyContent = ref('')
const messageList = ref(null)
const loading = ref(false)

const expertName = ref('')
const expertAvatar = ref('')
const currentExpertId = ref(0)

let consultationPollingTimer = null
let currentConsultationPollingTimer = null

const loadExpertInfo = () => {
  const expertStr = localStorage.getItem('expert')
  if (expertStr) {
    const expert = JSON.parse(expertStr)
    expertName.value = expert.name || expert.username
    expertAvatar.value = expert.avatar || 'https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=professional%20agriculture%20expert%20portrait&image_size=square'
    currentExpertId.value = expert.id
  }
}

const expertConsultations = ref([])

const currentConsultation = reactive({
  id: '',
  userName: '',
  subject: '',
  status: '',
  createTime: '',
  messages: []
})

const goToProfile = () => {
  router.push('/expert/profile')
}

const logout = () => {
  localStorage.removeItem('expert')
  router.push('/expert/login')
  ElMessage.success('已退出登录')
}

const getStatusTag = (status) => {
  const tagMap = {
    '已回复': 'success',
    '处理中': 'warning',
    '待处理': 'info'
  }
  return tagMap[status] || 'default'
}

const fetchExpertConsultations = async () => {
  loading.value = true
  try {
    const response = await fetch(`/api/consultations/expert/${currentExpertId.value}`)
    expertConsultations.value = await response.json()
  } catch (error) {
    ElMessage.error('获取咨询列表失败')
  } finally {
    loading.value = false
  }
}

const fetchCurrentConsultation = async () => {
  if (!currentConsultation.id || !dialogVisible.value) return
  try {
    const response = await fetch(`/api/consultations/${currentConsultation.id}`)
    const data = await response.json()
    const oldMessageCount = currentConsultation.messages.length
    Object.assign(currentConsultation, data)
    if (currentConsultation.messages.length > oldMessageCount) {
      nextTick(() => {
        if (messageList.value) {
          messageList.value.scrollTop = messageList.value.scrollHeight
        }
      })
    }
  } catch (error) {
    console.error('获取咨询详情失败', error)
  }
}

const startPolling = () => {
  consultationPollingTimer = setInterval(fetchExpertConsultations, 1000)
}

const stopPolling = () => {
  if (consultationPollingTimer) {
    clearInterval(consultationPollingTimer)
    consultationPollingTimer = null
  }
}

const startCurrentConsultationPolling = () => {
  currentConsultationPollingTimer = setInterval(fetchCurrentConsultation, 2000)
}

const stopCurrentConsultationPolling = () => {
  if (currentConsultationPollingTimer) {
    clearInterval(currentConsultationPollingTimer)
    currentConsultationPollingTimer = null
  }
}

onMounted(() => {
  loadExpertInfo()
  fetchExpertConsultations()
  startPolling()
})

onUnmounted(() => {
  stopPolling()
  stopCurrentConsultationPolling()
})

const viewConsultationDetail = async (row) => {
  const response = await fetch(`/api/consultations/${row.id}`)
  const data = await response.json()
  Object.assign(currentConsultation, data)
  dialogVisible.value = true
  startCurrentConsultationPolling()
  
  nextTick(() => {
    if (messageList.value) {
      messageList.value.scrollTop = messageList.value.scrollHeight
    }
  })
}

watch(dialogVisible, (newVal) => {
  if (!newVal) {
    stopCurrentConsultationPolling()
  }
})

const sendReply = async () => {
  if (!replyContent.value.trim()) return
  
  fetch('/api/messages', {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify({
      consultationId: currentConsultation.id,
      sender: 'expert',
      content: replyContent.value
    })
  }).then(response => {
    if (response.ok) {
      // 更新本地消息
      currentConsultation.messages.push({
        sender: 'expert',
        content: replyContent.value,
        time: new Date().toLocaleString('zh-CN')
      })
      replyContent.value = ''
      
      // 更新状态为已回复
      fetch('/api/consultations/status', {
        method: 'PUT',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ id: currentConsultation.id, status: '已回复' })
      }).then(() => {
        currentConsultation.status = '已回复'
        fetchExpertConsultations()
      })
      
      nextTick(() => {
        if (messageList.value) {
          messageList.value.scrollTop = messageList.value.scrollHeight
        }
      })
    } else {
      ElMessage.error('发送失败')
    }
  }).catch(() => {
    ElMessage.error('网络错误')
  })
}

watch(
  () => currentConsultation.messages,
  () => {
    nextTick(() => {
      if (messageList.value) {
        messageList.value.scrollTop = messageList.value.scrollHeight
      }
    })
  },
  { deep: true }
)
</script>

<style scoped>
.expert-dashboard {
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
  transition: width 0.3s ease;
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
  transition: opacity 0.3s ease;
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

.consultation-list-card {
  flex: 1;
  margin: 20px;
  display: flex;
  flex-direction: column;
}

.consultation-list-card :deep(.el-card__body) {
  flex: 1;
  overflow: auto;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.consultation-detail {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.detail-header {
  padding: 10px;
  background-color: #f5f7fa;
  border-radius: 4px;
}

.detail-header p {
  margin: 5px 0;
}

.message-area {
  flex: 1;
  min-height: 300px;
  display: flex;
  flex-direction: column;
}

.message-list {
  flex: 1;
  overflow: auto;
  padding: 10px;
  background-color: #f9f9f9;
  border-radius: 4px;
  max-height: 400px;
}

.message-item {
  margin-bottom: 20px;
  display: flex;
  align-items: flex-start;
  position: relative;
}

.user-message {
  justify-content: flex-start;
}

.expert-message {
  justify-content: flex-end;
}

.user-message .message-content {
  border-radius: 18px 18px 18px 4px;
  background-color: #ffffff;
  margin-left: 10px;
}

.expert-message .message-content {
  border-radius: 18px 18px 4px 18px;
  background-color: #92e478;
  margin-right: 10px;
}

.message-content {
  max-width: 70%;
  padding: 12px 16px;
  box-shadow: 0 1px 2px rgba(0, 0, 0, 0.08);
  position: relative;
  word-wrap: break-word;
}

.message-avatar {
  width: 36px;
  height: 36px;
  border-radius: 50%;
  object-fit: cover;
  flex-shrink: 0;
}

.message-sender {
  font-size: 12px;
  color: #909399;
  margin-bottom: 6px;
  padding: 0 52px;
  position: absolute;
  top: -18px;
  left: 0;
  width: 100%;
  text-align: left;
}

.expert-message .message-sender {
  text-align: right;
}

.message-text {
  line-height: 1.5;
  font-size: 14px;
}

.message-time {
  font-size: 11px;
  color: #c0c4cc;
  text-align: right;
  margin-top: 4px;
  padding-right: 6px;
}

.reply-area {
  margin-top: 20px;
}

.reply-actions {
  display: flex;
  justify-content: flex-end;
  gap: 10px;
  margin-top: 10px;
}
</style>
