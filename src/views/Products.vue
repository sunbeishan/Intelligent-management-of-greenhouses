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
              <el-option label="蔬菜" value="蔬菜"></el-option>
              <el-option label="水果" value="水果"></el-option>
              <el-option label="谷物" value="谷物"></el-option>
              <el-option label="其他" value="其他"></el-option>
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
      <el-table :data="products" style="width: 100%" v-loading="loading">
        <el-table-column prop="id" label="编号" width="100"></el-table-column>
        <el-table-column prop="name" label="名称"></el-table-column>
        <el-table-column prop="type" label="类型" width="120"></el-table-column>
        <el-table-column prop="spec" label="规格" width="150"></el-table-column>
        <el-table-column prop="stock" label="库存" width="100"></el-table-column>
        <el-table-column prop="unit" label="单位" width="80"></el-table-column>
        <el-table-column prop="price" label="单价" width="100">
          <template #default="scope">
            ¥{{ scope.row.price }}
          </template>
        </el-table-column>
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
      <el-table :data="sales" style="width: 100%" v-loading="saleLoading">
        <el-table-column prop="id" label="销售编号" width="150"></el-table-column>
        <el-table-column prop="productName" label="产品名称"></el-table-column>
        <el-table-column prop="quantity" label="数量" width="100"></el-table-column>
        <el-table-column prop="unitPrice" label="单价" width="100">
          <template #default="scope">
            ¥{{ scope.row.unitPrice }}
          </template>
        </el-table-column>
        <el-table-column prop="totalPrice" label="总价" width="120">
          <template #default="scope">
            ¥{{ scope.row.totalPrice }}
          </template>
        </el-table-column>
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

    <!-- 新增/编辑产品对话框 -->
    <el-dialog v-model="productDialogVisible" :title="isEdit ? '编辑产品' : '新增产品'" width="500px">
      <el-form :model="productForm" :rules="productRules" ref="productFormRef" label-width="80px">
        <el-form-item label="名称" prop="name">
          <el-input v-model="productForm.name"></el-input>
        </el-form-item>
        <el-form-item label="类型" prop="type">
          <el-select v-model="productForm.type" style="width: 100%">
            <el-option label="蔬菜" value="蔬菜"></el-option>
            <el-option label="水果" value="水果"></el-option>
            <el-option label="谷物" value="谷物"></el-option>
            <el-option label="其他" value="其他"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="规格" prop="spec">
          <el-input v-model="productForm.spec"></el-input>
        </el-form-item>
        <el-form-item label="库存" prop="stock">
          <el-input-number v-model="productForm.stock" :min="0" style="width: 100%"></el-input-number>
        </el-form-item>
        <el-form-item label="单位" prop="unit">
          <el-input v-model="productForm.unit"></el-input>
        </el-form-item>
        <el-form-item label="单价" prop="price">
          <el-input-number v-model="productForm.price" :min="0" :precision="2" style="width: 100%"></el-input-number>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="productDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitProduct">确定</el-button>
      </template>
    </el-dialog>

    <!-- 销售对话框 -->
    <el-dialog v-model="saleDialogVisible" title="产品出售" width="500px">
      <el-form :model="saleForm" :rules="saleRules" ref="saleFormRef" label-width="100px">
        <el-form-item label="产品名称" prop="productName">
          <el-select v-model="saleForm.productName" style="width: 100%">
            <el-option v-for="item in products" :key="item.id" :label="item.name" :value="item.name"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="数量" prop="quantity">
          <el-input-number v-model="saleForm.quantity" :min="1" style="width: 100%"></el-input-number>
        </el-form-item>
        <el-form-item label="单价" prop="unitPrice">
          <el-input-number v-model="saleForm.unitPrice" :min="0" :precision="2" style="width: 100%"></el-input-number>
        </el-form-item>
        <el-form-item label="总价">
          <el-input :model-value="saleForm.quantity * saleForm.unitPrice" disabled></el-input>
        </el-form-item>
        <el-form-item label="客户" prop="customer">
          <el-input v-model="saleForm.customer"></el-input>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="saleDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitSale">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { Plus, ShoppingCart } from '@element-plus/icons-vue'
import { ElMessage, ElMessageBox } from 'element-plus'

const loading = ref(false)
const saleLoading = ref(false)
const products = ref([])
const sales = ref([])

const filterForm = ref({
  name: '',
  type: ''
})

