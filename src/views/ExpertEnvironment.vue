<template>
  <div class="expert-environment">
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
            <h2>环境监测</h2>
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

        <!-- 实时环境数据 -->
        <el-card shadow="hover" class="realtime-card">
          <template #header>
            <div class="card-header">
              <span>实时环境数据</span>
            </div>
          </template>
          <div class="realtime-data">
            <div class="data-item">
              <div class="data-value">{{ environmentData.temperature }}°C</div>
              <div class="data-label">温度</div>
            </div>
            <div class="data-item">
              <div class="data-value">{{ environmentData.humidity }}%</div>
              <div class="data-label">湿度</div>
            </div>
            <div class="data-item">
              <div class="data-value">{{ environmentData.light }} lux</div>
              <div class="data-label">光照</div>
            </div>
            <div class="data-item">
              <div class="data-value">{{ environmentData.co2 }} ppm</div>
              <div class="data-label">CO2</div>
            </div>
          </div>
        </el-card>

        <!-- 环境趋势 -->
        <el-card shadow="hover" class="trend-card">
          <template #header>
            <div class="card-header">
              <span>环境趋势</span>
              <div class="chart-controls">
                <el-select v-model="timeRange" size="small" placeholder="最近24小时">
                  <el-option label="最近24小时" value="24h"></el-option>
                  <el-option label="最近7天" value="7d"></el-option>
                  <el-option label="最近30天" value="30d"></el-option>
                </el-select>
                <el-select v-model="selectedIndicator" size="small" @change="updateChart" placeholder="温度">
                  <el-option label="温度" value="temperature"></el-option>
                  <el-option label="湿度" value="humidity"></el-option>
                  <el-option label="光照" value="light"></el-option>
                  <el-option label="CO2" value="co2"></el-option>
                </el-select>
              </div>
            </div>
          </template>
          <div class="trend-chart">
            <div ref="chartRef" style="width: 100%; height: 100%;"></div>
          </div>
        </el-card>

        <!-- 监测点状态 -->
        <el-card shadow="hover" class="monitor-points-card">
          <template #header>
            <div class="card-header">
              <span>监测点状态</span>
            </div>
          </template>
          <el-table :data="monitorPoints" style="width: 100%">
            <el-table-column prop="id" label="编号" width="80"></el-table-column>
            <el-table-column prop="name" label="名称"></el-table-column>
            <el-table-column prop="location" label="位置"></el-table-column>
            <el-table-column prop="status" label="状态" width="100">
              <template #default="scope">
                <el-tag :type="scope.row.status === '正常' ? 'success' : 'danger'">
                  {{ scope.row.status }}
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column prop="lastUpdate" label="最后更新" width="180"></el-table-column>
          </el-table>
        </el-card>
      </main>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, onUnmounted, watch, nextTick } from 'vue'
import { useRouter } from 'vue-router'
import * as echarts from 'echarts'
import {
  ChatDotRound,
  Monitor,
  Location,
  DataAnalysis
} from '@element-plus/icons-vue'

const router = useRouter()

const expertName = ref('张教授')
const expertAvatar = ref('https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=professional%20agriculture%20expert%20portrait&image_size=square')

const timeRange = ref('24h')
const selectedIndicator = ref('temperature')
const chartRef = ref(null)
let chart = null

const environmentData = ref({
  temperature: 25.6,
  humidity: 65.2,
  light: 1200,
  co2: 450
})

const monitorPoints = ref([
  {
    id: 1,
    name: '大棚1号监测点',
    location: '大棚区A1',
    status: '正常',
    lastUpdate: '2026-04-27 10:30'
  },
  {
    id: 2,
    name: '大棚2号监测点',
    location: '大棚区A2',
    status: '正常',
    lastUpdate: '2026-04-27 10:28'
  },
  {
    id: 3,
    name: '露天区监测点',
    location: '露天区B',
    status: '正常',
    lastUpdate: '2026-04-27 10:25'
  },
  {
    id: 4,
    name: '仓库监测点',
    location: '仓库区',
    status: '正常',
    lastUpdate: '2026-04-27 10:20'
  }
])

// 生成模拟数据
const generateChartData = (range) => {
  const data = {
    labels: [],
    temperature: [],
    humidity: [],
    light: [],
    co2: []
  }
  
  const now = new Date()
  let step = 1
  let points = 24
  
  if (range === '7d') {
    step = 24
    points = 7
  } else if (range === '30d') {
    step = 24 * 3
    points = 10
  }
  
  for (let i = points - 1; i >= 0; i--) {
    const date = new Date(now.getTime() - i * step * 60 * 60 * 1000)
    
    if (range === '24h') {
      data.labels.push(`${date.getHours()}:00`)
    } else {
      data.labels.push(`${date.getMonth() + 1}/${date.getDate()}`)
    }
    
    // 生成模拟数据
    data.temperature.push((20 + Math.random() * 10).toFixed(1))
    data.humidity.push((50 + Math.random() * 30).toFixed(1))
    data.light.push(Math.floor(500 + Math.random() * 2000))
    data.co2.push(Math.floor(300 + Math.random() * 300))
  }
  
  return data
}

// 初始化图表
const initChart = () => {
  if (chartRef.value) {
    chart = echarts.init(chartRef.value)
    updateChart()
    
    // 响应式调整
    window.addEventListener('resize', handleResize)
  }
}

