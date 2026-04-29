<template>
  <div class="crop-identification">
    <div class="action-bar">
      <el-button type="primary" @click="openUploadDialog">
        <el-icon><upload /></el-icon>
        上传图片
      </el-button>
    </div>

    <el-card shadow="hover" class="history-card">
      <template #header>
        <div class="card-header">
          <span>识别记录</span>
        </div>
      </template>
      <el-table :data="historyList" style="width: 100%">
        <el-table-column prop="id" label="编号" width="80"></el-table-column>
        <el-table-column prop="imageName" label="图片名称"></el-table-column>
        <el-table-column prop="result" label="识别结果"></el-table-column>
        <el-table-column prop="confidence" label="置信度" width="120">
          <template #default="scope">
            <el-progress :percentage="scope.row.confidence * 100" :color="getConfidenceColor(scope.row.confidence)" :show-text="false" stroke-width="6" />
            <span :style="{ color: getConfidenceColor(scope.row.confidence) }">{{ (scope.row.confidence * 100).toFixed(1) }}%</span>
          </template>
        </el-table-column>
        <el-table-column prop="time" label="识别时间" width="180"></el-table-column>
        <el-table-column label="操作" width="200">
          <template #default="scope">
            <el-button type="primary" size="small" @click="viewDetail(scope.row)">查看详情</el-button>
            <el-button type="danger" size="small" @click="confirmDelete(scope.row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <el-card v-if="currentResult" shadow="hover" class="result-card">
      <template #header>
        <div class="card-header">
          <span>识别结果</span>
          <el-tag type="success" size="small">识别成功</el-tag>
        </div>
      </template>
      <div class="result-content">
        <div class="result-main">
          <div class="result-image">
            <el-image :src="currentResult.imageUrl" fit="cover"></el-image>
          </div>
          <div class="result-info">
            <div class="result-item primary">
              <div class="result-label">识别结果</div>
              <div class="result-value">{{ currentResult.result }}</div>
              <div class="confidence-wrapper">
                <span class="confidence-label">置信度</span>
                <el-progress :percentage="currentResult.confidence * 100" :color="getConfidenceColor(currentResult.confidence)" :show-text="false" stroke-width="8" />
                <span class="confidence-value" :style="{ color: getConfidenceColor(currentResult.confidence) }">
                  {{ (currentResult.confidence * 100).toFixed(1) }}%
                </span>
              </div>
            </div>
            <div class="result-item">
              <div class="result-label">所属分类</div>
              <div class="result-value">{{ currentResult.category }}</div>
            </div>
            <div class="result-item">
              <div class="result-label">识别时间</div>
              <div class="result-value">{{ currentResult.time }}</div>
            </div>
          </div>
        </div>
        <div class="result-description" v-if="currentResult.description">
          <h4>详细说明</h4>
          <p>{{ currentResult.description }}</p>
        </div>
      </div>
    </el-card>

    <el-dialog
      v-model="uploadDialogVisible"
      title="上传图片进行作物识别"
      width="600px"
    >
      <div class="upload-content">
        <el-upload
          ref="uploadRef"
          class="upload-demo"
          drag
          :auto-upload="false"
          :on-change="handleFileChange"
          :limit="1"
          accept="image/*"
          list-type="picture"
        >
          <el-icon class="el-icon--upload"><upload-filled /></el-icon>
          <div class="el-upload__text">
            将图片拖到此处，或<em>点击上传</em>
          </div>
          <template #tip>
            <div class="el-upload__tip">支持jpg、png格式图片，大小不超过5MB</div>
          </template>
        </el-upload>
        
        <div class="upload-actions">
          <el-button @click="uploadDialogVisible = false">取消</el-button>
          <el-button type="primary" @click="identifyCrop" :loading="loading">开始识别</el-button>
        </div>
      </div>
    </el-dialog>

    <el-dialog
      v-model="detailDialogVisible"
      title="识别详情"
      width="700px"
    >
      <div class="detail-content" v-if="detailData">
        <div class="detail-main">
          <div class="detail-image">
            <el-image :src="detailData.imageUrl || 'https://via.placeholder.com/200'" fit="cover"></el-image>
          </div>
          <div class="detail-info">
            <div class="info-item"><label>图片名称：</label><span>{{ detailData.imageName }}</span></div>
            <div class="info-item"><label>识别结果：</label><span class="result-text">{{ detailData.result }}</span></div>
            <div class="info-item">
              <label>置信度：</label>
              <el-progress :percentage="detailData.confidence * 100" :color="getConfidenceColor(detailData.confidence)" :show-text="false" stroke-width="6" />
              <span :style="{ color: getConfidenceColor(detailData.confidence) }">{{ (detailData.confidence * 100).toFixed(1) }}%</span>
            </div>
            <div class="info-item"><label>所属分类：</label><span>{{ detailData.category }}</span></div>
            <div class="info-item"><label>识别时间：</label><span>{{ detailData.time }}</span></div>
          </div>
        </div>
        <div class="detail-description" v-if="detailData.description">
          <h4>详细说明</h4>
          <p>{{ detailData.description }}</p>
        </div>
        <div class="detail-suggestions" v-if="detailData.suggestions && detailData.suggestions.length > 0">
          <h4>种植建议</h4>
          <ul>
            <li v-for="(suggestion, index) in detailData.suggestions" :key="index">{{ suggestion }}</li>
          </ul>
        </div>
      </div>
      <template #footer>
        <el-button @click="detailDialogVisible = false">关闭</el-button>
        <el-button type="danger" @click="confirmDelete(detailData)">删除记录</el-button>
      </template>
    </el-dialog>

    <el-dialog
      v-model="deleteConfirmVisible"
      title="确认删除"
      width="400px"
    >
      <p>确定要删除这条识别记录吗？此操作无法撤销。</p>
      <template #footer>
        <el-button @click="deleteConfirmVisible = false">取消</el-button>
        <el-button type="danger" @click="doDelete">确认删除</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { Upload, UploadFilled } from '@element-plus/icons-vue'

