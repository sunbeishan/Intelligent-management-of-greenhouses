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
          <router-link to="/environment" class="menu-item">
            <el-icon><monitor /></el-icon>
            <span>环境监测</span>
          </router-link>
          <router-link to="/farm" class="menu-item">
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
                  <el-dropdown-item>个人中心</el-dropdown-item>
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
          <el-table :data="expertConsultations" style="width: 100%" @row-click="viewConsultationDetail">
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
                  <div class="message-content">
                    <div class="message-sender">{{ message.sender === 'user' ? '用户' : '专家' }}</div>
                    <div class="message-text">{{ message.content }}</div>
                    <div class="message-time">{{ message.time }}</div>
                  </div>
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
import { ref, reactive, watch, nextTick, computed } from 'vue'
import { useRouter } from 'vue-router'
import { useConsultationStore } from './../stores/consultation.js'
import {
  ChatDotRound,
  Monitor,
  Location
} from '@element-plus/icons-vue'

const router = useRouter()
const consultationStore = useConsultationStore()
const dialogVisible = ref(false)
const replyContent = ref('')
const messageList = ref(null)

const expertName = ref('张教授')
const expertAvatar = ref('https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=professional%20agriculture%20expert%20portrait&image_size=square')

// 假设当前专家ID为1（张教授）
const currentExpertId = 1

// 获取当前专家的咨询列表
const expertConsultations = computed(() => {
  return consultationStore.getExpertConsultations(currentExpertId)
})

const currentConsultation = reactive({
  id: '',
  userName: '',
  subject: '',
  status: '',
  createTime: '',
  messages: []
})

const logout = () => {
  // 退出登录
  router.push('/expert/login')
}

const getStatusTag = (status) => {
  const tagMap = {
    '已回复': 'success',
    '处理中': 'warning',
    '待处理': 'info'
  }
  return tagMap[status] || 'default'
}

const viewConsultationDetail = (row) => {
  // 查看咨询详情
  Object.assign(currentConsultation, row)
  dialogVisible.value = true
  
  // 自动滚动到底部
  nextTick(() => {
    if (messageList.value) {
      messageList.value.scrollTop = messageList.value.scrollHeight
    }
  })
}

const sendReply = () => {
  if (!replyContent.value.trim()) return
  
  // 添加回复消息
  const newMessage = {
    sender: 'expert',
    content: replyContent.value,
    time: new Date().toLocaleString('zh-CN')
  }
  
  // 更新本地状态
  currentConsultation.messages.push(newMessage)
  currentConsultation.status = '已回复'
  
  // 更新全局状态
  consultationStore.addMessage(currentConsultation.id, newMessage)
  consultationStore.updateConsultationStatus(currentConsultation.id, '已回复')
  
  // 清空回复内容
  replyContent.value = ''
  
  // 自动滚动到底部
  nextTick(() => {
    if (messageList.value) {
      messageList.value.scrollTop = messageList.value.scrollHeight
    }
  })
}

// 监听消息变化，自动滚动到底部
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
  margin-bottom: 15px;
  display: flex;
}

.user-message {
  justify-content: flex-start;
}

.expert-message {
  justify-content: flex-end;
}

.message-content {
  max-width: 70%;
  padding: 10px 15px;
  border-radius: 8px;
  background-color: #fff;
  box-shadow: 0 1px 2px rgba(0, 0, 0, 0.1);
}

.expert-message .message-content {
  background-color: #e6f7ff;
}

.message-sender {
  font-size: 12px;
  color: #909399;
  margin-bottom: 4px;
}

.message-text {
  margin-bottom: 4px;
  line-height: 1.4;
}

.message-time {
  font-size: 11px;
  color: #c0c4cc;
  text-align: right;
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
