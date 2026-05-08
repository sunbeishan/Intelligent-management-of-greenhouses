<template>
  <div class="dashboard">
    <!-- 数据概览卡片 -->
    <div class="stats-grid">
      <el-card shadow="hover" class="stat-card">
        <div class="stat-content">
          <div class="stat-info">
            <h3>总农田面积</h3>
            <p class="stat-value">{{ totalFarmlandArea ? totalFarmlandArea.toFixed(2) : '0.00' }} 亩</p>
            <p class="stat-desc">较上月增长 5%</p>
          </div>
          <div class="stat-icon green">
            <el-icon><location /></el-icon>
          </div>
        </div>
      </el-card>
      <el-card shadow="hover" class="stat-card">
        <div class="stat-content">
          <div class="stat-info">
            <h3>灌溉计划</h3>
            <p class="stat-value">{{ irrigationPlanCount }} 个</p>
            <p class="stat-desc">{{ activePlanCount }} 个正在运行</p>
          </div>
          <div class="stat-icon blue">
            <el-icon><hot-water /></el-icon>
          </div>
        </div>
      </el-card>
      <el-card shadow="hover" class="stat-card">
        <div class="stat-content">
          <div class="stat-info">
            <h3>专家咨询</h3>
            <p class="stat-value">{{ consultCount }} 次</p>
            <p class="stat-desc">本月已解决 {{ solvedCount }} 次</p>
          </div>
          <div class="stat-icon purple">
            <el-icon><user /></el-icon>
          </div>
        </div>
      </el-card>
    </div>

    <!-- 图表区域 -->
    <div class="charts-section">
      <el-card shadow="hover" class="chart-card full-width">
        <template #header>
          <div class="card-header">
            <span>环境指标</span>
            <el-select v-model="envChartPeriod" size="small" class="period-select" @change="updateEnvChart">
              <el-option label="近7天" value="7d"></el-option>
              <el-option label="近30天" value="30d"></el-option>
              <el-option label="近90天" value="90d"></el-option>
            </el-select>
          </div>
        </template>
        <div ref="envChartRef" class="chart-container"></div>
      </el-card>
    </div>

    <!-- 最近活动 -->
    <el-card shadow="hover" class="activity-card">
      <template #header>
        <div class="card-header">
          <span>最近活动</span>
        </div>
      </template>
      <el-table :data="recentActivities" style="width: 100%">
        <el-table-column prop="time" label="时间" width="180"></el-table-column>
        <el-table-column prop="type" label="类型" width="120">
          <template #default="scope">
            <el-tag :type="getActivityTypeTag(scope.row.type)">{{ scope.row.type }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="content" label="内容"></el-table-column>
      </el-table>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted, onUnmounted } from 'vue'
import { ElMessage } from 'element-plus'
import * as echarts from 'echarts'
import {
  Location,
  Monitor,
  DataAnalysis,
  User,
  HotWater
} from '@element-plus/icons-vue'

const envChartPeriod = ref('30d')
const envChartRef = ref(null)
const envChart = ref(null)
const totalFarmlandArea = ref(0)
const consultCount = ref(0)
const solvedCount = ref(0)
const irrigationPlanCount = ref(0)
const activePlanCount = ref(0)

const recentActivities = ref([])

const fetchTotalFarmlandArea = async () => {
  try {
    const response = await fetch('/api/farmland/total-area')
    const data = await response.json()
    totalFarmlandArea.value = data.totalArea || 0
  } catch (error) {
    ElMessage.error('获取农田总面积失败')
    totalFarmlandArea.value = 0
  }
}

const fetchConsultationStats = async () => {
  try {
    const response = await fetch('/api/consultations')
    const data = await response.json()
    consultCount.value = data.length || 0
    solvedCount.value = data.filter(c => c.status === '完成咨询').length || 0
  } catch (error) {
    ElMessage.error('获取咨询统计失败')
    consultCount.value = 0
    solvedCount.value = 0
  }
}

const fetchIrrigationStats = async () => {
  try {
    const response = await fetch('/api/irrigation/plans')
    const data = await response.json()
    irrigationPlanCount.value = data.length || 0
    activePlanCount.value = data.filter(p => p.status === '启用').length || 0
  } catch (error) {
    ElMessage.error('获取灌溉计划统计失败')
    irrigationPlanCount.value = 0
    activePlanCount.value = 0
  }
}

const fetchRecentActivities = async () => {
  try {
    const response = await fetch('/api/activities')
    const data = await response.json()
    recentActivities.value = data.length > 0 ? data : generateMockActivities()
  } catch (error) {
    console.error('获取活动日志失败:', error)
    recentActivities.value = generateMockActivities()
  }
}

const generateMockActivities = () => {
  const activities = [
    { time: new Date().toLocaleString('zh-CN'), type: '环境监测', content: '大棚A温度正常' },
    { time: new Date(Date.now() - 300000).toLocaleString('zh-CN'), type: '专家咨询', content: '咨询已提交' },
    { time: new Date(Date.now() - 600000).toLocaleString('zh-CN'), type: '农资采购', content: '采购单已创建' },
    { time: new Date(Date.now() - 900000).toLocaleString('zh-CN'), type: '产品出售', content: '销售订单已完成' },
    { time: new Date(Date.now() - 1200000).toLocaleString('zh-CN'), type: '农田管理', content: '农田信息已更新' }
  ]
  return activities
}

