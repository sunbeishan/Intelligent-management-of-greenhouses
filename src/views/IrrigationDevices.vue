<template>
  <div class="devices-container">
    <div class="action-bar">
      <el-button type="primary" @click="openAddDialog">
        <el-icon><plus /></el-icon>
        新增设备
      </el-button>
    </div>

    <el-card shadow="hover" class="filter-card">
      <div class="filter-content">
        <el-form :inline="true" :model="filterForm">
          <el-form-item label="所属农田">
            <el-select v-model="filterForm.farmlandId" placeholder="选择农田" clearable style="width: 200px">
              <el-option v-for="farm in farmlands" :key="farm.id" :label="farm.name" :value="farm.id"></el-option>
            </el-select>
          </el-form-item>
          <el-form-item>
            <el-button type="primary" @click="searchDevices">查询</el-button>
            <el-button @click="resetFilter">重置</el-button>
          </el-form-item>
        </el-form>
      </div>
    </el-card>

    <el-card shadow="hover" class="device-list-card">
      <template #header>
        <div class="card-header">
          <span>灌溉设备列表</span>
          <span class="total-count">共 {{ deviceList.length }} 条记录</span>
        </div>
      </template>
      <el-table :data="deviceList" style="width: 100%">
        <el-table-column type="index" label="序号" width="80"></el-table-column>
        <el-table-column prop="name" label="设备名称"></el-table-column>
        <el-table-column prop="farmlandName" label="所属农田"></el-table-column>
        <el-table-column prop="deviceType" label="设备类型" width="120"></el-table-column>
        <el-table-column prop="status" label="状态" width="120">
          <template #default="scope">
            <el-tag :type="getStatusType(scope.row.status)">{{ scope.row.status }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="waterFlow" label="水流量(升/分)" width="140"></el-table-column>
        <el-table-column prop="coverageArea" label="覆盖面积(亩)" width="140"></el-table-column>
        <el-table-column label="操作" width="200">
          <template #default="scope">
            <el-button type="primary" size="small" @click="openEditDialog(scope.row)">编辑</el-button>
            <el-button type="danger" size="small" @click="confirmDelete(scope.row.id)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <el-dialog v-model="dialogVisible" :title="isEdit ? '编辑设备' : '新增设备'" width="500px">
      <el-form :model="formData" :rules="formRules" ref="formRef" label-width="100px">
        <el-form-item label="设备名称" prop="name">
          <el-input v-model="formData.name" placeholder="请输入设备名称"></el-input>
        </el-form-item>
        <el-form-item label="所属农田" prop="farmlandId">
          <el-select v-model="formData.farmlandId" placeholder="请选择农田">
            <el-option v-for="farm in farmlands" :key="farm.id" :label="farm.name" :value="farm.id"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="设备类型" prop="deviceType">
          <el-select v-model="formData.deviceType" placeholder="请选择设备类型">
            <el-option label="喷灌" value="喷灌"></el-option>
            <el-option label="滴灌" value="滴灌"></el-option>
            <el-option label="漫灌" value="漫灌"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="设备状态" prop="status">
          <el-select v-model="formData.status" placeholder="请选择状态">
            <el-option label="正常" value="正常"></el-option>
            <el-option label="故障" value="故障"></el-option>
            <el-option label="维护中" value="维护中"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="水流量" prop="waterFlow">
          <el-input-number v-model="formData.waterFlow" :min="0" :precision="2" placeholder="升/分钟"></el-input-number>
        </el-form-item>
        <el-form-item label="覆盖面积" prop="coverageArea">
          <el-input-number v-model="formData.coverageArea" :min="0" :precision="2" placeholder="亩"></el-input-number>
        </el-form-item>
        <el-form-item label="安装日期" prop="installDate">
          <el-date-picker v-model="formData.installDate" type="date" placeholder="选择日期" value-format="YYYY-MM-DD"></el-date-picker>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="saveDevice">保存</el-button>
      </template>
    </el-dialog>

    <el-dialog v-model="deleteConfirmVisible" title="确认删除" width="400px">
      <p>确定要删除这条设备记录吗？此操作无法撤销。</p>
      <template #footer>
        <el-button @click="deleteConfirmVisible = false">取消</el-button>
        <el-button type="danger" @click="doDelete">确认删除</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { Plus } from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'

const emit = defineEmits(['refresh'])

const deviceList = ref([])
const farmlands = ref([])
const dialogVisible = ref(false)
const deleteConfirmVisible = ref(false)
const isEdit = ref(false)
const deleteTargetId = ref(null)
const formRef = ref(null)

const filterForm = reactive({
  farmlandId: null
})

const formData = reactive({
  id: null,
  name: '',
  farmlandId: null,
  deviceType: '',
  status: '正常',
  waterFlow: 0,
  coverageArea: 0,
  installDate: ''
})

const formRules = {
  name: [{ required: true, message: '请输入设备名称', trigger: 'blur' }],
  farmlandId: [{ required: true, message: '请选择所属农田', trigger: 'change' }]
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
    let url = '/api/irrigation/devices'
    if (filterForm.farmlandId) {
      url += `?farmlandId=${filterForm.farmlandId}`
    }
    const response = await fetch(url)
    deviceList.value = await response.json()
  } catch (error) {
    console.error('获取设备列表失败:', error)
    ElMessage.error('获取设备列表失败')
  }
}