// 对话框相关
const productDialogVisible = ref(false)
const saleDialogVisible = ref(false)
const isEdit = ref(false)
const productFormRef = ref()
const saleFormRef = ref()

const productForm = reactive({
  id: null,
  name: '',
  type: '',
  spec: '',
  stock: 0,
  unit: '',
  price: 0
})

const saleForm = reactive({
  productName: '',
  quantity: 1,
  unitPrice: 0,
  totalPrice: 0,
  customer: ''
})

const productRules = {
  name: [{ required: true, message: '请输入名称', trigger: 'blur' }],
  type: [{ required: true, message: '请选择类型', trigger: 'change' }],
  stock: [{ required: true, message: '请输入库存', trigger: 'blur' }],
  unit: [{ required: true, message: '请输入单位', trigger: 'blur' }],
  price: [{ required: true, message: '请输入单价', trigger: 'blur' }]
}

const saleRules = {
  productName: [{ required: true, message: '请选择产品', trigger: 'change' }],
  quantity: [{ required: true, message: '请输入数量', trigger: 'blur' }],
  unitPrice: [{ required: true, message: '请输入单价', trigger: 'blur' }],
  customer: [{ required: true, message: '请输入客户', trigger: 'blur' }]
}

// API 调用
const fetchProducts = async () => {
  loading.value = true
  try {
    const response = await fetch('/api/products')
    products.value = await response.json()
  } catch (error) {
    ElMessage.error('获取产品列表失败')
  } finally {
    loading.value = false
  }
}

const fetchSales = async () => {
  saleLoading.value = true
  try {
    const response = await fetch('/api/sales')
    sales.value = await response.json()
  } catch (error) {
    ElMessage.error('获取销售记录失败')
  } finally {
    saleLoading.value = false
  }
}

onMounted(() => {
  fetchProducts()
  fetchSales()
})

const addProduct = () => {
  isEdit.value = false
  Object.assign(productForm, {
    id: null,
    name: '',
    type: '',
    spec: '',
    stock: 0,
    unit: '',
    price: 0
  })
  productDialogVisible.value = true
}

const editProduct = (row) => {
  isEdit.value = true
  Object.assign(productForm, row)
  productDialogVisible.value = true
}

const submitProduct = async () => {
  try {
    await productFormRef.value.validate()
    const url = isEdit.value ? `/api/products/${productForm.id}` : '/api/products'
    const method = isEdit.value ? 'PUT' : 'POST'
    
    const response = await fetch(url, {
      method,
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(productForm)
    })
    
    if (response.ok) {
      ElMessage.success(isEdit.value ? '编辑成功' : '新增成功')
      productDialogVisible.value = false
      fetchProducts()
    } else {
      ElMessage.error('操作失败')
    }
  } catch (error) {
    console.error(error)
  }
}

const deleteProduct = async (id) => {
  try {
    await ElMessageBox.confirm('确定要删除该产品吗？', '提示', { type: 'warning' })
    const response = await fetch(`/api/products/${id}`, { method: 'DELETE' })
    if (response.ok) {
      ElMessage.success('删除成功')
      fetchProducts()
    } else {
      ElMessage.error('删除失败')
    }
  } catch (error) {
    // 取消删除
  }
}

const addSale = () => {
  Object.assign(saleForm, {
    productName: '',
    quantity: 1,
    unitPrice: 0,
    customer: ''
  })
  saleDialogVisible.value = true
}

const submitSale = async () => {
  try {
    await saleFormRef.value.validate()
    saleForm.totalPrice = saleForm.quantity * saleForm.unitPrice
    
    const response = await fetch('/api/sales', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(saleForm)
    })
    
    if (response.ok) {
      ElMessage.success('销售成功')
      saleDialogVisible.value = false
      fetchProducts()
      fetchSales()
    } else {
      ElMessage.error('销售失败')
    }
  } catch (error) {
    console.error(error)
  }
}

const searchProduct = () => {
  if (!filterForm.value.name && !filterForm.value.type) {
    fetchProducts()
    return
  }
  products.value = products.value.filter(p => {
    const nameMatch = !filterForm.value.name || p.name.includes(filterForm.value.name)
    const typeMatch = !filterForm.value.type || p.type === filterForm.value.type
    return nameMatch && typeMatch
  })
}

const resetFilter = () => {
  filterForm.value = { name: '', type: '' }
  fetchProducts()
}

const viewSale = (row) => {
  ElMessage.info(`销售编号: ${row.id}, 产品: ${row.productName}, 数量: ${row.quantity}`)
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