const generateEnvData = (days) => {
  const dates = []
  const temperature = []
  const humidity = []
  const today = new Date()
  
  for (let i = days - 1; i >= 0; i--) {
    const date = new Date(today)
    date.setDate(date.getDate() - i)
    dates.push(`${date.getMonth() + 1}/${date.getDate()}`)
    // 生成模拟温度数据
    const baseTemp = 25
    const tempRandom = Math.random() * 4 - 2
    temperature.push((baseTemp + tempRandom).toFixed(1) * 1)
    // 生成模拟湿度数据
    const baseHumidity = 65
    const humidityRandom = Math.random() * 10 - 5
    humidity.push((baseHumidity + humidityRandom).toFixed(1) * 1)
  }
  
  return { dates, temperature, humidity }
}

// 初始化环境指标图表
const initEnvChart = () => {
  if (!envChartRef.value) return
  
  envChart.value = echarts.init(envChartRef.value)
  updateEnvChart()
}

// 更新环境指标图表
const updateEnvChart = () => {
  if (!envChart.value) return
  
  const days = parseInt(envChartPeriod.value.replace('d', ''))
  const data = generateEnvData(days)
  
  const option = {
    tooltip: {
      trigger: 'axis'
    },
    legend: {
      data: ['温度', '湿度'],
      top: 0
    },
    grid: {
      left: '3%',
      right: '4%',
      bottom: '3%',
      containLabel: true
    },
    xAxis: {
      type: 'category',
      boundaryGap: false,
      data: data.dates,
      axisLabel: {
        rotate: 45,
        fontSize: 10
      }
    },
    yAxis: [
      {
        type: 'value',
        name: '温度（°C）',
        position: 'left',
        axisLabel: {
          formatter: '{value}'
        }
      },
      {
        type: 'value',
        name: '湿度（%）',
        position: 'right',
        axisLabel: {
          formatter: '{value}'
        }
      }
    ],
    series: [
      {
        name: '温度',
        type: 'line',
        data: data.temperature,
        smooth: true,
        itemStyle: {
          color: '#ff7875'
        }
      },
      {
        name: '湿度',
        type: 'line',
        yAxisIndex: 1,
        data: data.humidity,
        smooth: true,
        itemStyle: {
          color: '#69c0ff'
        }
      }
    ]
  }
  
  envChart.value.setOption(option)
}

// 响应式调整
const handleResize = () => {
  envChart.value?.resize()
}

const getActivityTypeTag = (type) => {
  const tagMap = {
    '环境监测': 'warning',
    '专家咨询': 'info',
    '农资采购': 'success',
    '产品出售': 'primary',
    '农田管理': 'default',
    '系统管理': 'danger'
  }
  return tagMap[type] || 'default'
}

onMounted(() => {
  fetchTotalFarmlandArea()
  fetchConsultationStats()
  fetchIrrigationStats()
  fetchRecentActivities()
  initEnvChart()
  window.addEventListener('resize', handleResize)
})

onUnmounted(() => {
  window.removeEventListener('resize', handleResize)
  envChart.value?.dispose()
})
</script>

<style scoped>
.dashboard {
  display: flex;
  flex-direction: column;
  gap: 20px;
  min-height: 100%;
  box-sizing: border-box;
  overflow-y: auto;
}

.stats-grid {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 20px;
  flex-shrink: 0;
}

.stat-card {
  transition: all 0.3s ease;
}

.stat-card:hover {
  transform: translateY(-4px);
}

.stat-content {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.stat-info h3 {
  font-size: 14px;
  color: #606266;
  margin: 0 0 8px 0;
}

.stat-value {
  font-size: 24px;
  font-weight: bold;
  margin: 0 0 4px 0;
}

.stat-desc {
  font-size: 12px;
  color: #909399;
  margin: 0;
}

.stat-icon {
  width: 48px;
  height: 48px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 24px;
  color: #fff;
}

.stat-icon.green {
  background-color: #67c23a;
}

.stat-icon.blue {
  background-color: #409eff;
}

.stat-icon.orange {
  background-color: #e6a23c;
}

.stat-icon.purple {
  background-color: #909399;
}

.charts-section {
  display: grid;
  grid-template-columns: 1fr;
  gap: 20px;
  flex-shrink: 0;
}

.chart-card {
  display: flex;
  flex-direction: column;
  min-height: 400px;
}

.chart-card.full-width {
  grid-column: 1;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  flex-shrink: 0;
}

.period-select {
  width: 120px;
}

.chart-container {
  flex: 1;
  min-height: 350px;
}

.activity-card {
  flex-shrink: 0;
  min-height: 300px;
  max-height: 400px;
  display: flex;
  flex-direction: column;
  margin-top: 0;
}

.activity-card :deep(.el-card__body) {
  flex: 1;
  overflow-y: auto;
  overflow-x: hidden;
}
</style>
