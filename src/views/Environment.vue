<template>
  <div class="environment">
    <el-card shadow="hover" class="filter-card">
      <div class="filter-content">
        <el-form :inline="true" :model="filterForm" class="filter-form">
          <el-form-item label="监测点">
          <el-select v-model="filterForm.monitorPoint" placeholder="选择监测点" @change="onMonitorPointChange">
            <el-option v-for="option in availableMonitorOptions" :key="option.value" :label="option.label" :value="option.value"></el-option>
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
          <el-form-item>
            <el-button type="success" @click="showAddModal = true">添加监测点</el-button>
          </el-form-item>
        </el-form>
      </div>
    </el-card>

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
        <el-table-column prop="temperature" label="温度(°C)" width="120"></el-table-column>
        <el-table-column prop="humidity" label="湿度(%)" width="100"></el-table-column>
        <el-table-column label="操作" width="150">
          <template #default="scope">
            <el-button size="small" @click="deleteMonitorPoint(scope.row.id)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <el-dialog title="添加监测点" v-model="showAddModal" width="500px">
      <el-form :model="addForm" label-width="120px">
        <el-form-item label="监测点名称" required>
          <el-input v-model="addForm.name" placeholder="请输入监测点名称"></el-input>
        </el-form-item>
        <el-form-item label="位置">
          <el-input v-model="addForm.location" placeholder="请输入位置信息"></el-input>
        </el-form-item>
        <el-form-item label="所属农田" required>
          <el-select v-model="addForm.farmlandId" placeholder="请选择所属农田">
            <el-option v-for="farmland in filteredFarmlands" :key="farmland.id" :label="farmland.name" :value="farmland.id"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="初始温度">
          <el-input v-model.number="addForm.temperature" placeholder="请输入初始温度" type="number" step="0.1"></el-input>
        </el-form-item>
        <el-form-item label="初始湿度">
          <el-input v-model.number="addForm.humidity" placeholder="请输入初始湿度" type="number"></el-input>
        </el-form-item>
        <el-form-item label="初始光照">
          <el-input v-model.number="addForm.light" placeholder="请输入初始光照" type="number"></el-input>
        </el-form-item>
        <el-form-item label="初始CO2">
          <el-input v-model.number="addForm.co2" placeholder="请输入初始CO2" type="number"></el-input>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="showAddModal = false">取消</el-button>
        <el-button type="primary" @click="addMonitorPoint">确认添加</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted, onUnmounted, computed } from 'vue'
import * as echarts from 'echarts'
import { useAppStore } from '../stores/index.js'
import { ElMessage, ElMessageBox } from 'element-plus'

const store = useAppStore()
const isAdmin = computed(() => store.user.role === '管理员')

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

const userFarmlands = ref([])
const allMonitorPoints = ref([])
const farmlands = ref([])

const showAddModal = ref(false)
const addForm = ref({
  name: '',
  location: '',
  farmlandId: '',
  temperature: 25.0,
  humidity: 65,
  light: 8000,
  co2: 450
})

const monitorPoints = computed(() => {
  if (isAdmin.value) {
    return allMonitorPoints.value
  }
  return allMonitorPoints.value.filter(mp => {
    const farmland = farmlands.value.find(f => f.id === mp.farmlandId)
    return farmland && userFarmlands.value.includes(farmland.name)
  })
})

const availableMonitorOptions = computed(() => {
  const filteredPoints = isAdmin.value ? allMonitorPoints.value : monitorPoints.value
  return filteredPoints.map(mp => ({
    label: mp.name,
    value: mp.name
  }))
})

const filteredFarmlands = computed(() => {
  if (isAdmin.value) {
    return farmlands.value
  }
  return farmlands.value.filter(farmland => 
    userFarmlands.value.includes(farmland.name)
  )
})

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
  const monitorPoint = filterForm.value.monitorPoint || '大棚A'
  const config = monitorPointConfig[monitorPoint.toLowerCase().replace(/\s/g, '')] || monitorPointConfig['greenhouseA']
  
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

const initChart = () => {
  if (!chartRef.value) return
  
  chart.value = echarts.init(chartRef.value)
  updateChart()
}

const updateChart = () => {
  if (!chart.value) return
  
  const { dates, data } = generateHistoricalData()
  
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
              color: `${indicatorColor}33`
            },
            {
              offset: 1,
              color: `${indicatorColor}11`
            }
          ])
        }
      }
    ]
  }
  
  chart.value.setOption(option)
}

const getIndicatorName = (indicator) => {
  const nameMap = {
    temperature: '温度 (°C)',
    humidity: '湿度 (%)',
    light: '光照 (lux)',
    co2: 'CO2 (ppm)'
  }
  return nameMap[indicator] || ''
}

