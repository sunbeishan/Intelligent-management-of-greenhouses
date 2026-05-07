<template>
  <div class="materials-inventory">
    <!-- 操作按钮 -->
    <div class="action-bar">
      <el-button type="primary" @click="openAddDialog">
        <el-icon><plus /></el-icon>
        新增库存
      </el-button>
      <el-button type="primary" @click="exportInventory">
        <el-icon><download /></el-icon>
        导出库存
      </el-button>
      <el-button type="success" @click="inventoryAlert">
        <el-icon><warning /></el-icon>
        库存预警
      </el-button>
    </div>

    <!-- 搜索和筛选 -->
    <el-card shadow="hover" class="filter-card">
      <div class="filter-content">
        <el-form :inline="true" :model="filterForm" class="filter-form">
          <el-form-item label="名称">
            <el-input v-model="filterForm.name" placeholder="输入名称" style="width: 200px"></el-input>
          </el-form-item>
          <el-form-item label="类型">
            <el-select v-model="filterForm.type" placeholder="选择类型" class="type-select">
              <el-option label="农资" value="农资"></el-option>
              <el-option label="产品" value="产品"></el-option>
            </el-select>
          </el-form-item>
          <el-form-item label="库存状态">
            <el-select v-model="filterForm.status" placeholder="选择状态" class="status-select">
              <el-option label="正常" value="normal"></el-option>
              <el-option label="不足" value="low"></el-option>
              <el-option label="过多" value="excess"></el-option>
            </el-select>
          </el-form-item>
          <el-form-item>
            <el-button type="primary" @click="searchInventory">查询</el-button>
            <el-button @click="resetFilter">重置</el-button>
          </el-form-item>
        </el-form>
      </div>
    </el-card>

    <!-- 库存列表 -->
    <el-card shadow="hover" class="inventory-card">
      <template #header>
        <div class="card-header">
          <span>库存列表</span>
        </div>
      </template>
      <el-table :data="inventory" style="width: 100%" v-loading="loading">
        <el-table-column prop="id" label="编号" width="100"></el-table-column>
        <el-table-column prop="name" label="名称"></el-table-column>
        <el-table-column prop="type" label="类型" width="100"></el-table-column>
        <el-table-column prop="category" label="类别" width="100"></el-table-column>
        <el-table-column prop="spec" label="规格" width="150"></el-table-column>
        <el-table-column prop="stock" label="库存" width="100">
          <template #default="scope">
            <span :class="{
              'text-danger': scope.row.stock < 50,
              'text-warning': scope.row.stock >= 50 && scope.row.stock < 100,
              'text-success': scope.row.stock >= 100
            }">
              {{ scope.row.stock }}
            </span>
          </template>
        </el-table-column>
        <el-table-column prop="unit" label="单位" width="80"></el-table-column>
        <el-table-column prop="lastUpdate" label="更新时间" width="180"></el-table-column>
        <el-table-column label="操作" width="180">
          <template #default="scope">
            <el-button type="primary" size="small" @click="openEditDialog(scope.row)">编辑</el-button>
            <el-button type="danger" size="small" @click="deleteInventory(scope.row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <!-- 新增/编辑对话框 -->
    <el-dialog
      v-model="dialogVisible"
      :title="isEdit ? '编辑库存' : '新增库存'"
      width="500px"
    >
      <el-form :model="inventoryForm" :rules="inventoryRules" ref="inventoryFormRef" label-width="80px">
        <el-form-item label="名称" prop="name">
          <el-input v-model="inventoryForm.name" placeholder="请输入名称"></el-input>
        </el-form-item>
        <el-form-item label="类型" prop="type">
          <el-select v-model="inventoryForm.type" placeholder="请选择类型">
            <el-option label="农资" value="农资"></el-option>
            <el-option label="产品" value="产品"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="类别" prop="category">
          <el-input v-model="inventoryForm.category" placeholder="请输入类别"></el-input>
        </el-form-item>
        <el-form-item label="规格" prop="spec">
          <el-input v-model="inventoryForm.spec" placeholder="请输入规格"></el-input>
        </el-form-item>
        <el-form-item label="库存" prop="stock">
          <el-input v-model.number="inventoryForm.stock" placeholder="请输入库存数量"></el-input>
        </el-form-item>
        <el-form-item label="单位" prop="unit">
          <el-input v-model="inventoryForm.unit" placeholder="请输入单位"></el-input>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitInventory">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { Download, Warning, Plus } from '@element-plus/icons-vue'
import { ElMessage, ElMessageBox } from 'element-plus'

const loading = ref(false)
const inventory = ref([])
const dialogVisible = ref(false)
const isEdit = ref(false)
const inventoryFormRef = ref(null)

const filterForm = ref({
  name: '',
  type: '',
  status: ''
})

const inventoryForm = reactive({
  id: '',
  name: '',
  type: '',
  category: '',
  spec: '',
  stock: 0,
  unit: ''
})

const inventoryRules = {
  name: [{ required: true, message: '请输入名称', trigger: 'blur' }],
  type: [{ required: true, message: '请选择类型', trigger: 'blur' }],
  category: [{ required: true, message: '请输入类别', trigger: 'blur' }],
  spec: [{ required: true, message: '请输入规格', trigger: 'blur' }],
  stock: [{ required: true, message: '请输入库存数量', trigger: 'blur' }],
  unit: [{ required: true, message: '请输入单位', trigger: 'blur' }]
}