const uploadDialogVisible = ref(false)
const detailDialogVisible = ref(false)
const deleteConfirmVisible = ref(false)
const loading = ref(false)
const uploadRef = ref(null)
const selectedFile = ref(null)
const deleteTarget = ref(null)

const currentResult = ref(null)
const detailData = ref(null)

const historyList = ref([])

const cropDatabase = {
  '番茄': { category: '蔬菜类', description: '番茄是茄科番茄属植物，果实营养丰富。', suggestions: ['保持适宜温度', '充足阳光'] },
  '西红柿': { category: '蔬菜类', description: '西红柿即番茄，可生食或加工。', suggestions: ['保持土壤湿润'] },
  '黄瓜': { category: '蔬菜类', description: '黄瓜是葫芦科植物，果实脆嫩多汁。', suggestions: ['保持较高湿度'] },
  '玉米': { category: '粮食类', description: '玉米是重要的粮食作物。', suggestions: ['充足阳光'] },
  '小麦': { category: '粮食类', description: '小麦是世界重要粮食作物。', suggestions: ['适时播种'] },
  '水稻': { category: '粮食类', description: '水稻是全球主要主食。', suggestions: ['保持浅水层'] },
  '大豆': { category: '油料类', description: '大豆是重要油料作物。', suggestions: ['注意轮作'] },
  '棉花': { category: '经济类', description: '棉花是重要经济作物。', suggestions: ['充足光照'] },
  '陆地棉': { category: '经济类', description: '陆地棉是主要栽培棉种。', suggestions: ['温暖干燥气候'] }
}

const openUploadDialog = () => {
  uploadDialogVisible.value = true
  selectedFile.value = null
  if (uploadRef.value) uploadRef.value.clearFiles()
}

const handleFileChange = (file) => {
  selectedFile.value = file.raw
}

const identifyCrop = async () => {
  if (!selectedFile.value) {
    ElMessage.warning('请先选择一张图片')
    return
  }

  loading.value = true
  
  try {
    const reader = new FileReader()
    const base64 = await new Promise((resolve, reject) => {
      reader.onload = (e) => resolve(e.target.result)
      reader.onerror = reject
      reader.readAsDataURL(selectedFile.value)
    })

    const imageBase64 = base64.split(',')[1]

    const response = await fetch('/api/plant-recognition', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ imageBase64: imageBase64, imageName: selectedFile.value.name })
    })

    const data = await response.json()

    if (data.success) {
      const cropInfo = cropDatabase[data.result] || { category: data.category || '未知分类', description: data.description || '', suggestions: [] }

      const result = {
        id: historyList.value.length + 1,
        imageName: selectedFile.value.name,
        imageUrl: URL.createObjectURL(selectedFile.value),
        result: data.result,
        confidence: data.confidence,
        category: cropInfo.category,
        time: new Date().toLocaleString('zh-CN'),
        description: cropInfo.description || data.description || '暂无详细说明',
        suggestions: cropInfo.suggestions
      }

      historyList.value.unshift(result)
      currentResult.value = result
      uploadDialogVisible.value = false
      ElMessage.success('识别成功！')
    } else {
      ElMessage.error(data.error || '识别失败')
    }
  } catch (error) {
    console.error('识别异常:', error)
    ElMessage.error('识别失败，请重试')
  } finally {
    loading.value = false
  }
}

