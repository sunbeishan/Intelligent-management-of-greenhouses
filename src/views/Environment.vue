<template>
  <div class="environment">
    <!-- 监测点选择 -->
    <el-card shadow="hover" class="filter-card">
      <div class="filter-content">
        <el-form :inline="true" :model="filterForm" class="filter-form">
          <el-form-item label="监测点">
            <el-select v-model="filterForm.monitorPoint" placeholder="选择监测点" @change="onMonitorPointChange">
              <el-option label="大棚A" value="greenhouseA"></el-option>
              <el-option label="大棚B" value="greenhouseB"></el-option>
              <el-option label="大棚C" value="greenhouseC"></el-option>
              <el-option label="露天农田" value="openField"></el-option>
            </el-select>
          </el-form-item>
          <el-form-item label="选择日期">
            <el-date-picker
              v-model="filterForm.selectedDate"
              type="date"
              placeholder="选择日期"
              format="YYYY-MM-DD"
              value-format="YYYY-MM-DD"
            />
          </el-form-item>
          <el-form-item>
            <el-button type="primary" @click="searchData">查询</el-button>
          </el-form-item>
        </el-form>
      </div>
    </el-card>

    <!-- 环境指标卡片 -->
    <div class="env-indicators">
      <el-card shadow="hover" class="indicator-card">
        <div class="indicator-content">
          <h3>温度</h3>
          <p class="indicator-value">{{ environmentData.temperature }} °C</p>
          <p class="indicator-status" :class="getStatusClass(environmentData.temperatureStatus)">
            {{ environmentData.temperatureStatus }}
          </p>
        </div>
      </el-card>
      <el-card shadow="hover" class="indicator-card">
        <div class="indicator-content">
          <h3>湿度</h3>
          <p class="indicator-value">{{ environmentData.humidity }} %</p>
          <p class="indicator-status" :class="getStatusClass(environmentData.humidityStatus)">
            {{ environmentData.humidityStatus }}
          </p>
        </div>
      </el-card>
      <el-card shadow="hover" class="indicator-card">
        <div class="indicator-content">
          <h3>光照</h3>
          <p class="indicator-value">{{ environmentData.light }} lux</p>
          <p class="indicator-status" :class="getStatusClass(environmentData.lightStatus)">
            {{ environmentData.lightStatus }}
          </p>
        </div>
      </el-card>
      <el-card shadow="hover" class="indicator-card">
        <div class="indicator-content">
          <h3>CO2</h3>
          <p class="indicator-value">{{ environmentData.co2 }} ppm</p>
          <p class="indicator-status" :class="getStatusClass(environmentData.co2Status)">
            {{ environmentData.co2Status }}
          </p>
        </div>
      </el-card>
    </div>

    <!-- 历史数据图表 -->
    <el-card shadow="hover" class="chart-card">
      <template #header>
        <div class="card-header">
          <span>历史数据趋势</span>
          <div class="chart-controls">
            <el-select v-model="selectedIndicator" size="small" @change="updateChart" placeholder="温度">
              <el-option label="温度" value="temperature"></el-option>
              <el-option label="湿度" value="humidity"></el-option>
              <el-option label="光照" value="light"></el-option>
              <el-option label="CO2" value="co2"></el-option>
            </el-select>
          </div>
        </div>
      </template>
      <div ref="chartRef" class="chart-container"></div>
    </el-card>

    <!-- 监测点列表 -->
    <el-card shadow="hover" class="monitor-list-card">
      <template #header>
        <div class="card-header">
          <span>监测点状态</span>
        </div>
      </template>
      <el-table :data="monitorPoints" style="width: 100%">
        <el-table-column prop="name" label="监测点名称" width="180"></el-table-column>
        <el-table-column prop="location" label="位置" width="200"></el-table-column>
        <el-table-column prop="status" label="状态" width="120">
          <template #default="scope">
            <el-tag :type="getMonitorStatusTag(scope.row.status)">
              {{ scope.row.status }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="lastUpdate" label="最后更新" width="180"></el-table-column>
      </el-table>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted, onUnmounted } from 'vue'
import * as echarts from 'echarts'
import { Monitor } from '@element-plus/icons-vue'

const filterForm = ref({
  monitorPoint: '',
  selectedDate: ''
})

