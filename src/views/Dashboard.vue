<template>
  <div class="dashboard">
    <!-- 数据概览卡片 -->
    <div class="stats-grid">
      <el-card shadow="hover" class="stat-card">
        <div class="stat-content">
          <div class="stat-info">
            <h3>总农田面积</h3>
            <p class="stat-value">1250 亩</p>
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
            <h3>环境监测点</h3>
            <p class="stat-value">32 个</p>
            <p class="stat-desc">全部正常运行</p>
          </div>
          <div class="stat-icon blue">
            <el-icon><monitor /></el-icon>
          </div>
        </div>
      </el-card>
      <el-card shadow="hover" class="stat-card">
        <div class="stat-content">
          <div class="stat-info">
            <h3>本月产量</h3>
            <p class="stat-value">85 吨</p>
            <p class="stat-desc">较上月增长 8%</p>
          </div>
          <div class="stat-icon orange">
            <el-icon><data-analysis /></el-icon>
          </div>
        </div>
      </el-card>
      <el-card shadow="hover" class="stat-card">
        <div class="stat-content">
          <div class="stat-info">
            <h3>专家咨询</h3>
            <p class="stat-value">12 次</p>
            <p class="stat-desc">本月已解决 10 次</p>
          </div>
          <div class="stat-icon purple">
            <el-icon><user /></el-icon>
          </div>
        </div>
      </el-card>
    </div>

    <!-- 图表区域 -->
    <div class="charts-section">
      <el-card shadow="hover" class="chart-card">
        <template #header>
          <div class="card-header">
            <span>产量趋势</span>
            <el-select v-model="yieldChartPeriod" size="small" class="period-select" @change="updateYieldChart">
              <el-option label="近7天" value="7d"></el-option>
              <el-option label="近30天" value="30d"></el-option>
              <el-option label="近90天" value="90d"></el-option>
            </el-select>
          </div>
        </template>
        <div ref="yieldChartRef" class="chart-container"></div>
      </el-card>
      <el-card shadow="hover" class="chart-card">
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
import * as echarts from 'echarts'
import {
  Location,
  Monitor,
  DataAnalysis,
  User
} from '@element-plus/icons-vue'

const yieldChartPeriod = ref('30d')
const envChartPeriod = ref('30d')
const yieldChartRef = ref(null)
const envChartRef = ref(null)
const yieldChart = ref(null)
const envChart = ref(null)

const recentActivities = ref([
  {
    time: '2026-04-26 14:30',
    type: '环境监测',
    content: '大棚A温度异常，已发送预警'
  },
  {
    time: '2026-04-26 11:15',
    type: '专家咨询',
    content: '张专家回复了关于病虫害防治的问题'
  },
  {
    time: '2026-04-25 16:45',
    type: '农资采购',
    content: '采购了100kg化肥，已入库'
  },
  {
    time: '2026-04-25 09:30',
    type: '产品出售',
    content: '销售了5吨蔬菜，已完成交易'
  }
])

// 生成模拟数据
const generateYieldData = (days) => {
  const dates = []
  const values = []
  const today = new Date()
  
  for (let i = days - 1; i >= 0; i--) {
    const date = new Date(today)
    date.setDate(date.getDate() - i)
    dates.push(`${date.getMonth() + 1}/${date.getDate()}`)
    // 生成模拟产量数据，带有一些随机波动
    const baseValue = 2.5
    const randomValue = Math.random() * 1 - 0.5
    values.push((baseValue + randomValue).toFixed(2) * 1)
  }
  
  return { dates, values }
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

// 初始化产量趋势图表
const initYieldChart = () => {
  if (!yieldChartRef.value) return
  
  yieldChart.value = echarts.init(yieldChartRef.value)
  updateYieldChart()
}

// 初始化环境指标图表
const initEnvChart = () => {
  if (!envChartRef.value) return
  
  envChart.value = echarts.init(envChartRef.value)
  updateEnvChart()
}

// 更新产量趋势图表
const updateYieldChart = () => {
  if (!yieldChart.value) return
  
  const days = parseInt(yieldChartPeriod.value.replace('d', ''))
  const data = generateYieldData(days)
  
  const option = {
    tooltip: {
      trigger: 'axis',
      formatter: '{b}: {c} 吨'
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
    yAxis: {
      type: 'value',
      name: '产量（吨）',
      axisLabel: {
        formatter: '{value}'
      }
    },
    series: [
      {
        name: '产量',
        type: 'line',
        data: data.values,
        smooth: true,
        itemStyle: {
          color: '#1890ff'
        },
        areaStyle: {
          color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
            {
              offset: 0,
              color: 'rgba(24, 144, 255, 0.3)'
            },
            {
              offset: 1,
              color: 'rgba(24, 144, 255, 0.1)'
            }
          ])
        }
      }
    ]
  }
  
  yieldChart.value.setOption(option)
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

// 更新所有图表
const updateCharts = () => {
  updateYieldChart()
  updateEnvChart()
}

// 响应式调整
const handleResize = () => {
  yieldChart.value?.resize()
  envChart.value?.resize()
}

const getActivityTypeTag = (type) => {
  const tagMap = {
    '环境监测': 'warning',
    '专家咨询': 'info',
    '农资采购': 'success',
    '产品出售': 'primary'
  }
  return tagMap[type] || 'default'
}

onMounted(() => {
  initYieldChart()
  initEnvChart()
  window.addEventListener('resize', handleResize)
})

onUnmounted(() => {
  window.removeEventListener('resize', handleResize)
  yieldChart.value?.dispose()
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
  grid-template-columns: repeat(4, 1fr);
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
  grid-template-columns: repeat(2, 1fr);
  gap: 20px;
  flex-shrink: 0;
}

.chart-card {
  display: flex;
  flex-direction: column;
  min-height: 400px;
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
  display: flex;
  flex-direction: column;
  margin-top: 0;
}

.activity-card :deep(.el-card__body) {
  flex: 1;
  overflow: auto;
}
</style>
