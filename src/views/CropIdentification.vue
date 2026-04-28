<template>
  <div class="crop-identification">
    <!-- 操作按钮 -->
    <div class="action-bar">
      <el-button type="primary" @click="openUploadDialog">
        <el-icon><upload /></el-icon>
        上传图片
      </el-button>
    </div>

    <!-- 识别历史 -->
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
        <el-table-column prop="confidence" label="置信度" width="100">
          <template #default="scope">
            {{ (scope.row.confidence * 100).toFixed(2) }}%
          </template>
        </el-table-column>
        <el-table-column prop="time" label="识别时间" width="180"></el-table-column>
        <el-table-column label="操作" width="120">
          <template #default="scope">
            <el-button type="primary" size="small" @click="viewDetail(scope.row)">查看详情</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <!-- 识别结果展示 -->
    <el-card v-if="currentResult" shadow="hover" class="result-card">
      <template #header>
        <div class="card-header">
          <span>识别结果</span>
        </div>
      </template>
      <div class="result-content">
        <div class="result-main">
          <div class="result-image">
            <el-image :src="currentResult.imageUrl" fit="contain"></el-image>
          </div>
          <div class="result-info">
            <div class="result-item primary">
              <div class="result-label">识别结果</div>
              <div class="result-value">{{ currentResult.result }}</div>
              <div class="result-confidence">置信度: {{ (currentResult.confidence * 100).toFixed(2) }}%</div>
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

    <!-- 上传对话框 -->
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

    <!-- 识别结果详情对话框 -->
    <el-dialog
      v-model="detailDialogVisible"
      title="识别详情"
      width="700px"
    >
      <div class="detail-content" v-if="detailData">
        <div class="detail-main">
          <div class="detail-image">
            <el-image :src="detailData.imageUrl" fit="contain"></el-image>
          </div>
          <div class="detail-info">
            <div class="info-item">
              <label>图片名称：</label>
              <span>{{ detailData.imageName }}</span>
            </div>
            <div class="info-item">
              <label>识别结果：</label>
              <span class="result-text">{{ detailData.result }}</span>
            </div>
            <div class="info-item">
              <label>置信度：</label>
              <span>{{ (detailData.confidence * 100).toFixed(2) }}%</span>
            </div>
            <div class="info-item">
              <label>所属分类：</label>
              <span>{{ detailData.category }}</span>
            </div>
            <div class="info-item">
              <label>识别时间：</label>
              <span>{{ detailData.time }}</span>
            </div>
          </div>
        </div>
        <div class="detail-description" v-if="detailData.description">
          <h4>详细说明</h4>
          <p>{{ detailData.description }}</p>
        </div>
        <div class="detail-suggestions" v-if="detailData.suggestions">
          <h4>种植建议</h4>
          <ul>
            <li v-for="(suggestion, index) in detailData.suggestions" :key="index">{{ suggestion }}</li>
          </ul>
        </div>
      </div>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { ElMessage } from 'element-plus'
import {
  Upload,
  UploadFilled
} from '@element-plus/icons-vue'

const uploadDialogVisible = ref(false)
const detailDialogVisible = ref(false)
const loading = ref(false)
const uploadRef = ref(null)
const selectedFile = ref(null)

const currentResult = ref(null)
const detailData = ref(null)

