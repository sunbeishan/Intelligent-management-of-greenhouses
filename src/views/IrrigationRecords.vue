<template>
  <div class="records-container">
    <div class="action-bar">
      <el-button type="primary" @click="openAddDialog">
        <el-icon><plus /></el-icon>
        手动灌溉
      </el-button>
      <el-button @click="exportData">
        <el-icon><download /></el-icon>
        导出数据
      </el-button>
    </div>

    <el-card shadow="hover" class="statistics-card">
      <div class="statistics-content">
        <div class="stat-item">
          <div class="stat-label">今日用水量</div>
          <div class="stat-value">{{ todayWaterUsage.toFixed(2) }} 升</div>
        </div>
        <div class="stat-item">
          <div class="stat-label">本周用水量</div>
          <div class="stat-value">{{ weekWaterUsage.toFixed(2) }} 升</div>
        </div>
        <div class="stat-item">
          <div class="stat-label">本月用水量</div>
          <div class="stat-value">{{ monthWaterUsage.toFixed(2) }} 升</div>
        </div>
        <div class="stat-item">
          <div class="stat-label">总记录数</div>
          <div class="stat-value">{{ recordList.length }} 条</div>
        </div>
      </div>
    </el-card>

    <el-card shadow="hover" class="filter-card">
      <div class="filter-content">
        <el-form :inline="true" :model="filterForm">
          <el-form-item label="所属农田">
            <el-select v-model="filterForm.farmlandId" placeholder="选择农田" clearable style="width: 200px">
              <el-option v-for="farm in farmlands" :key="farm.id" :label="farm.name" :value="farm.id"></el-option>
            </el-select>
          </el-form-item>
          <el-form-item label="日期范围">
            <el-date-picker v-model="filterForm.dateRange" type="daterange" range-separator="至" start-placeholder="开始日期" end-placeholder="结束日期" value-format="YYYY-MM-DD"></el-date-picker>
          </el-form-item>
          <el-form-item>
            <el-button type="primary" @click="searchRecords">查询</el-button>
            <el-button @click="resetFilter">重置</el-button>
          </el-form-item>
        </el-form>
      </div>
    </el-card>

    <el-card shadow="hover" class="record-list-card">
      <template #header>
        <div class="card-header">
          <span>灌溉记录列表</span>
          <span class="total-count">共 {{ recordList.length }} 条记录</span>
        </div>
      </template>
      <el-table :data="recordList" style="width: 100%">
        <el-table-column type="index" label="序号" width="80"></el-table-column>
        <el-table-column prop="farmlandName" label="所属农田"></el-table-column>
        <el-table-column prop="deviceName" label="设备名称"></el-table-column>
        <el-table-column prop="planName" label="关联计划"></el-table-column>
        <el-table-column prop="startTime" label="开始时间" width="160"></el-table-column>
        <el-table-column prop="duration" label="持续时间(分)" width="120"></el-table-column>
        <el-table-column prop="waterAmount" label="用水量(升)" width="120"></el-table-column>
        <el-table-column prop="type" label="类型" width="100">
          <template #default="scope">
            <el-tag :type="scope.row.type === '自动' ? 'success' : 'primary'">{{ scope.row.type }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="operator" label="操作人" width="100"></el-table-column>
        <el-table-column prop="remark" label="备注"></el-table-column>
      </el-table>
    </el-card>

    <el-dialog v-model="dialogVisible" title="手动灌溉" width="500px">
      <el-form :model="formData" :rules="formRules" ref="formRef" label-width="100px">
        <el-form-item label="所属农田" prop="farmlandId">
          <el-select v-model="formData.farmlandId" placeholder="请选择农田" @change="onFarmlandChange">
            <el-option v-for="farm in farmlands" :key="farm.id" :label="farm.name" :value="farm.id"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="灌溉设备" prop="deviceId">
          <el-select v-model="formData.deviceId" placeholder="请先选择农田">
            <el-option v-for="device in filteredDevices" :key="device.id" :label="device.name" :value="device.id"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="持续时间" prop="duration">
          <el-input-number v-model="formData.duration" :min="1" :max="1440" placeholder="分钟"></el-input-number>
        </el-form-item>
        <el-form-item label="用水量" prop="waterAmount">
          <el-input-number v-model="formData.waterAmount" :min="0" :precision="2" placeholder="升"></el-input-number>
        </el-form-item>
        <el-form-item label="操作人" prop="operator">
          <el-input v-model="formData.operator" placeholder="请输入操作人"></el-input>
        </el-form-item>
        <el-form-item label="备注" prop="remark">
          <el-input v-model="formData.remark" type="textarea" :rows="3" placeholder="请输入备注"></el-input>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="saveRecord">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { Plus, Download } from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'
import { useAppStore } from '../stores/index.js'

const emit = defineEmits(['refresh'])
const store = useAppStore()

const recordList = ref([])
const farmlands = ref([])
const devices = ref([])
const dialogVisible = ref(false)
const formRef = ref(null)
const userFarmlandIds = ref([])
const isAdmin = computed(() => store.user.role === '管理员')

const todayWaterUsage = ref(0)
const weekWaterUsage = ref(0)
const monthWaterUsage = ref(0)

const filterForm = reactive({
  farmlandId: null,
  dateRange: null
})

const formData = reactive({
  farmlandId: null,
  deviceId: null,
  duration: 30,
  waterAmount: 0,
  operator: '',
  remark: ''
})

const formRules = {
  farmlandId: [{ required: true, message: '请选择农田', trigger: 'change' }],
  deviceId: [{ required: true, message: '请选择设备', trigger: 'change' }],
  duration: [{ required: true, message: '请输入持续时间', trigger: 'blur' }]
}

const filteredDevices = computed(() => {
  if (!formData.farmlandId) return []
  return devices.value.filter(d => d.farmlandId === formData.farmlandId)
})

const getToday = () => {
  const now = new Date()
  return now.toISOString().split('T')[0]
}

const getWeekStart = () => {
  const now = new Date()
  const dayOfWeek = now.getDay() || 7
  const weekStart = new Date(now)
  weekStart.setDate(now.getDate() - dayOfWeek + 1)
  return weekStart.toISOString().split('T')[0]
}

const getMonthStart = () => {
  const now = new Date()
  return `${now.getFullYear()}-${String(now.getMonth() + 1).padStart(2, '0')}-01`
}

const fetchFarmlands = async () => {
  try {
    const response = await fetch('/api/farmland')
    farmlands.value = await response.json()
  } catch (error) {
    console.error('获取农田列表失败:', error)
  }
}

const fetchDevices = async () => {
  try {
    const response = await fetch('/api/irrigation/devices')
    devices.value = await response.json()
  } catch (error) {
    console.error('获取设备列表失败:', error)
  }
}

const fetchUserFarmlandIds = async () => {
  if (isAdmin.value) {
    userFarmlandIds.value = []
    return
  }
  const username = store.user.username || store.user.name
  if (!username) return
  try {
    const response = await fetch(`/api/user/info?username=${encodeURIComponent(username)}`)
    const data = await response.json()
    if (data.farmlands && farmlands.value.length > 0) {
      userFarmlandIds.value = farmlands.value
        .filter(f => data.farmlands.includes(f.name))
        .map(f => f.id)
    }
  } catch (error) {
    console.error('获取用户农田失败:', error)
    userFarmlandIds.value = []
  }
}

const fetchRecords = async () => {
  try {
    let url = '/api/irrigation/records'
    const params = []
    
    if (!isAdmin.value && userFarmlandIds.value.length > 0) {
      params.push(`farmlandIds=${userFarmlandIds.value.join(',')}`)
    }
    
    if (filterForm.farmlandId) {
      params.push(`farmlandId=${filterForm.farmlandId}`)
    }
    if (filterForm.dateRange && filterForm.dateRange.length === 2) {
      params.push(`startDate=${filterForm.dateRange[0]}`)
      params.push(`endDate=${filterForm.dateRange[1]}`)
    }
    if (params.length > 0) {
      url += '?' + params.join('&')
    }
    const response = await fetch(url)
    recordList.value = await response.json()
  } catch (error) {
    console.error('获取记录列表失败:', error)
    ElMessage.error('获取记录列表失败')
  }
}

const fetchStatistics = async () => {
  try {
    const today = getToday()
    const weekStart = getWeekStart()
    const monthStart = getMonthStart()

    const farmlandParam = (!isAdmin.value && userFarmlandIds.value.length > 0) 
      ? `&farmlandIds=${userFarmlandIds.value.join(',')}` 
      : ''

    const todayRes = await fetch(`/api/irrigation/records/statistics?startDate=${today}&endDate=${today}${farmlandParam}`)
    const todayData = await todayRes.json()
    todayWaterUsage.value = todayData.totalWater || 0

    const weekRes = await fetch(`/api/irrigation/records/statistics?startDate=${weekStart}&endDate=${today}${farmlandParam}`)
    const weekData = await weekRes.json()
    weekWaterUsage.value = weekData.totalWater || 0

    const monthRes = await fetch(`/api/irrigation/records/statistics?startDate=${monthStart}&endDate=${today}${farmlandParam}`)
    const monthData = await monthRes.json()
    monthWaterUsage.value = monthData.totalWater || 0
  } catch (error) {
    console.error('获取统计数据失败:', error)
  }
}

const onFarmlandChange = () => {
  formData.deviceId = null
}

const openAddDialog = () => {
  formData.farmlandId = null
  formData.deviceId = null
  formData.duration = 30
  formData.waterAmount = 0
  formData.operator = ''
  formData.remark = ''
  dialogVisible.value = true
}

const saveRecord = async () => {
  try {
    await formRef.value.validate()

    const recordData = {
      farmlandId: formData.farmlandId,
      deviceId: formData.deviceId,
      duration: formData.duration,
      waterAmount: formData.waterAmount,
      type: '手动',
      operator: formData.operator,
      remark: formData.remark
    }

    const response = await fetch('/api/irrigation/records', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(recordData)
    })

    const data = await response.json()
    if (data.success) {
      ElMessage.success('灌溉记录已创建')
      dialogVisible.value = false
      fetchRecords()
      fetchStatistics()
      emit('refresh')
    } else {
      ElMessage.error(data.message)
    }
  } catch (error) {
    console.error('创建失败:', error)
    ElMessage.error('创建失败')
  }
}