const environmentData = ref({
  temperature: 25.5,
  temperatureStatus: '正常',
  humidity: 65,
  humidityStatus: '正常',
  light: 8000,
  lightStatus: '正常',
  co2: 450,
  co2Status: '正常'
})

const monitorPoints = ref([
  {
    id: 1,
    name: '大棚A',
    location: '东区1号',
    status: '正常',
    lastUpdate: '2026-04-26 14:30'
  },
  {
    id: 2,
    name: '大棚B',
    location: '东区2号',
    status: '正常',
    lastUpdate: '2026-04-26 14:28'
  },
  {
    id: 3,
    name: '大棚C',
    location: '西区1号',
    status: '正常',
    lastUpdate: '2026-04-26 14:25'
  },
  {
    id: 4,
    name: '露天农田',
    location: '南区',
    status: '正常',
    lastUpdate: '2026-04-26 14:20'
  }
])

const chartRef = ref(null)
const chart = ref(null)
const selectedIndicator = ref('temperature')

const monitorPointConfig = {
  greenhouseA: {
    crop: '西红柿',
    tempRange: [20, 28],
    humidityRange: [60, 80],
    lightRange: [6000, 10000],
    co2Range: [400, 500]
  },
  greenhouseB: {
    crop: '黄瓜',
    tempRange: [18, 30],
    humidityRange: [65, 85],
    lightRange: [5000, 9000],
    co2Range: [380, 480]
  },
  greenhouseC: {
    crop: '茄子',
    tempRange: [22, 32],
    humidityRange: [55, 75],
    lightRange: [7000, 11000],
    co2Range: [420, 520]
  },
  openField: {
    crop: '玉米',
    tempRange: [15, 35],
    humidityRange: [45, 70],
    lightRange: [8000, 12000],
    co2Range: [350, 450]
  }
}

const tianjinMonthlyBase = {
  1: { temp: -2, humidity: 55, light: 5500, co2: 480 },
  2: { temp: 1, humidity: 50, light: 6000, co2: 470 },
  3: { temp: 8, humidity: 45, light: 7000, co2: 450 },
  4: { temp: 15, humidity: 50, light: 7800, co2: 440 },
  5: { temp: 22, humidity: 55, light: 8500, co2: 430 },
  6: { temp: 26, humidity: 65, light: 9000, co2: 420 },
  7: { temp: 28, humidity: 75, light: 9200, co2: 410 },
  8: { temp: 27, humidity: 75, light: 8800, co2: 415 },
  9: { temp: 22, humidity: 70, light: 8000, co2: 430 },
  10: { temp: 15, humidity: 60, light: 7200, co2: 450 },
  11: { temp: 7, humidity: 55, light: 6000, co2: 465 },
  12: { temp: -1, humidity: 58, light: 5500, co2: 475 }
}

const generateYearData = () => {
  const yearData = {}
  const baseYear = 2026
  
  for (let month = 1; month <= 12; month++) {
    const daysInMonth = new Date(baseYear, month, 0).getDate()
    const base = tianjinMonthlyBase[month]
    
    for (let day = 1; day <= daysInMonth; day++) {
      const dateStr = `${baseYear}-${String(month).padStart(2, '0')}-${String(day).padStart(2, '0')}`
      const dayOfYear = (new Date(baseYear, month - 1, day)).getDay()
      
      yearData[dateStr] = {
        temperature: (base.temp + Math.random() * 8 - 4).toFixed(1) * 1,
        humidity: (base.humidity + Math.random() * 20 - 10).toFixed(0) * 1,
        light: Math.floor(base.light + Math.random() * 2000 - 1000),
        co2: (base.co2 + Math.random() * 60 - 30).toFixed(0) * 1
      }
      
      if (dayOfYear === 0 || dayOfYear === 6) {
        yearData[dateStr].light = Math.floor(yearData[dateStr].light * 0.8)
      }
    }
  }
  
  return yearData
}

const yearData = generateYearData()