const historyList = ref([
  {
    id: 1,
    imageName: 'tomato_leaf.jpg',
    imageUrl: 'https://example.com/tomato_leaf.jpg',
    result: '番茄叶',
    confidence: 0.9856,
    category: '蔬菜类',
    time: '2026-04-27 10:30',
    description: '番茄叶是茄科番茄属植物的叶子，具有羽状复叶的特点。叶片呈椭圆形或卵形，边缘有锯齿。',
    suggestions: [
      '保持适宜的温度范围（18-27°C）',
      '注意合理灌溉，避免过度浇水',
      '定期施用氮磷钾复合肥'
    ]
  },
  {
    id: 2,
    imageName: 'corn_plant.jpg',
    imageUrl: 'https://example.com/corn_plant.jpg',
    result: '玉米',
    confidence: 0.9723,
    category: '粮食类',
    time: '2026-04-26 15:20',
    description: '玉米是禾本科玉米属一年生草本植物，是重要的粮食作物和饲料作物。',
    suggestions: [
      '选择合适的播种时间（春季或夏季）',
      '保持充足的阳光照射',
      '注意氮肥的施用'
    ]
  },
  {
    id: 3,
    imageName: 'wheat_field.jpg',
    imageUrl: 'https://example.com/wheat_field.jpg',
    result: '小麦',
    confidence: 0.9689,
    category: '粮食类',
    time: '2026-04-26 09:15',
    description: '小麦是禾本科小麦属一年生或二年生草本植物，是世界上最重要的粮食作物之一。',
    suggestions: [
      '注意防治小麦锈病和白粉病',
      '合理施肥，提高产量',
      '及时收割，避免过度成熟'
    ]
  }
])

const cropDatabase = {
  '番茄': {
    category: '蔬菜类',
    description: '番茄是茄科番茄属一年生或多年生草本植物，果实营养丰富，是常见的蔬菜和水果。',
    suggestions: [
      '保持适宜的生长温度（18-27°C）',
      '需要充足的阳光照射',
      '注意合理灌溉，避免过度浇水',
      '定期施用复合肥，促进果实生长'
    ]
  },
  '西红柿': {
    category: '蔬菜类',
    description: '西红柿即番茄，是茄科番茄属植物，果实可生食、煮食或加工成番茄酱等制品。',
    suggestions: [
      '保持土壤湿润但不过湿',
      '适时修剪侧枝，促进主茎生长',
      '注意防治早疫病和晚疫病'
    ]
  },
  '黄瓜': {
    category: '蔬菜类',
    description: '黄瓜是葫芦科黄瓜属一年生蔓生或攀援草本植物，是常见的蔬菜作物。',
    suggestions: [
      '保持较高的空气湿度',
      '需要搭架引蔓',
      '注意防治霜霉病和白粉病'
    ]
  },
  '玉米': {
    category: '粮食类',
    description: '玉米是禾本科玉米属一年生草本植物，是重要的粮食作物和饲料作物。',
    suggestions: [
      '选择合适的播种时间',
      '保证充足的阳光和水分',
      '注意氮磷钾肥的合理配比'
    ]
  },
  '小麦': {
    category: '粮食类',
    description: '小麦是禾本科小麦属一年生或二年生草本植物，是世界上最重要的粮食作物之一。',
    suggestions: [
      '适时播种，确保出苗整齐',
      '注意防治小麦锈病',
      '合理施肥，提高抗病能力'
    ]
  },
  '水稻': {
    category: '粮食类',
    description: '水稻是禾本科稻属一年生草本植物，是世界上最重要的粮食作物之一。',
    suggestions: [
      '保持田间浅水层',
      '适时晒田，控制无效分蘖',
      '注意防治稻瘟病和纹枯病'
    ]
  },
  '大豆': {
    category: '油料类',
    description: '大豆是豆科大豆属一年生草本植物，是重要的油料作物和蛋白质来源。',
    suggestions: [
      '选择合适的播种时间',
      '注意轮作，避免连作',
      '防治大豆蚜虫和食心虫'
    ]
  },
  '棉花': {
    category: '经济类',
    description: '棉花是锦葵科棉属一年生草本或亚灌木植物，是重要的经济作物。',
    suggestions: [
      '保持充足的阳光和热量',
      '注意防治棉铃虫',
      '适时灌溉和施肥'
    ]
  }
}

