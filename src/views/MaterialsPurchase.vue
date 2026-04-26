<template>
  <div class="materials-purchase">
    <!-- 操作按钮 -->
    <div class="action-bar">
      <el-button type="primary" @click="addMaterial">
        <el-icon><plus /></el-icon>
        新增农资
      </el-button>
      <el-button type="success" @click="addPurchase">
        <el-icon><shopping-cart /></el-icon>
        采购农资
      </el-button>
    </div>

    <!-- 搜索和筛选 -->
    <el-card shadow="hover" class="filter-card">
      <div class="filter-content">
        <el-form :inline="true" :model="filterForm" class="filter-form">
          <el-form-item label="农资名称">
            <el-input v-model="filterForm.name" placeholder="输入农资名称" style="width: 200px"></el-input>
          </el-form-item>
          <el-form-item label="类型">
            <el-select v-model="filterForm.type" placeholder="选择类型">
              <el-option label="化肥" value="fertilizer"></el-option>
              <el-option label="农药" value="pesticide"></el-option>
              <el-option label="种子" value="seed"></el-option>
              <el-option label="农具" value="tool"></el-option>
            </el-select>
          </el-form-item>
          <el-form-item>
            <el-button type="primary" @click="searchMaterial">查询</el-button>
            <el-button @click="resetFilter">重置</el-button>
          </el-form-item>
        </el-form>
      </div>
    </el-card>

    <!-- 农资库存 -->
    <el-card shadow="hover" class="materials-card">
      <template #header>
        <div class="card-header">
          <span>农资库存</span>
        </div>
      </template>
      <el-table :data="materials" style="width: 100%">
        <el-table-column prop="id" label="编号" width="100"></el-table-column>
        <el-table-column prop="name" label="名称"></el-table-column>
        <el-table-column prop="type" label="类型" width="120"></el-table-column>
        <el-table-column prop="spec" label="规格" width="150"></el-table-column>
        <el-table-column prop="stock" label="库存" width="100"></el-table-column>
        <el-table-column prop="unit" label="单位" width="80"></el-table-column>
        <el-table-column prop="supplier" label="供应商" width="150"></el-table-column>
        <el-table-column label="操作" width="150">
          <template #default="scope">
            <el-button type="primary" size="small" @click="editMaterial(scope.row)">
              编辑
            </el-button>
            <el-button type="danger" size="small" @click="deleteMaterial(scope.row.id)">
              删除
            </el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <!-- 采购记录 -->
    <el-card shadow="hover" class="purchase-card">
      <template #header>
        <div class="card-header">
          <span>采购记录</span>
        </div>
      </template>
      <el-table :data="purchases" style="width: 100%">
        <el-table-column prop="id" label="采购编号" width="150"></el-table-column>
        <el-table-column prop="materialName" label="农资名称"></el-table-column>
        <el-table-column prop="quantity" label="数量" width="100"></el-table-column>
        <el-table-column prop="unitPrice" label="单价" width="100"></el-table-column>
        <el-table-column prop="totalPrice" label="总价" width="120"></el-table-column>
        <el-table-column prop="supplier" label="供应商" width="150"></el-table-column>
        <el-table-column prop="purchaseDate" label="采购日期" width="180"></el-table-column>
        <el-table-column label="操作" width="120">
          <template #default="scope">
            <el-button type="primary" size="small" @click="viewPurchase(scope.row)">
              查看
            </el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { Plus, ShoppingCart } from '@element-plus/icons-vue'

const filterForm = ref({
  name: '',
  type: ''
})

const materials = ref([
  {
    id: 1,
    name: '尿素',
    type: '化肥',
    spec: '46%含量',
    stock: 500,
    unit: 'kg',
    supplier: '农业物资公司'
  },
  {
    id: 2,
    name: '复合肥',
    type: '化肥',
    spec: 'NPK 15-15-15',
    stock: 300,
    unit: 'kg',
    supplier: '农业物资公司'
  },
  {
    id: 3,
    name: '杀虫剂',
    type: '农药',
    spec: '100ml/瓶',
    stock: 100,
    unit: '瓶',
    supplier: '植保公司'
  },
  {
    id: 4,
    name: '西红柿种子',
    type: '种子',
    spec: '杂交种',
    stock: 50,
    unit: '袋',
    supplier: '种业公司'
  },
  {
    id: 5,
    name: '锄头',
    type: '农具',
    spec: '铁制',
    stock: 20,
    unit: '把',
    supplier: '农具厂'
  }
])

const purchases = ref([
  {
    id: 'P20260425001',
    materialName: '尿素',
    quantity: 200,
    unitPrice: 2.5,
    totalPrice: 500,
    supplier: '农业物资公司',
    purchaseDate: '2026-04-25'
  },
  {
    id: 'P20260420002',
    materialName: '复合肥',
    quantity: 150,
    unitPrice: 3.0,
    totalPrice: 450,
    supplier: '农业物资公司',
    purchaseDate: '2026-04-20'
  },
  {
    id: 'P20260415003',
    materialName: '杀虫剂',
    quantity: 50,
    unitPrice: 15.0,
    totalPrice: 750,
    supplier: '植保公司',
    purchaseDate: '2026-04-15'
  }
])

const addMaterial = () => {
  // 新增农资
  console.log('新增农资')
}

const addPurchase = () => {
  // 采购农资
  console.log('采购农资')
}

const searchMaterial = () => {
  // 查询农资
  console.log('查询农资:', filterForm.value)
}

const resetFilter = () => {
  // 重置筛选
  filterForm.value = {
    name: '',
    type: ''
  }
}

const editMaterial = (row) => {
  // 编辑农资
  console.log('编辑农资:', row)
}

const deleteMaterial = (id) => {
  // 删除农资
  console.log('删除农资:', id)
}

const viewPurchase = (row) => {
  // 查看采购记录
  console.log('查看采购记录:', row)
}
</script>

<style scoped>
.materials-purchase {
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

.materials-card {
  flex: 1;
  min-height: 0;
  display: flex;
  flex-direction: column;
}

.materials-card :deep(.el-card__body) {
  flex: 1;
  overflow: auto;
}

.purchase-card {
  flex: 1;
  min-height: 0;
  display: flex;
  flex-direction: column;
}

.purchase-card :deep(.el-card__body) {
  flex: 1;
  overflow: auto;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
</style>