const getEnvironmentDataByDate = (dateStr) => {
  const monitorPoint = filterForm.value.monitorPoint || 'greenhouseA'
  const config = monitorPointConfig[monitorPoint] || monitorPointConfig['greenhouseA']
  
  const tempMin = config.tempRange[0]
  const tempMax = config.tempRange[1]
  const humidityMin = config.humidityRange[0]
  const humidityMax = config.humidityRange[1]
  const lightMin = config.lightRange[0]
  const lightMax = config.lightRange[1]
  const co2Min = config.co2Range[0]
  const co2Max = config.co2Range[1]
  
  const temperature = (tempMin + Math.random() * (tempMax - tempMin)).toFixed(1) * 1
  const humidity = Math.floor(humidityMin + Math.random() * (humidityMax - humidityMin))
  const light = Math.floor(lightMin + Math.random() * (lightMax - lightMin))
  const co2 = Math.floor(co2Min + Math.random() * (co2Max - co2Min))
  
  const tempStatus = temperature >= tempMin && temperature <= tempMax ? '正常' : 
                    temperature < tempMin ? '警告' : '异常'
  const humidityStatus = humidity >= humidityMin && humidity <= humidityMax ? '正常' : '异常'
  const lightStatus = light >= lightMin && light <= lightMax ? '正常' : '异常'
  const co2Status = co2 >= co2Min && co2 <= co2Max ? '正常' : '异常'
  
  return {
    temperature: temperature,
    temperatureStatus: tempStatus,
    humidity: humidity,
    humidityStatus: humidityStatus,
    light: light,
    lightStatus: lightStatus,
    co2: co2,
    co2Status: co2Status
  }
}

const generateHistoricalData = (days = 7) => {
  const dates = []
  const data = {
    temperature: [],
    humidity: [],
    light: [],
    co2: []
  }
  const today = new Date()
  
  for (let i = days - 1; i >= 0; i--) {
    const date = new Date(today)
    date.setDate(date.getDate() - i)
    dates.push(`${date.getMonth() + 1}/${date.getDate()}`)
    
    const dateStr = `${date.getFullYear()}-${String(date.getMonth() + 1).padStart(2, '0')}-${String(date.getDate()).padStart(2, '0')}`
    const dayData = yearData[dateStr]
    
    if (dayData) {
      data.temperature.push(dayData.temperature)
      data.humidity.push(dayData.humidity)
      data.light.push(dayData.light)
      data.co2.push(dayData.co2)
    } else {
      data.temperature.push((25 + Math.random() * 4 - 2).toFixed(1) * 1)
      data.humidity.push((65 + Math.random() * 10 - 5).toFixed(1) * 1)
      data.light.push(Math.floor(7000 + Math.random() * 2000))
      data.co2.push((450 + Math.random() * 50 - 25).toFixed(0) * 1)
    }
  }
  
  return { dates, data }
}

// 初始化图表
const initChart = () => {
  if (!chartRef.value) return
  
  chart.value = echarts.init(chartRef.value)
  updateChart()
}

// 更新图表
const updateChart = () => {
  if (!chart.value) return
  
  const { dates, data } = generateHistoricalData()
  
  // 获取当前选中的指标数据
  let seriesData = []
  let indicatorName = ''
  let indicatorColor = ''
  
  switch (selectedIndicator.value) {
    case 'temperature':
      seriesData = data.temperature
      indicatorName = '温度 (°C)'
      indicatorColor = '#ff7875'
      break
    case 'humidity':
      seriesData = data.humidity
      indicatorName = '湿度 (%)'
      indicatorColor = '#40a9ff'
      break
    case 'light':
      seriesData = data.light
      indicatorName = '光照 (lux)'
      indicatorColor = '#faad14'
      break
    case 'co2':
      seriesData = data.co2
      indicatorName = 'CO2 (ppm)'
      indicatorColor = '#73d13d'
      break
    default:
      seriesData = data.temperature
      indicatorName = '温度 (°C)'
      indicatorColor = '#ff7875'
  }
  
  // 配置图表选项
  const option = {
    tooltip: {
      trigger: 'axis',
      formatter: (params) => {
        return `${params[0].name}: ${params[0].value} ${indicatorName.split(' ')[1]}`
      }
    },
    grid: {
      left: '3%',
      right: '4%',
      bottom: '20%',
      top: '10%',
      containLabel: true
    },
    xAxis: {
      type: 'category',
      boundaryGap: false,
      data: dates,
      axisLabel: {
        rotate: 45,
        fontSize: 10
      }
    },
    yAxis: {
      type: 'value',
      name: indicatorName,
      axisLabel: {
        formatter: '{value}'
      }
    },
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
            {
              offset: 0,
              color: `${indicatorColor}33` // 33 is 20% opacity
            },
            {
              offset: 1,
              color: `${indicatorColor}11` // 11 is 7% opacity
            }
          ])
        }
      }
    ]
  }
  
  chart.value.setOption(option)
}