const openUploadDialog = () => {
  uploadDialogVisible.value = true
  selectedFile.value = null
  if (uploadRef.value) {
    uploadRef.value.clearFiles()
  }
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
    // 模拟API调用延迟
    await new Promise(resolve => setTimeout(resolve, 2000))
    
    // 模拟识别结果
    const crops = Object.keys(cropDatabase)
    const randomCrop = crops[Math.floor(Math.random() * crops.length)]
    const confidence = 0.85 + Math.random() * 0.14
    const cropInfo = cropDatabase[randomCrop]
    
    const result = {
      id: historyList.value.length + 1,
      imageName: selectedFile.value.name,
      imageUrl: URL.createObjectURL(selectedFile.value),
      result: randomCrop,
      confidence: confidence,
      category: cropInfo.category,
      time: new Date().toLocaleString('zh-CN'),
      description: cropInfo.description,
      suggestions: cropInfo.suggestions
    }
    
    historyList.value.unshift(result)
    currentResult.value = result
    uploadDialogVisible.value = false
    
    ElMessage.success('识别成功！')
  } catch (error) {
    ElMessage.error('识别失败，请重试')
  } finally {
    loading.value = false
  }
}

const viewDetail = (row) => {
  detailData.value = row
  detailDialogVisible.value = true
}
</script>

<style scoped>
.crop-identification {
  display: flex;
  flex-direction: column;
  gap: 20px;
  height: 100%;
  box-sizing: border-box;
}

.action-bar {
  flex-shrink: 0;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.history-card {
  flex: 1;
  min-height: 0;
  display: flex;
  flex-direction: column;
}

.history-card :deep(.el-card__body) {
  flex: 1;
  overflow: auto;
}

.result-card {
  flex-shrink: 0;
}

.result-content {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.result-main {
  display: flex;
  gap: 20px;
}

.result-image {
  width: 300px;
  height: 300px;
  background-color: #f5f5f5;
  border-radius: 8px;
  display: flex;
  align-items: center;
  justify-content: center;
  overflow: hidden;
}

.result-image :deep(.el-image) {
  max-width: 100%;
  max-height: 100%;
}

.result-info {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.result-item {
  padding: 16px;
  background-color: #f9f9f9;
  border-radius: 8px;
}

.result-item.primary {
  background-color: #e6f7ff;
  border: 2px solid #1890ff;
}

.result-label {
  font-size: 14px;
  color: #909399;
  margin-bottom: 8px;
}

.result-value {
  font-size: 20px;
  font-weight: 600;
  color: #303133;
  margin-bottom: 4px;
}

.result-confidence {
  font-size: 14px;
  color: #1890ff;
}

.result-description {
  padding: 16px;
  background-color: #f9f9f9;
  border-radius: 8px;
}

.result-description h4 {
  margin: 0 0 12px 0;
  font-size: 16px;
  color: #303133;
}

.result-description p {
  margin: 0;
  font-size: 14px;
  color: #606266;
  line-height: 1.6;
}

.upload-content {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.upload-demo {
  width: 100%;
}

.upload-actions {
  display: flex;
  justify-content: flex-end;
  gap: 10px;
}

.detail-content {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.detail-main {
  display: flex;
  gap: 20px;
}

.detail-image {
  width: 250px;
  height: 250px;
  background-color: #f5f5f5;
  border-radius: 8px;
  display: flex;
  align-items: center;
  justify-content: center;
  overflow: hidden;
}

.detail-image :deep(.el-image) {
  max-width: 100%;
  max-height: 100%;
}

.detail-info {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.info-item {
  display: flex;
  align-items: center;
  padding: 8px 0;
  border-bottom: 1px solid #f0f0f0;
}

.info-item label {
  width: 100px;
  font-weight: 600;
  color: #606266;
}

.info-item span {
  flex: 1;
  color: #303133;
}

.info-item .result-text {
  font-size: 18px;
  font-weight: 600;
  color: #1890ff;
}

.detail-description,
.detail-suggestions {
  padding: 16px;
  background-color: #f9f9f9;
  border-radius: 8px;
}

.detail-description h4,
.detail-suggestions h4 {
  margin: 0 0 12px 0;
  font-size: 16px;
  color: #303133;
}

.detail-description p {
  margin: 0;
  font-size: 14px;
  color: #606266;
  line-height: 1.6;
}

.detail-suggestions ul {
  margin: 0;
  padding-left: 20px;
}

.detail-suggestions li {
  margin-bottom: 8px;
  font-size: 14px;
  color: #606266;
  line-height: 1.6;
}

.detail-suggestions li:last-child {
  margin-bottom: 0;
}
</style>
