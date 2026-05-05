<template>
  <div class="expert">
    <!-- 操作按钮 -->
    <div class="action-bar">
      <el-button type="primary" @click="openConsultDialog">
        <el-icon><plus /></el-icon>
        发起咨询
      </el-button>
    </div>

    <!-- 专家列表 -->
    <el-card shadow="hover" class="expert-list-card">
      <template #header>
        <div class="card-header">
          <span>专家列表</span>
        </div>
      </template>
      <div class="expert-grid">
        <el-card v-for="expert in experts" :key="expert.id" shadow="hover" class="expert-card">
          <div class="expert-content">
            <el-avatar :size="80" :src="expert.avatar"></el-avatar>
            <div class="expert-info">
              <h3>{{ expert.name }}</h3>
              <p class="expert-title">{{ expert.title }}</p>
              <p class="expert-specialty">{{ expert.specialty }}</p>
              <el-button type="primary" size="small" @click="openConsultDialog(expert)">
                立即咨询
              </el-button>
            </div>
          </div>
        </el-card>
      </div>
    </el-card>

    <!-- 咨询记录 -->
    <el-card shadow="hover" class="consultation-card">
      <template #header>
        <div class="card-header">
          <span>咨询记录</span>
        </div>
      </template>
      <el-table :data="consultations" style="width: 100%" v-loading="loading" @row-click="viewConsultation">
        <el-table-column prop="id" label="咨询编号" width="120"></el-table-column>
        <el-table-column prop="expertName" label="专家" width="150"></el-table-column>
        <el-table-column prop="subject" label="咨询主题"></el-table-column>
        <el-table-column prop="status" label="状态" width="100">
          <template #default="scope">
            <el-tag :type="getConsultationStatusTag(scope.row.status)">{{ scope.row.status }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="创建时间" width="180"></el-table-column>
      </el-table>
    </el-card>

    <!-- 发起咨询对话框 -->
    <el-dialog
      v-model="consultDialogVisible"
      title="发起咨询"
      width="600px"
    >
      <el-form :model="consultForm" :rules="consultRules" ref="consultFormRef" label-width="80px">
        <el-form-item label="选择专家" prop="expertId">
          <el-select v-model="consultForm.expertId" placeholder="请选择专家">
            <el-option v-for="expert in experts" :key="expert.id" :label="expert.name + ' - ' + expert.specialty" :value="expert.id"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="咨询主题" prop="subject">
          <el-input v-model="consultForm.subject" placeholder="请输入咨询主题"></el-input>
        </el-form-item>
        <el-form-item label="咨询内容" prop="content">
          <el-input v-model="consultForm.content" type="textarea" :rows="4" placeholder="请详细描述您的问题"></el-input>
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="consultDialogVisible = false">取消</el-button>
          <el-button type="primary" @click="submitConsultation">提交咨询</el-button>
        </span>
      </template>
    </el-dialog>

    <!-- 咨询详情对话框 -->
    <el-dialog
      v-model="detailDialogVisible"
      :title="currentConsultation.subject || '咨询详情'"
      width="800px"
      top="5vh"
    >
      <div class="consultation-detail">
        <div class="detail-header">
          <p><strong>咨询编号：</strong>{{ currentConsultation.id }}</p>
          <p><strong>专家：</strong>{{ currentConsultation.expertName }}</p>
          <p><strong>创建时间：</strong>{{ currentConsultation.createTime }}</p>
        </div>
        
        <div class="message-area">
          <h4>聊天记录</h4>
          <div class="message-list" ref="messageList">
            <div v-for="(message, index) in currentConsultation.messages" :key="index" class="message-item" :class="{ 'user-message': message.sender === 'user', 'expert-message': message.sender === 'expert' }">
              <div class="message-sender">{{ message.sender === 'user' ? '我' : currentConsultation.expertName }}</div>
              <img v-if="message.sender === 'user'" src="https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=user%20avatar%20portrait&image_size=square" alt="用户头像" class="message-avatar">
              <div class="message-content">
                <div class="message-text">{{ message.content }}</div>
                <div class="message-time">{{ message.time }}</div>
              </div>
              <img v-if="message.sender === 'expert'" :src="getExpertAvatar(currentConsultation.expertId)" alt="专家头像" class="message-avatar">
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
            <el-button @click="detailDialogVisible = false">关闭</el-button>
            <el-button type="primary" @click="sendReply">发送消息</el-button>
          </div>
        </div>
      </div>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, nextTick, watch } from 'vue'
import { Plus } from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'

const consultDialogVisible = ref(false)
const detailDialogVisible = ref(false)
const consultFormRef = ref(null)
const messageList = ref(null)
const loading = ref(false)

const replyContent = ref('')

const experts = ref([])

