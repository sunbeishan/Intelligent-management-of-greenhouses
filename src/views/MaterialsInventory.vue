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
              <el-option label="农资" value="material"></el-option>
              <el-option label="产品" value="product"></el-option>
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
      <el-table :data="inventory" style="width: 100%">
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
import { ref } from 'vue'
import { Download, Warning } from '@element-plus/icons-vue'

const filterForm = ref({
  name: '',
  type: '',
  status: ''
})

const inventory = ref([
  {
    id: 1,
    name: '尿素',
    type: '农资',
    category: '化肥',
    spec: '46%含量',
    stock: 500,
    unit: 'kg',
    lastUpdate: '2026-04-25 10:00'
  },
  {
    id: 2,
    name: '复合肥',
    type: '农资',
    category: '化肥',
    spec: 'NPK 15-15-15',
    stock: 300,
    unit: 'kg',
    lastUpdate: '2026-04-25 10:00'
  },
  {
    id: 3,
    name: '杀虫剂',
    type: '农资',
    category: '农药',
    spec: '100ml/瓶',
    stock: 45,
    unit: '瓶',
    lastUpdate: '2026-04-25 10:00'
  },
  {
    id: 4,
    name: '西红柿',
    type: '产品',
    category: '蔬菜',
    spec: '新鲜',
    stock: 1200,
    unit: 'kg',
    lastUpdate: '2026-04-25 08:00'
  },
  {
    id: 5,
    name: '黄瓜',
    type: '产品',
    category: '蔬菜',
    spec: '新鲜',
    stock: 800,
    unit: 'kg',
    lastUpdate: '2026-04-25 08:00'
  }
])

const exportInventory = () => {
  // 导出库存
  console.log('导出库存')
}

const inventoryAlert = () => {
  // 库存预警
  console.log('库存预警')
}

const searchInventory = () => {
  // 查询库存
  console.log('查询库存:', filterForm.value)
}

const resetFilter = () => {
  // 重置筛选
  filterForm.value = {
    name: '',
    type: '',
    status: ''
  }
}

const viewDetail = (row) => {
  // 查看详情
  console.log('查看详情:', row)
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
  font-weight: bold;
}

.text-success {
  color: #67c23a;
  font-weight: bold;
}
</style>