// 更新图表
const updateChart = () => {
  if (!chart) return
  
  const data = generateChartData(timeRange.value)
  
  // 获取当前选中的指标数据
  let seriesData = []
  let indicatorName = ''
  let indicatorColor = ''
  let yAxisName = ''
  
  switch (selectedIndicator.value) {
    case 'temperature':
      seriesData = data.temperature
      indicatorName = '温度'
      yAxisName = '温度 (°C)'
      indicatorColor = '#ff7875'
      break
    case 'humidity':
      seriesData = data.humidity
      indicatorName = '湿度'
      yAxisName = '湿度 (%)'
      indicatorColor = '#73d13d'
      break
    case 'light':
      seriesData = data.light
      indicatorName = '光照'
      yAxisName = '光照 (lux)'
      indicatorColor = '#40a9ff'
      break
    case 'co2':
      seriesData = data.co2
      indicatorName = 'CO2'
      yAxisName = 'CO2 (ppm)'
      indicatorColor = '#faad14'
      break
    default:
      seriesData = data.temperature
      indicatorName = '温度'
      yAxisName = '温度 (°C)'
      indicatorColor = '#ff7875'
  }
  
  const option = {
    tooltip: {
      trigger: 'axis',
      formatter: (params) => {
        return `${params[0].name}: ${params[0].value} ${yAxisName.split(' ')[1]}`
      }
    },
    grid: {
      left: '3%',
      right: '4%',
      bottom: '3%',
      containLabel: true
    },
    xAxis: [
      {
        type: 'category',
        boundaryGap: false,
        data: data.labels
      }
    ],
    yAxis: [
      {
        type: 'value',
        name: yAxisName,
        axisLabel: {
          formatter: '{value}'
        }
      }
    ],
    series: [
      {
        name: indicatorName,
        type: 'line',
        data: seriesData,
        smooth: true,
        itemStyle: {
          color: indicatorColor
        },
        areaStyle: {
          color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
            { offset: 0, color: `${indicatorColor}80` },
            { offset: 1, color: `${indicatorColor}20` }
          ])
        }
      }
    ]
  }
  
  chart.setOption(option)
}

// 处理窗口大小变化
const handleResize = () => {
  chart?.resize()
}

// 监听时间范围变化
watch(timeRange, () => {
  updateChart()
})

// 组件挂载时初始化图表
onMounted(() => {
  nextTick(() => {
    initChart()
  })
})

// 组件卸载时销毁图表
onUnmounted(() => {
  chart?.dispose()
  window.removeEventListener('resize', handleResize)
})

const logout = () => {
  // 退出登录
  router.push('/expert/login')
}
</script>

<style scoped>
.expert-environment {
  min-height: 100vh;
  overflow-y: auto;
}

.app-container {
  display: flex;
  min-height: 100vh;
  overflow-y: auto;
}

/* 确保主内容区域可以滚动 */
.main-content {
  flex: 1;
  display: flex;
  flex-direction: column;
  background-color: #f5f7fa;
  overflow-y: auto !important;
  max-height: calc(100vh - 64px); /* 减去头部高度 */
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

.realtime-card {
  margin: 20px;
  flex-shrink: 0;
}

.realtime-data {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 20px;
}

.data-item {
  text-align: center;
  padding: 20px;
  background-color: #f9f9f9;
  border-radius: 8px;
  transition: all 0.3s ease;
}

.data-item:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1);
}

.data-value {
  font-size: 24px;
  font-weight: 600;
  color: #3498db;
  margin-bottom: 8px;
}

.data-label {
  font-size: 14px;
  color: #606266;
}

.trend-card {
  margin: 0 20px 20px;
  flex-shrink: 0;
  min-height: 400px;
}

.card-header {
  display: flex;
  flex-direction: column;
  align-items: flex-start;
  gap: 10px;
}

.chart-controls {
  display: flex;
  gap: 10px;
  align-items: center;
}

/* 选择框样式 */
:deep(.el-select) {
  min-width: 120px;
}

:deep(.el-select .el-input__wrapper) {
  border-radius: 4px;
  border: 1px solid #dcdfe6;
  box-shadow: none;
  transition: all 0.3s;
}

:deep(.el-select .el-input__wrapper:hover) {
  border-color: #409eff;
  box-shadow: 0 0 0 2px rgba(64, 158, 255, 0.2);
}

:deep(.el-select .el-input__wrapper.is-focus) {
  border-color: #409eff;
  box-shadow: 0 0 0 2px rgba(64, 158, 255, 0.2);
}

:deep(.el-select-dropdown) {
  border-radius: 4px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
  border: 1px solid #ebeef5;
  background-color: #fff;
}

:deep(.el-select-dropdown__item) {
  padding: 10px 20px;
  font-size: 14px;
  color: #606266;
  transition: all 0.3s;
}

:deep(.el-select-dropdown__item:hover) {
  background-color: #ecf5ff;
  color: #409eff;
}

:deep(.el-select-dropdown__item.selected) {
  background-color: #ecf5ff;
  color: #409eff;
  font-weight: 500;
}

.trend-chart {
  height: 300px;
  display: flex;
  align-items: center;
  justify-content: center;
  background-color: #f9f9f9;
  border-radius: 8px;
  overflow: hidden;
}

.chart-placeholder {
  text-align: center;
  color: #909399;
}

.chart-icon {
  font-size: 48px;
  margin-bottom: 16px;
}

.monitor-points-card {
  margin: 0 20px 20px;
  flex-shrink: 0;
  min-height: 300px;
  display: flex;
  flex-direction: column;
}

.monitor-points-card :deep(.el-card__body) {
  flex: 1;
  overflow: auto;
}
</style>
