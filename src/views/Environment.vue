<template>
  <div class="environment">
    <!-- 监测点选择 -->
    <el-card shadow="hover" class="filter-card">
      <div class="filter-content">
        <el-form :inline="true" :model="filterForm" class="filter-form">
          <el-form-item label="监测点">
            <el-select v-model="filterForm.monitorPoint" placeholder="选择监测点">
              <el-option label="大棚A" value="greenhouseA"></el-option>
              <el-option label="大棚B" value="greenhouseB"></el-option>
              <el-option label="大棚C" value="greenhouseC"></el-option>
              <el-option label="露天农田" value="openField"></el-option>
            </el-select>
          </el-form-item>
          <el-form-item label="时间范围">
            <el-date-picker
              v-model="filterForm.dateRange"
              type="daterange"
              range-separator="至"
              start-placeholder="开始日期"
              end-placeholder="结束日期"
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
        </div>
      </template>
      <div class="chart-container">
        <div class="chart-placeholder">
          <el-icon class="chart-icon"><monitor /></el-icon>
          <p>环境数据趋势图表</p>
        </div>
      </div>
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
            <el-tag :type="getMonitorStatusTag(scope.row.status)">{{ scope.row.status }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="lastUpdate" label="最后更新" width="180"></el-table-column>
        <el-table-column label="操作" width="150">
          <template #default="scope">
            <el-button type="primary" size="small" @click="viewDetails(scope.row)">
              查看详情
            </el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { Monitor } from '@element-plus/icons-vue'

const filterForm = ref({
  monitorPoint: '',
  dateRange: []
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

const searchData = () => {
  // 模拟查询数据
  console.log('查询数据:', filterForm.value)
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
</script>

<style scoped>
.environment {
  display: flex;
  flex-direction: column;
  gap: 20px;
  height: 100%;
  box-sizing: border-box;
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

.chart-container {
  flex: 1;
  display: flex;
  align-items: center;
  justify-content: center;
  min-height: 0;
}

.chart-placeholder {
  text-align: center;
  color: #909399;
}

.chart-icon {
  font-size: 48px;
  margin-bottom: 16px;
}

.monitor-list-card {
  flex: 1;
  min-height: 0;
  display: flex;
  flex-direction: column;
}

.monitor-list-card :deep(.el-card__body) {
  flex: 1;
  overflow: auto;
}
</style>