const getIndicatorColor = (indicator, alpha = 1) => {
  const colorMap = {
    temperature: `rgba(255, 120, 117, ${alpha})`,
    humidity: `rgba(105, 192, 255, ${alpha})`,
    light: `rgba(230, 162, 60, ${alpha})`,
    co2: `rgba(103, 194, 58, ${alpha})`
  }
  return colorMap[indicator] || `rgba(144, 147, 153, ${alpha})`
}

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

const fetchMonitorPoints = async () => {
  try {
    const response = await fetch('/api/monitor-points')
    const data = await response.json()
    if (Array.isArray(data)) {
      allMonitorPoints.value = data.map(mp => ({
        id: mp.id,
        name: mp.name,
        value: mp.name,
        location: mp.location,
        farmlandId: mp.farmlandId,
        status: mp.status,
        lastUpdate: mp.lastUpdate ? formatDateTime(mp.lastUpdate) : '',
        temperature: mp.temperature,
        humidity: mp.humidity,
        light: mp.light,
        co2: mp.co2
      }))
      const filteredOptions = availableMonitorOptions.value
      if (filteredOptions.length > 0) {
        filterForm.value.monitorPoint = filteredOptions[0].value
      }
    }
  } catch (error) {
    console.error('获取监测点数据失败:', error)
  }
}

const fetchFarmlands = async () => {
  try {
    const response = await fetch('/api/farmland')
    const data = await response.json()
    if (Array.isArray(data)) {
      farmlands.value = data
    }
  } catch (error) {
    console.error('获取农田数据失败:', error)
  }
}

const formatDateTime = (dateTimeStr) => {
  if (!dateTimeStr) return ''
  const date = new Date(dateTimeStr)
  return `${date.getFullYear()}-${String(date.getMonth() + 1).padStart(2, '0')}-${String(date.getDate()).padStart(2, '0')} ${String(date.getHours()).padStart(2, '0')}:${String(date.getMinutes()).padStart(2, '0')}`
}

const addMonitorPoint = async () => {
  if (!addForm.value.name || !addForm.value.farmlandId) {
    ElMessage.warning('请填写监测点名称和所属农田')
    return
  }
  
  try {
    const response = await fetch('/api/monitor-points', {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json'
      },
      body: JSON.stringify({
        name: addForm.value.name,
        location: addForm.value.location,
        farmlandId: addForm.value.farmlandId,
        status: '正常',
        temperature: addForm.value.temperature || 25.0,
        humidity: addForm.value.humidity || 65,
        light: addForm.value.light || 8000,
        co2: addForm.value.co2 || 450
      })
    })
    
    const data = await response.json()
    if (data.success) {
      ElMessage.success('添加成功')
      showAddModal.value = false
      addForm.value = {
        name: '',
        location: '',
        farmlandId: '',
        temperature: 25.0,
        humidity: 65,
        light: 8000,
        co2: 450
      }
      await fetchMonitorPoints()
    } else {
      ElMessage.error(data.message || '添加失败')
    }
  } catch (error) {
    console.error('添加监测点失败:', error)
    ElMessage.error('添加失败')
  }
}

const deleteMonitorPoint = async (id) => {
  try {
    await ElMessageBox.confirm(
      '确定要删除这个监测点吗？',
      '提示',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }
    )
    
    const response = await fetch(`/api/monitor-points/${id}`, {
      method: 'DELETE'
    })
    
    const data = await response.json()
    if (data.success) {
      ElMessage.success('删除成功')
      await fetchMonitorPoints()
    } else {
      ElMessage.error(data.message || '删除失败')
    }
  } catch (error) {
    if (error !== 'cancel') {
      console.error('删除监测点失败:', error)
      ElMessage.error('删除失败')
    }
  }
}

const fetchUserFarmlands = async () => {
  if (isAdmin.value) {
    userFarmlands.value = ['大棚A', '大棚B', '大棚C', '露天农田']
    return
  }
  const username = store.user.username || store.user.name
  if (!username) {
    console.error('用户名不存在')
    return
  }
  try {
    const response = await fetch(`/api/user/info?username=${encodeURIComponent(username)}`)
    const data = await response.json()
    if (data.farmlands) {
      userFarmlands.value = data.farmlands
    }
  } catch (error) {
    console.error('获取用户农田失败:', error)
    userFarmlands.value = []
  }
}

onMounted(async () => {
  await fetchUserFarmlands()
  await fetchFarmlands()
  await fetchMonitorPoints()
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