const openAddDialog = () => {
  isEdit.value = false
  formData.id = null
  formData.name = ''
  formData.farmlandId = null
  formData.deviceType = ''
  formData.status = '正常'
  formData.waterFlow = 0
  formData.coverageArea = 0
  formData.installDate = ''
  dialogVisible.value = true
}

const openEditDialog = (row) => {
  isEdit.value = true
  formData.id = row.id
  formData.name = row.name
  formData.farmlandId = row.farmlandId
  formData.deviceType = row.deviceType
  formData.status = row.status
  formData.waterFlow = row.waterFlow
  formData.coverageArea = row.coverageArea
  formData.installDate = row.installDate || ''
  dialogVisible.value = true
}

const saveDevice = async () => {
  try {
    await formRef.value.validate()

    const deviceData = {
      name: formData.name,
      farmlandId: formData.farmlandId,
      deviceType: formData.deviceType,
      status: formData.status,
      waterFlow: formData.waterFlow,
      coverageArea: formData.coverageArea,
      installDate: formData.installDate
    }

    let response
    if (isEdit.value) {
      response = await fetch(`/api/irrigation/devices/${formData.id}`, {
        method: 'PUT',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(deviceData)
      })
    } else {
      response = await fetch('/api/irrigation/devices', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(deviceData)
      })
    }

    const data = await response.json()
    if (data.success) {
      ElMessage.success(isEdit.value ? '修改成功' : '添加成功')
      dialogVisible.value = false
      fetchDevices()
      emit('refresh')
    } else {
      ElMessage.error(data.message)
    }
  } catch (error) {
    console.error('保存失败:', error)
    ElMessage.error('保存失败')
  }
}

const confirmDelete = (id) => {
  deleteTargetId.value = id
  deleteConfirmVisible.value = true
}

const doDelete = async () => {
  try {
    const response = await fetch(`/api/irrigation/devices/${deleteTargetId.value}`, {
      method: 'DELETE'
    })

    const data = await response.json()
    if (data.success) {
      ElMessage.success('删除成功')
      fetchDevices()
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

const searchDevices = () => {
  fetchDevices()
}

const resetFilter = () => {
  filterForm.farmlandId = null
  fetchDevices()
}

const getStatusType = (status) => {
  const typeMap = {
    '正常': 'success',
    '故障': 'danger',
    '维护中': 'warning'
  }
  return typeMap[status] || 'info'
}

onMounted(() => {
  fetchFarmlands()
  fetchDevices()
})
</script>

<style scoped>
.devices-container {
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

.filter-card {
  flex-shrink: 0;
}

.filter-content {
  padding: 10px 0;
}

.device-list-card {
  flex: 1;
  min-height: 0;
  display: flex;
  flex-direction: column;
}

.device-list-card :deep(.el-card__body) {
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