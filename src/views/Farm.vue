<template>
  <div class="farm">
    <div class="action-bar">
      <el-button type="primary" @click="openAddDialog">
        <el-icon><plus /></el-icon>
        新增农田
      </el-button>
      <el-button @click="exportData">
        <el-icon><download /></el-icon>
        导出数据
      </el-button>
    </div>

    <el-card shadow="hover" class="filter-card">
      <div class="filter-content">
        <el-form :inline="true" :model="filterForm" class="filter-form">
          <el-form-item label="农田名称">
            <el-input v-model="filterForm.name" placeholder="输入农田名称" style="width: 200px"></el-input>
          </el-form-item>
          <el-form-item label="区域">
            <el-select v-model="filterForm.area" placeholder="选择区域" class="area-select">
              <el-option label="东区" value="东区"></el-option>
              <el-option label="西区" value="西区"></el-option>
              <el-option label="南区" value="南区"></el-option>
              <el-option label="北区" value="北区"></el-option>
            </el-select>
          </el-form-item>
          <el-form-item>
            <el-button type="primary" @click="searchFarm">查询</el-button>
            <el-button @click="resetFilter">重置</el-button>
          </el-form-item>
        </el-form>
      </div>
    </el-card>

    <el-card shadow="hover" class="farm-list-card">
      <template #header>
        <div class="card-header">
          <span>农田信息列表</span>
          <span class="total-count">共 {{ total }} 条记录</span>
        </div>
      </template>
      <el-table :data="farmList" style="width: 100%">
        <el-table-column type="index" label="序号" width="80"></el-table-column>
        <el-table-column prop="name" label="农田名称"></el-table-column>
        <el-table-column prop="area" label="区域" width="120"></el-table-column>
        <el-table-column prop="acreage" label="面积(亩)" width="120"></el-table-column>
        <el-table-column prop="soilType" label="土壤类型" width="150"></el-table-column>
        <el-table-column prop="crop" label="种植作物" width="150"></el-table-column>
        <el-table-column prop="status" label="状态" width="100">
          <template #default="scope">
            <el-tag :type="getFarmStatusTag(scope.row.status)">{{ scope.row.status }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="200">
          <template #default="scope">
            <el-button type="primary" size="small" @click="openEditDialog(scope.row)">
              编辑
            </el-button>
            <el-button type="danger" size="small" @click="confirmDelete(scope.row.id)">
              删除
            </el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <div class="pagination">
      <el-pagination
        v-model:current-page="currentPage"
        v-model:page-size="pageSize"
        :page-sizes="[10, 20, 50, 100]"
        layout="total, sizes, prev, pager, next, jumper"
        :total="total"
        @size-change="handleSizeChange"
        @current-change="handleCurrentChange"
      />
    </div>

    <el-dialog
      v-model="dialogVisible"
      :title="isEdit ? '编辑农田' : '新增农田'"
      width="500px"
    >
      <el-form :model="formData" :rules="formRules" ref="formRef" label-width="100px">
        <el-form-item label="农田名称" prop="name">
          <el-input v-model="formData.name" placeholder="请输入农田名称"></el-input>
        </el-form-item>
        <el-form-item label="区域" prop="area">
          <el-select v-model="formData.area" placeholder="请选择区域">
            <el-option label="东区" value="东区"></el-option>
            <el-option label="西区" value="西区"></el-option>
            <el-option label="南区" value="南区"></el-option>
            <el-option label="北区" value="北区"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="面积(亩)" prop="acreage">
          <el-input v-model.number="formData.acreage" placeholder="请输入面积"></el-input>
        </el-form-item>
        <el-form-item label="土壤类型" prop="soilType">
          <el-input v-model="formData.soilType" placeholder="请输入土壤类型"></el-input>
        </el-form-item>
        <el-form-item label="种植作物" prop="crop">
          <el-input v-model="formData.crop" placeholder="请输入种植作物"></el-input>
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-select v-model="formData.status" placeholder="请选择状态">
            <el-option label="种植中" value="种植中"></el-option>
            <el-option label="休耕" value="休耕"></el-option>
            <el-option label="闲置" value="闲置"></el-option>
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="saveFarm">保存</el-button>
      </template>
    </el-dialog>

    <el-dialog
      v-model="deleteConfirmVisible"
      title="确认删除"
      width="400px"
    >
      <p>确定要删除这条农田记录吗？此操作无法撤销。</p>
      <template #footer>
        <el-button @click="deleteConfirmVisible = false">取消</el-button>
        <el-button type="danger" @click="doDelete">确认删除</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { Plus, Download } from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'

const filterForm = reactive({
  name: '',
  area: ''
})

const farmList = ref([])
const currentPage = ref(1)
const pageSize = ref(10)
const total = ref(0)

const dialogVisible = ref(false)
const deleteConfirmVisible = ref(false)
const isEdit = ref(false)
const deleteTargetId = ref(null)
const formRef = ref(null)

const formData = reactive({
  id: null,
  name: '',
  area: '',
  acreage: null,
  soilType: '',
  crop: '',
  status: '种植中'
})

const formRules = {
  name: [{ required: true, message: '请输入农田名称', trigger: 'blur' }],
  acreage: [{ required: true, message: '请输入面积', trigger: 'blur' }]
}

const fetchFarmList = async () => {
  try {
    let url = '/api/farmland'
    const params = []
    if (filterForm.name) params.push(`name=${encodeURIComponent(filterForm.name)}`)
    if (filterForm.area) params.push(`area=${encodeURIComponent(filterForm.area)}`)
    if (params.length > 0) url += '?' + params.join('&')
    
    const response = await fetch(url)
    const data = await response.json()
    farmList.value = data
    total.value = data.length
  } catch (error) {
    console.error('获取农田列表失败:', error)
    ElMessage.error('获取农田列表失败')
  }
}

const openAddDialog = () => {
  isEdit.value = false
  formData.id = null
  formData.name = ''
  formData.area = ''
  formData.acreage = null
  formData.soilType = ''
  formData.crop = ''
  formData.status = '种植中'
  dialogVisible.value = true
}

const openEditDialog = (row) => {
  isEdit.value = true
  formData.id = row.id
  formData.name = row.name
  formData.area = row.area
  formData.acreage = row.acreage
  formData.soilType = row.soilType
  formData.crop = row.crop
  formData.status = row.status
  dialogVisible.value = true
}

const saveFarm = async () => {
  try {
    await formRef.value.validate()
    
    const farmData = {
      name: formData.name,
      area: formData.area,
      acreage: formData.acreage,
      soilType: formData.soilType,
      crop: formData.crop,
      status: formData.status
    }

    let response
    if (isEdit.value) {
      response = await fetch(`/api/farmland/${formData.id}`, {
        method: 'PUT',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(farmData)
      })
    } else {
      response = await fetch('/api/farmland', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(farmData)
      })
    }

    const data = await response.json()
    if (data.success) {
      ElMessage.success(isEdit.value ? '修改成功' : '添加成功')
      dialogVisible.value = false
      fetchFarmList()
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
    const response = await fetch(`/api/farmland/${deleteTargetId.value}`, {
      method: 'DELETE'
    })

    const data = await response.json()
    if (data.success) {
      ElMessage.success('删除成功')
      fetchFarmList()
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

const searchFarm = () => {
  currentPage.value = 1
  fetchFarmList()
}

const resetFilter = () => {
  filterForm.name = ''
  filterForm.area = ''
  currentPage.value = 1
  fetchFarmList()
}

const exportData = () => {
  if (farmList.value.length === 0) {
    ElMessage.warning('没有数据可以导出')
    return
  }
  
  const headers = ['农田名称', '区域', '面积(亩)', '土壤类型', '种植作物', '状态']
  const rows = farmList.value.map(item => [
    item.name,
    item.area,
    item.acreage,
    item.soilType,
    item.crop,
    item.status
  ])
  
  let content = headers.join('\t') + '\n'
  rows.forEach(row => {
    content += row.join('\t') + '\n'
  })
  
  const blob = new Blob([content], { type: 'text/plain;charset=utf-8' })
  const url = URL.createObjectURL(blob)
  const link = document.createElement('a')
  link.href = url
  link.download = `农田信息_${new Date().toLocaleDateString('zh-CN').replace(/\//g, '-')}.txt`
  document.body.appendChild(link)
  link.click()
  document.body.removeChild(link)
  URL.revokeObjectURL(url)
  
  ElMessage.success('导出成功')
}

const getFarmStatusTag = (status) => {
  const tagMap = {
    '种植中': 'success',
    '休耕': 'info',
    '闲置': 'warning'
  }
  return tagMap[status] || 'default'
}

const handleSizeChange = (size) => {
  pageSize.value = size
}

const handleCurrentChange = (current) => {
  currentPage.value = current
}

fetchFarmList()
</script>

<style scoped>
.farm {
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

.filter-form {
  display: flex;
  align-items: center;
  gap: 16px;
}

.area-select {
  width: 150px;
}

.area-select :deep(.el-select__wrapper) {
  border-radius: 6px;
  border: 1px solid #dcdfe6;
  box-shadow: none;
  background: #fff;
}

.area-select :deep(.el-select__wrapper:hover) {
  border-color: #c0c4cc;
}

.area-select :deep(.el-select__wrapper.is-focus) {
  border-color: #409eff;
  box-shadow: 0 0 0 2px rgba(64, 158, 255, 0.1);
}

.area-select :deep(.el-select__trigger) {
  padding: 0 25px 0 15px;
}

.area-select :deep(.el-select__caret) {
  color: #909399;
}

.farm-list-card {
  flex: 1;
  min-height: 0;
  display: flex;
  flex-direction: column;
}

.farm-list-card :deep(.el-card__body) {
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

.pagination {
  display: flex;
  justify-content: flex-end;
  flex-shrink: 0;
}
</style>