const viewDetail = (row) => {
  detailData.value = row
  detailDialogVisible.value = true
}

const confirmDelete = (row) => {
  deleteTarget.value = row
  deleteConfirmVisible.value = true
}

const doDelete = async () => {
  if (!deleteTarget.value) return

  try {
    const response = await fetch(`/api/plant-recognition/${deleteTarget.value.id}`, {
      method: 'DELETE'
    })

    const data = await response.json()

    if (data.success) {
      historyList.value = historyList.value.filter(item => item.id !== deleteTarget.value.id)
      if (currentResult.value && currentResult.value.id === deleteTarget.value.id) {
        currentResult.value = null
      }
      ElMessage.success('删除成功！')
    } else {
      ElMessage.error(data.error || '删除失败')
    }
  } catch (error) {
    console.error('删除异常:', error)
    ElMessage.error('删除失败，请重试')
  } finally {
    deleteConfirmVisible.value = false
    detailDialogVisible.value = false
    deleteTarget.value = null
  }
}

const fetchHistory = async () => {
  try {
    const response = await fetch('/api/plant-recognition')
    const records = await response.json()
    
    records.forEach(record => {
      historyList.value.push({
        id: record.id,
        imageName: record.imageName,
        imageUrl: '',
        result: record.recognitionResult,
        confidence: record.confidence,
        category: cropDatabase[record.recognitionResult]?.category || '未知分类',
        time: record.recognizeTime,
        description: cropDatabase[record.recognitionResult]?.description || '',
        suggestions: cropDatabase[record.recognitionResult]?.suggestions || []
      })
    })
  } catch (error) {
    console.error('获取识别历史失败:', error)
  }
}

const getConfidenceColor = (confidence) => {
  if (confidence >= 0.8) return '#67c23a'
  if (confidence >= 0.6) return '#e6a23c'
  return '#f56c6c'
}

onMounted(() => {
  fetchHistory()
})
</script>

<style scoped>
.crop-identification {
  padding: 20px;
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.result-main {
  display: flex;
  gap: 24px;
}

.result-image {
  width: 320px;
  height: 280px;
  border-radius: 12px;
  overflow: hidden;
  box-shadow: 0 4px 20px rgba(0,0,0,0.1);
}

.result-image :deep(.el-image) { width: 100%; height: 100%; }

.result-info { flex: 1; display: flex; flex-direction: column; gap: 16px; }

.result-item {
  padding: 20px;
  background: #fafafa;
  border-radius: 12px;
}

.result-item.primary {
  background: linear-gradient(135deg, #e6f7ff, #b3d9f5);
  border: 2px solid #1890ff;
}

.result-label { font-size: 14px; color: #8c8c8c; margin-bottom: 8px; }
.result-value { font-size: 24px; font-weight: 700; color: #303133; }

.confidence-wrapper {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-top: 12px;
}

.confidence-label { font-size: 14px; color: #8c8c8c; width: 60px; }
.confidence-value { font-size: 16px; font-weight: 600; width: 60px; text-align: right; }

.result-description {
  padding: 20px;
  background: #fff;
  border-radius: 12px;
  border: 1px solid #e8e8e8;
}

.result-description h4 { margin: 0 0 12px 0; font-size: 16px; }
.result-description p { margin: 0; line-height: 1.8; }

.detail-content { display: flex; flex-direction: column; gap: 20px; }
.detail-main { display: flex; gap: 24px; }
.detail-image { width: 220px; height: 220px; border-radius: 12px; overflow: hidden; }
.detail-image :deep(.el-image) { width: 100%; height: 100%; }
.detail-info { flex: 1; display: flex; flex-direction: column; gap: 14px; }

.info-item { display: flex; align-items: center; gap: 10px; padding: 12px 0; border-bottom: 1px dashed #e8e8e8; }
.info-item:last-child { border-bottom: none; }
.info-item label { width: 100px; font-weight: 600; color: #606266; }
.info-item .result-text { font-size: 20px; font-weight: 600; color: #1890ff; }

.detail-description, .detail-suggestions {
  padding: 20px;
  background: #fafafa;
  border-radius: 12px;
}

.detail-suggestions ul { margin: 0; padding-left: 20px; }
.detail-suggestions li { margin-bottom: 10px; }
</style>