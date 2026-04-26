<template>
  <div class="products">
    <!-- 操作按钮 -->
    <div class="action-bar">
      <el-button type="primary" @click="addProduct">
        <el-icon><plus /></el-icon>
        新增产品
      </el-button>
      <el-button type="success" @click="addSale">
        <el-icon><shopping-cart /></el-icon>
        产品出售
      </el-button>
    </div>

    <!-- 搜索和筛选 -->
    <el-card shadow="hover" class="filter-card">
      <div class="filter-content">
        <el-form :inline="true" :model="filterForm" class="filter-form">
          <el-form-item label="产品名称">
            <el-input v-model="filterForm.name" placeholder="输入产品名称" style="width: 200px"></el-input>
          </el-form-item>
          <el-form-item label="类型">
            <el-select v-model="filterForm.type" placeholder="选择类型">
              <el-option label="蔬菜" value="vegetable"></el-option>
              <el-option label="水果" value="fruit"></el-option>
              <el-option label="谷物" value="grain"></el-option>
              <el-option label="其他" value="other"></el-option>
            </el-select>
          </el-form-item>
          <el-form-item>
            <el-button type="primary" @click="searchProduct">查询</el-button>
            <el-button @click="resetFilter">重置</el-button>
          </el-form-item>
        </el-form>
      </div>
    </el-card>

    <!-- 产品库存 -->
    <el-card shadow="hover" class="products-card">
      <template #header>
        <div class="card-header">
          <span>产品库存</span>
        </div>
      </template>
      <el-table :data="products" style="width: 100%">
        <el-table-column prop="id" label="编号" width="100"></el-table-column>
        <el-table-column prop="name" label="名称"></el-table-column>
        <el-table-column prop="type" label="类型" width="120"></el-table-column>
        <el-table-column prop="spec" label="规格" width="150"></el-table-column>
        <el-table-column prop="stock" label="库存" width="100"></el-table-column>
        <el-table-column prop="unit" label="单位" width="80"></el-table-column>
        <el-table-column prop="price" label="单价" width="100"></el-table-column>
        <el-table-column label="操作" width="150">
          <template #default="scope">
            <el-button type="primary" size="small" @click="editProduct(scope.row)">
              编辑
            </el-button>
            <el-button type="danger" size="small" @click="deleteProduct(scope.row.id)">
              删除
            </el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <!-- 销售记录 -->
    <el-card shadow="hover" class="sales-card">
      <template #header>
        <div class="card-header">
          <span>销售记录</span>
        </div>
      </template>
      <el-table :data="sales" style="width: 100%">
        <el-table-column prop="id" label="销售编号" width="150"></el-table-column>
        <el-table-column prop="productName" label="产品名称"></el-table-column>
        <el-table-column prop="quantity" label="数量" width="100"></el-table-column>
        <el-table-column prop="unitPrice" label="单价" width="100"></el-table-column>
        <el-table-column prop="totalPrice" label="总价" width="120"></el-table-column>
        <el-table-column prop="customer" label="客户" width="150"></el-table-column>
        <el-table-column prop="saleDate" label="销售日期" width="180"></el-table-column>
        <el-table-column label="操作" width="120">
          <template #default="scope">
            <el-button type="primary" size="small" @click="viewSale(scope.row)">
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

const products = ref([
  {
    id: 1,
    name: '西红柿',
    type: '蔬菜',
    spec: '红熟',
    stock: 500,
    unit: 'kg',
    price: 3.5
  },
  {
    id: 2,
    name: '黄瓜',
    type: '蔬菜',
    spec: '新鲜',
    stock: 300,
    unit: 'kg',
    price: 2.8
  },
  {
    id: 3,
    name: '茄子',
    type: '蔬菜',
    spec: '紫皮',
    stock: 200,
    unit: 'kg',
    price: 2.2
  },
  {
    id: 4,
    name: '玉米',
    type: '谷物',
    spec: '新鲜',
    stock: 1000,
    unit: 'kg',
    price: 1.8
  },
  {
    id: 5,
    name: '小麦',
    type: '谷物',
    spec: '优质',
    stock: 2000,
    unit: 'kg',
    price: 1.5
  }
])

const sales = ref([
  {
    id: 'S20260425001',
    productName: '西红柿',
    quantity: 100,
    unitPrice: 3.5,
    totalPrice: 350,
    customer: '超市A',
    saleDate: '2026-04-25'
  },
  {
    id: 'S20260424002',
    productName: '黄瓜',
    quantity: 150,
    unitPrice: 2.8,
    totalPrice: 420,
    customer: '超市B',
    saleDate: '2026-04-24'
  },
  {
    id: 'S20260423003',
    productName: '玉米',
    quantity: 500,
    unitPrice: 1.8,
    totalPrice: 900,
    customer: '饲料厂',
    saleDate: '2026-04-23'
  }
])

const addProduct = () => {
  // 新增产品
  console.log('新增产品')
}

const addSale = () => {
  // 产品出售
  console.log('产品出售')
}

const searchProduct = () => {
  // 查询产品
  console.log('查询产品:', filterForm.value)
}

const resetFilter = () => {
  // 重置筛选
  filterForm.value = {
    name: '',
    type: ''
  }
}

const editProduct = (row) => {
  // 编辑产品
  console.log('编辑产品:', row)
}

const deleteProduct = (id) => {
  // 删除产品
  console.log('删除产品:', id)
}

const viewSale = (row) => {
  // 查看销售记录
  console.log('查看销售记录:', row)
}
</script>

<style scoped>
.products {
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

.products-card {
  flex: 1;
  min-height: 0;
  display: flex;
  flex-direction: column;
}

.products-card :deep(.el-card__body) {
  flex: 1;
  overflow: auto;
}

.sales-card {
  flex: 1;
  min-height: 0;
  display: flex;
  flex-direction: column;
}

.sales-card :deep(.el-card__body) {
  flex: 1;
  overflow: auto;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
</style>
