<template>
  <div class="materials-inventory">
    <!-- 操作按钮 -->
    <div class="action-bar">
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
            <el-select v-model="filterForm.type" placeholder="选择类型">
              <el-option label="农资" value="农资"></el-option>
              <el-option label="产品" value="产品"></el-option>
            </el-select>
          </el-form-item>
          <el-form-item label="库存状态">
            <el-select v-model="filterForm.status" placeholder="选择状态">
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
        <el-table-column label="操作" width="120">
          <template #default="scope">
            <el-button type="primary" size="small" @click="viewDetail(scope.row)">
              详情
            </el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { Download, Warning } from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'

const loading = ref(false)
const inventory = ref([])

const filterForm = ref({
  name: '',
  type: '',
  status: ''
})

// API 调用
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

const exportInventory = () => {
  // 导出CSV
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

const viewDetail = (row) => {
  ElMessage.info(`${row.name} - 当前库存: ${row.stock}${row.unit}`)
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
