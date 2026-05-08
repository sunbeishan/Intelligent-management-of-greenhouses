<template>
  <div class="plans-container">
    <div class="action-bar">
      <el-button type="primary" @click="openAddDialog">
        <el-icon><plus /></el-icon>
        新增计划
      </el-button>
    </div>

    <el-card shadow="hover" class="plan-list-card">
      <template #header>
        <div class="card-header">
          <span>灌溉计划列表</span>
          <span class="total-count">共 {{ planList.length }} 条记录</span>
        </div>
      </template>
      <el-table :data="planList" style="width: 100%">
        <el-table-column type="index" label="序号" width="80"></el-table-column>
        <el-table-column prop="planName" label="计划名称"></el-table-column>
        <el-table-column prop="farmlandName" label="所属农田"></el-table-column>
        <el-table-column prop="deviceName" label="设备名称"></el-table-column>
        <el-table-column prop="startTime" label="开始时间" width="120"></el-table-column>
        <el-table-column prop="duration" label="持续时间(分)" width="120"></el-table-column>
        <el-table-column prop="waterAmount" label="用水量(升)" width="120"></el-table-column>
        <el-table-column prop="frequency" label="频率" width="100"></el-table-column>
        <el-table-column prop="status" label="状态" width="100">
          <template #default="scope">
            <el-tag :type="scope.row.status === '启用' ? 'success' : 'info'">{{ scope.row.status }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="280">
          <template #default="scope">
            <el-button type="primary" size="small" @click="openEditDialog(scope.row)">编辑</el-button>
            <el-button size="small" @click="toggleStatus(scope.row)">
              {{ scope.row.status === '启用' ? '禁用' : '启用' }}
            </el-button>
            <el-button type="danger" size="small" @click="confirmDelete(scope.row.id)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <el-dialog v-model="dialogVisible" :title="isEdit ? '编辑计划' : '新增计划'" width="600px">
      <el-form :model="formData" :rules="formRules" ref="formRef" label-width="100px">
        <el-form-item label="计划名称" prop="planName">
          <el-input v-model="formData.planName" placeholder="请输入计划名称"></el-input>
        </el-form-item>
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
        <el-form-item label="开始时间" prop="startTime">
          <el-time-picker v-model="formData.startTime" format="HH:mm:ss" value-format="HH:mm:ss" placeholder="选择时间"></el-time-picker>
        </el-form-item>
        <el-form-item label="持续时间" prop="duration">
          <el-input-number v-model="formData.duration" :min="1" :max="1440" placeholder="分钟"></el-input-number>
        </el-form-item>
        <el-form-item label="用水量" prop="waterAmount">
          <el-input-number v-model="formData.waterAmount" :min="0" :precision="2" placeholder="升"></el-input-number>
        </el-form-item>
        <el-form-item label="频率" prop="frequency">
          <el-select v-model="formData.frequency" placeholder="请选择频率">
            <el-option label="每天" value="每天"></el-option>
            <el-option label="每周" value="每周"></el-option>
            <el-option label="自定义" value="自定义"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="执行日期" prop="weekDays" v-if="formData.frequency === '每周' || formData.frequency === '自定义'">
          <el-checkbox-group v-model="selectedWeekDays">
            <el-checkbox label="1">周一</el-checkbox>
            <el-checkbox label="2">周二</el-checkbox>
            <el-checkbox label="3">周三</el-checkbox>
            <el-checkbox label="4">周四</el-checkbox>
            <el-checkbox label="5">周五</el-checkbox>
            <el-checkbox label="6">周六</el-checkbox>
            <el-checkbox label="7">周日</el-checkbox>
          </el-checkbox-group>
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-select v-model="formData.status" placeholder="请选择状态">
            <el-option label="启用" value="启用"></el-option>
            <el-option label="禁用" value="禁用"></el-option>
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="savePlan">保存</el-button>
      </template>
    </el-dialog>

    <el-dialog v-model="deleteConfirmVisible" title="确认删除" width="400px">
      <p>确定要删除这条计划记录吗？此操作无法撤销。</p>
      <template #footer>
        <el-button @click="deleteConfirmVisible = false">取消</el-button>
        <el-button type="danger" @click="doDelete">确认删除</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { Plus } from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'

const emit = defineEmits(['refresh'])

const planList = ref([])
const farmlands = ref([])
const devices = ref([])
const dialogVisible = ref(false)
const deleteConfirmVisible = ref(false)
const isEdit = ref(false)
const deleteTargetId = ref(null)
const formRef = ref(null)
const selectedWeekDays = ref(['1', '2', '3', '4', '5', '6', '7'])

const formData = reactive({
  id: null,
  planName: '',
  farmlandId: null,
  deviceId: null,
  startTime: '08:00:00',
  duration: 30,
  waterAmount: 0,
  frequency: '每天',
  weekDays: '1,2,3,4,5,6,7',
  status: '启用'
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

const fetchPlans = async () => {
  try {
    const response = await fetch('/api/irrigation/plans')
    planList.value = await response.json()
  } catch (error) {
    console.error('获取计划列表失败:', error)
    ElMessage.error('获取计划列表失败')
  }
}

const onFarmlandChange = () => {
  formData.deviceId = null
}

const openAddDialog = () => {
  isEdit.value = false
  formData.id = null
  formData.planName = ''
  formData.farmlandId = null
  formData.deviceId = null
  formData.startTime = '08:00:00'
  formData.duration = 30
  formData.waterAmount = 0
  formData.frequency = '每天'
  selectedWeekDays.value = ['1', '2', '3', '4', '5', '6', '7']
  formData.status = '启用'
  dialogVisible.value = true
}

const openEditDialog = (row) => {
  isEdit.value = true
  formData.id = row.id
  formData.planName = row.planName
  formData.farmlandId = row.farmlandId
  formData.deviceId = row.deviceId
  formData.startTime = row.startTime || '08:00:00'
  formData.duration = row.duration
  formData.waterAmount = row.waterAmount
  formData.frequency = row.frequency
  formData.weekDays = row.weekDays
  selectedWeekDays.value = row.weekDays ? row.weekDays.split(',') : ['1', '2', '3', '4', '5', '6', '7']
  formData.status = row.status
  dialogVisible.value = true
}

const savePlan = async () => {
  try {
    await formRef.value.validate()

    const weekDaysStr = selectedWeekDays.value.sort().join(',')

    const planData = {
      planName: formData.planName,
      farmlandId: formData.farmlandId,
      deviceId: formData.deviceId,
      startTime: formData.startTime,
      duration: formData.duration,
      waterAmount: formData.waterAmount,
      frequency: formData.frequency,
      weekDays: weekDaysStr,
      status: formData.status
    }

    let response
    if (isEdit.value) {
      response = await fetch(`/api/irrigation/plans/${formData.id}`, {
        method: 'PUT',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(planData)
      })
    } else {
      response = await fetch('/api/irrigation/plans', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(planData)
      })
    }

    const data = await response.json()
    if (data.success) {
      ElMessage.success(isEdit.value ? '修改成功' : '添加成功')
      dialogVisible.value = false
      fetchPlans()
      emit('refresh')
    } else {
      ElMessage.error(data.message)
    }
  } catch (error) {
    console.error('保存失败:', error)
    ElMessage.error('保存失败')
  }
}

const toggleStatus = async (row) => {
  try {
    const newStatus = row.status === '启用' ? '禁用' : '启用'
    const planData = {
      planName: row.planName,
      farmlandId: row.farmlandId,
      deviceId: row.deviceId,
      startTime: row.startTime,
      duration: row.duration,
      waterAmount: row.waterAmount,
      frequency: row.frequency,
      weekDays: row.weekDays,
      status: newStatus
    }

    const response = await fetch(`/api/irrigation/plans/${row.id}`, {
      method: 'PUT',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(planData)
    })

    const data = await response.json()
    if (data.success) {
      ElMessage.success(`计划已${newStatus}`)
      fetchPlans()
      emit('refresh')
    } else {
      ElMessage.error(data.message)
    }
  } catch (error) {
    console.error('操作失败:', error)
    ElMessage.error('操作失败')
  }
}

const confirmDelete = (id) => {
  deleteTargetId.value = id
  deleteConfirmVisible.value = true
}

const doDelete = async () => {
  try {
    const response = await fetch(`/api/irrigation/plans/${deleteTargetId.value}`, {
      method: 'DELETE'
    })

    const data = await response.json()
    if (data.success) {
      ElMessage.success('删除成功')
      fetchPlans()
      emit('refresh')
    } else {
      ElMessage.error(data.message)
    }
  } catch (error) {
    console.error('删除失败:', error)
    ElMessage.error('删除失败')
  } finally {
    deleteConfirmVisible.value = false
    deleteTargetId.value = null
  }
}

onMounted(() => {
  fetchFarmlands()
  fetchDevices()
  fetchPlans()
})
</script>

<style scoped>
.plans-container {
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

.plan-list-card {
  flex: 1;
  min-height: 0;
  display: flex;
  flex-direction: column;
}

.plan-list-card :deep(.el-card__body) {
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