// 获取指标名称
const getIndicatorName = (indicator) => {
  const nameMap = {
    temperature: '温度 (°C)',
    humidity: '湿度 (%)',
    light: '光照 (lux)',
    co2: 'CO2 (ppm)'
  }
  return nameMap[indicator] || ''
}

// 获取指标颜色
const getIndicatorColor = (indicator, alpha = 1) => {
  const colorMap = {
    temperature: `rgba(255, 120, 117, ${alpha})`,
    humidity: `rgba(105, 192, 255, ${alpha})`,
    light: `rgba(230, 162, 60, ${alpha})`,
    co2: `rgba(103, 194, 58, ${alpha})`
  }
  return colorMap[indicator] || `rgba(144, 147, 153, ${alpha})`
}

// 响应式调整
const handleResize = () => {
  chart.value?.resize()
}

const searchData = () => {
  const dateStr = filterForm.value.selectedDate
  environmentData.value = getEnvironmentDataByDate(dateStr)
  updateChart()
}

const onMonitorPointChange = () => {
  environmentData.value = getEnvironmentDataByDate(filterForm.value.selectedDate)
  updateChart()
}

const getStatusClass = (status) => {
  const classMap = {
    '正常': 'status-normal',
    '警告': 'status-warning',
    '异常': 'status-error'
  }
  return classMap[status] || ''
}

const getMonitorStatusTag = (status) => {
  const tagMap = {
    '正常': 'success',
    '警告': 'warning',
    '异常': 'danger'
  }
  return tagMap[status] || 'default'
}

const viewDetails = (row) => {
  // 查看监测点详情
  console.log('查看监测点详情:', row)
}

onMounted(() => {
  environmentData.value = getEnvironmentDataByDate()
  initChart()
  window.addEventListener('resize', handleResize)
})

onUnmounted(() => {
  window.removeEventListener('resize', handleResize)
  chart.value?.dispose()
})
</script>

<style scoped>
.environment {
  display: flex;
  flex-direction: column;
  gap: 20px;
  min-height: 100%;
  box-sizing: border-box;
  overflow-y: auto;
}

.filter-card {
  flex-shrink: 0;
}

.filter-content {
  padding: 10px 0;
}

.filter-form {
  display: flex;
  align-items: center;
  gap: 16px;
}

.env-indicators {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 20px;
  flex-shrink: 0;
}

.indicator-card {
  transition: all 0.3s ease;
}

.indicator-card:hover {
  transform: translateY(-4px);
}

.indicator-content {
  text-align: center;
}

.indicator-content h3 {
  font-size: 14px;
  color: #606266;
  margin: 0 0 12px 0;
}

.indicator-value {
  font-size: 28px;
  font-weight: bold;
  margin: 0 0 8px 0;
}

.indicator-status {
  font-size: 12px;
  margin: 0;
  padding: 4px 12px;
  border-radius: 12px;
  display: inline-block;
}

.status-normal {
  background-color: #f0f9eb;
  color: #67c23a;
}

.status-warning {
  background-color: #fdf6ec;
  color: #e6a23c;
}

.status-error {
  background-color: #fef0f0;
  color: #f56c6c;
}

.chart-card {
  flex: 1;
  min-height: 0;
  display: flex;
  flex-direction: column;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  flex-shrink: 0;
}

.chart-controls {
  display: flex;
  gap: 10px;
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

.chart-container {
  flex: 1;
  min-height: 450px;
  overflow: hidden;
  position: relative;
}

/* 隐藏滚动条 */
:deep(.el-card__body) {
  overflow: hidden !important;
  padding: 20px !important;
}

.chart-card {
  flex-shrink: 0;
  min-height: 450px;
  display: flex;
  flex-direction: column;
}

.monitor-list-card {
  flex-shrink: 0;
  min-height: 300px;
  display: flex;
  flex-direction: column;
}

.monitor-list-card :deep(.el-card__body) {
  flex: 1;
  overflow: auto;
}
</style>