const fetchInventory = async () => {
  loading.value = true
  try {
    let url = '/api/inventory'
    const params = new URLSearchParams()
    if (filterForm.value.name) params.append('name', filterForm.value.name)
    if (filterForm.value.type) params.append('type', filterForm.value.type)
    if (filterForm.value.status) params.append('status', filterForm.value.status)
    
    if (params.toString()) {
      url += '?' + params.toString()
    }
    
    const response = await fetch(url)
    inventory.value = await response.json()
  } catch (error) {
    ElMessage.error('获取库存列表失败')
  } finally {
    loading.value = false
  }
}

onMounted(() => {
  fetchInventory()
})

const openAddDialog = () => {
  isEdit.value = false
  inventoryForm.id = ''
  inventoryForm.name = ''
  inventoryForm.type = ''
  inventoryForm.category = ''
  inventoryForm.spec = ''
  inventoryForm.stock = 0
  inventoryForm.unit = ''
  dialogVisible.value = true
}

const openEditDialog = (row) => {
  isEdit.value = true
  inventoryForm.id = row.id
  inventoryForm.name = row.name
  inventoryForm.type = row.type
  inventoryForm.category = row.category
  inventoryForm.spec = row.spec
  inventoryForm.stock = row.stock
  inventoryForm.unit = row.unit
  dialogVisible.value = true
}

const submitInventory = async () => {
  inventoryFormRef.value.validate(async (valid) => {
    if (valid) {
      try {
        const method = isEdit.value ? 'PUT' : 'POST'
        const url = isEdit.value ? `/api/inventory/${inventoryForm.id}` : '/api/inventory'
        
        const response = await fetch(url, {
          method: method,
          headers: { 'Content-Type': 'application/json' },
          body: JSON.stringify(inventoryForm)
        })
        
        const data = await response.json()
        
        if (data.success) {
          ElMessage.success(isEdit.value ? '修改成功' : '添加成功')
          dialogVisible.value = false
          fetchInventory()
        } else {
          ElMessage.error(data.message || (isEdit.value ? '修改失败' : '添加失败'))
        }
      } catch (error) {
        ElMessage.error('操作失败')
      }
    }
  })
}

const deleteInventory = async (row) => {
  try {
    await ElMessageBox.confirm('确定要删除该库存吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    
    const response = await fetch(`/api/inventory/${row.id}`, {
      method: 'DELETE'
    })
    
    const data = await response.json()
    
    if (data.success) {
      ElMessage.success('删除成功')
      fetchInventory()
    } else {
      ElMessage.error(data.message || '删除失败')
    }
  } catch (error) {
    ElMessage.info('已取消删除')
  }
}

const exportInventory = () => {
  let csv = '编号,名称,类型,类别,规格,库存,单位,更新时间\n'
  inventory.value.forEach(item => {
    csv += `${item.id},${item.name},${item.type},${item.category},${item.spec},${item.stock},${item.unit},${item.lastUpdate}\n`
  })
  
  const blob = new Blob(['\ufeff' + csv], { type: 'text/csv;charset=utf-8;' })
  const link = document.createElement('a')
  link.href = URL.createObjectURL(blob)
  link.download = '库存列表_' + new Date().toLocaleDateString() + '.csv'
  link.click()
  ElMessage.success('导出成功')
}

const inventoryAlert = () => {
  const lowStock = inventory.value.filter(item => item.stock < 50)
  if (lowStock.length > 0) {
    let message = '以下库存不足:\n'
    lowStock.forEach(item => {
      message += `${item.name}: ${item.stock}${item.unit}\n`
    })
    ElMessage.warning(message)
  } else {
    ElMessage.success('库存充足')
  }
}

const searchInventory = () => {
  fetchInventory()
}

const resetFilter = () => {
  filterForm.value = { name: '', type: '', status: '' }
  fetchInventory()
}
</script>

<style scoped>
.materials-inventory {
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

.type-select, .status-select {
  width: 120px;
}

.type-select :deep(.el-select__wrapper),
.status-select :deep(.el-select__wrapper) {
  border-radius: 6px;
  border: 1px solid #dcdfe6;
  box-shadow: none;
  background: #fff;
}

.type-select :deep(.el-select__wrapper:hover),
.status-select :deep(.el-select__wrapper:hover) {
  border-color: #c0c4cc;
}

.type-select :deep(.el-select__wrapper.is-focus),
.status-select :deep(.el-select__wrapper.is-focus) {
  border-color: #409eff;
  box-shadow: 0 0 0 2px rgba(64, 158, 255, 0.1);
}

.type-select :deep(.el-select__trigger),
.status-select :deep(.el-select__trigger) {
  padding: 0 25px 0 15px;
}

.type-select :deep(.el-select__caret),
.status-select :deep(.el-select__caret) {
  color: #909399;
}

.inventory-card {
  flex: 1;
  min-height: 0;
  display: flex;
  flex-direction: column;
}

.inventory-card :deep(.el-card__body) {
  flex: 1;
  overflow: auto;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.text-danger {
  color: #f56c6c;
  font-weight: bold;
}

.text-warning {
  color: #e6a23c;
}

.text-success {
  color: #67c23a;
}
</style>