const fetchExperts = async () => {
  try {
    const response = await fetch('/api/experts')
    const data = await response.json()
    experts.value = data.map(e => ({
      ...e,
      title: '专家'
    }))
  } catch (error) {
    ElMessage.error('获取专家列表失败')
  }
}

const consultations = ref([])

const consultForm = reactive({
  expertId: '',
  subject: '',
  content: ''
})

const consultRules = {
  expertId: [
    { required: true, message: '请选择专家', trigger: 'blur' }
  ],
  subject: [
    { required: true, message: '请输入咨询主题', trigger: 'blur' }
  ],
  content: [
    { required: true, message: '请输入咨询内容', trigger: 'blur' }
  ]
}

const currentConsultation = reactive({
  id: '',
  expertId: '',
  expertName: '',
  subject: '',
  status: '',
  createTime: '',
  messages: []
})

const fetchConsultations = async () => {
  loading.value = true
  try {
    const response = await fetch('/api/consultations')
    consultations.value = await response.json()
  } catch (error) {
    ElMessage.error('获取咨询列表失败')
  } finally {
    loading.value = false
  }
}

onMounted(() => {
  fetchExperts()
  fetchConsultations()
})

const openConsultDialog = (expert = null) => {
  if (expert) {
    consultForm.expertId = expert.id
  }
  consultDialogVisible.value = true
}

const submitConsultation = async () => {
  consultFormRef.value.validate((valid) => {
    if (valid) {
      const expert = experts.value.find(e => e.id === consultForm.expertId)
      const newConsultation = {
        id: 'C' + new Date().toISOString().replace(/[-:.TZ]/g, '').slice(0, 14),
        expertId: consultForm.expertId,
        expertName: expert.name,
        userName: '当前用户',
        subject: consultForm.subject,
        content: consultForm.content
      }
      
      fetch('/api/consultations', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(newConsultation)
      }).then(response => {
        if (response.ok) {
          ElMessage.success('咨询提交成功')
          consultDialogVisible.value = false
          consultForm.expertId = ''
          consultForm.subject = ''
          consultForm.content = ''
          fetchConsultations()
        } else {
          ElMessage.error('提交失败')
        }
      }).catch(() => {
        ElMessage.error('网络错误')
      })
    }
  })
}

const viewConsultation = async (row) => {
  const response = await fetch(`/api/consultations/${row.id}`)
  const data = await response.json()
  Object.assign(currentConsultation, data)
  detailDialogVisible.value = true
  
  nextTick(() => {
    if (messageList.value) {
      messageList.value.scrollTop = messageList.value.scrollHeight
    }
  })
}

const sendReply = async () => {
  if (!replyContent.value.trim()) return
  
  fetch('/api/messages', {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify({
      consultationId: currentConsultation.id,
      sender: 'user',
      content: replyContent.value
    })
  }).then(response => {
    if (response.ok) {
      // 更新本地消息
      currentConsultation.messages.push({
        sender: 'user',
        content: replyContent.value,
        time: new Date().toLocaleString('zh-CN')
      })
      replyContent.value = ''
      
      // 更新状态为处理中
      fetch('/api/consultations/status', {
        method: 'PUT',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ id: currentConsultation.id, status: '处理中' })
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

const getConsultationStatusTag = (status) => {
  const tagMap = {
    '已回复': 'success',
    '处理中': 'warning',
    '待处理': 'info'
  }
  return tagMap[status] || 'default'
}

const getExpertAvatar = (expertId) => {
  const expert = experts.value.find(e => e.id === expertId)
  return expert ? expert.avatar : 'https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=professional%20expert%20portrait&image_size=square'
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
.expert {
  display: flex;
  flex-direction: column;
  gap: 20px;
  height: 100%;
  box-sizing: border-box;
}

.action-bar {
  flex-shrink: 0;
}

.expert-list-card {
  flex-shrink: 0;
}

.expert-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 20px;
}

.expert-card {
  transition: all 0.3s ease;
}

.expert-card:hover {
  transform: translateY(-4px);
}

.expert-content {
  display: flex;
  align-items: center;
  gap: 20px;
}

.expert-info {
  flex: 1;
}

.expert-info h3 {
  margin: 0 0 8px 0;
  font-size: 16px;
  font-weight: 600;
}

.expert-title {
  margin: 0 0 4px 0;
  font-size: 14px;
  color: #606266;
}

.expert-specialty {
  margin: 0 0 12px 0;
  font-size: 12px;
  color: #909399;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.consultation-card {
  flex: 1;
  min-height: 0;
  display: flex;
  flex-direction: column;
}

.consultation-card :deep(.el-card__body) {
  flex: 1;
  overflow: auto;
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

.dialog-footer {
  width: 100%;
  display: flex;
  justify-content: flex-end;
  gap: 10px;
}
</style>