const searchRecords = () => {
  fetchRecords()
}

const resetFilter = () => {
  filterForm.farmlandId = null
  filterForm.dateRange = null
  fetchRecords()
}

const exportData = () => {
  if (recordList.value.length === 0) {
    ElMessage.warning('没有数据可以导出')
    return
  }

  const headers = ['所属农田', '设备名称', '关联计划', '开始时间', '持续时间(分)', '用水量(升)', '类型', '操作人', '备注']
  const rows = recordList.value.map(item => [
    item.farmlandName || '',
    item.deviceName || '',
    item.planName || '',
    item.startTime || '',
    item.duration || '',
    item.waterAmount || '',
    item.type || '',
    item.operator || '',
    item.remark || ''
  ])

  let content = headers.join('\t') + '\n'
  rows.forEach(row => {
    content += row.join('\t') + '\n'
  })

  const blob = new Blob([content], { type: 'text/plain;charset=utf-8' })
  const url = URL.createObjectURL(blob)
  const link = document.createElement('a')
  link.href = url
  link.download = `灌溉记录_${new Date().toLocaleDateString('zh-CN').replace(/\//g, '-')}.txt`
  document.body.appendChild(link)
  link.click()
  document.body.removeChild(link)
  URL.revokeObjectURL(url)

  ElMessage.success('导出成功')
}

onMounted(async () => {
  await fetchFarmlands()
  await fetchDevices()
  await fetchUserFarmlandIds()
  fetchRecords()
  fetchStatistics()
})
</script>

<style scoped>
.records-container {
  display: flex;
  flex-direction: column;
  gap: 20px;
  height: 100%;
  box-sizing: border-box;
}

.action-bar {
  display: flex;
  gap: 12px;
  flex-shrink: 0;
}

.statistics-card {
  flex-shrink: 0;
}

.statistics-content {
  display: flex;
  justify-content: space-around;
  padding: 10px 0;
}

.stat-item {
  text-align: center;
}

.stat-label {
  font-size: 14px;
  color: #909399;
  margin-bottom: 8px;
}

.stat-value {
  font-size: 20px;
  font-weight: bold;
  color: #409eff;
}

.filter-card {
  flex-shrink: 0;
}

.filter-content {
  padding: 10px 0;
}

.record-list-card {
  flex: 1;
  min-height: 0;
  display: flex;
  flex-direction: column;
}

.record-list-card :deep(.el-card__body) {
  flex: 1;
  overflow: auto;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.total-count {
  font-size: 14px;
  color: #909399;
}
</style>