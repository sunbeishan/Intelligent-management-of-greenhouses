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
              <el-option label="化肥" value="化肥"></el-option>
              <el-option label="农药" value="农药"></el-option>
              <el-option label="种子" value="种子"></el-option>
              <el-option label="农具" value="农具"></el-option>
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
      <el-table :data="materials" style="width: 100%" v-loading="loading">
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
      <el-table :data="purchases" style="width: 100%" v-loading="purchaseLoading">
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

    <!-- 新增/编辑农资对话框 -->
    <el-dialog v-model="materialDialogVisible" :title="isEdit ? '编辑农资' : '新增农资'" width="500px">
      <el-form :model="materialForm" :rules="materialRules" ref="materialFormRef" label-width="80px">
        <el-form-item label="名称" prop="name">
          <el-input v-model="materialForm.name"></el-input>
        </el-form-item>
        <el-form-item label="类型" prop="type">
          <el-select v-model="materialForm.type" style="width: 100%">
            <el-option label="化肥" value="化肥"></el-option>
            <el-option label="农药" value="农药"></el-option>
            <el-option label="种子" value="种子"></el-option>
            <el-option label="农具" value="农具"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="规格" prop="spec">
          <el-input v-model="materialForm.spec"></el-input>
        </el-form-item>
        <el-form-item label="库存" prop="stock">
          <el-input-number v-model="materialForm.stock" :min="0" style="width: 100%"></el-input-number>
        </el-form-item>
        <el-form-item label="单位" prop="unit">
          <el-input v-model="materialForm.unit"></el-input>
        </el-form-item>
        <el-form-item label="供应商" prop="supplier">
          <el-input v-model="materialForm.supplier"></el-input>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="materialDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitMaterial">确定</el-button>
      </template>
    </el-dialog>

    <!-- 采购对话框 -->
    <el-dialog v-model="purchaseDialogVisible" title="采购农资" width="500px">
      <el-form :model="purchaseForm" :rules="purchaseRules" ref="purchaseFormRef" label-width="100px">
        <el-form-item label="农资名称" prop="materialName">
          <el-select v-model="purchaseForm.materialName" style="width: 100%">
            <el-option v-for="item in materials" :key="item.id" :label="item.name" :value="item.name"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="数量" prop="quantity">
          <el-input-number v-model="purchaseForm.quantity" :min="1" style="width: 100%"></el-input-number>
        </el-form-item>
        <el-form-item label="单价" prop="unitPrice">
          <el-input-number v-model="purchaseForm.unitPrice" :min="0" :precision="2" style="width: 100%"></el-input-number>
        </el-form-item>
        <el-form-item label="总价">
          <el-input :model-value="purchaseForm.quantity * purchaseForm.unitPrice" disabled></el-input>
        </el-form-item>
        <el-form-item label="供应商" prop="supplier">
          <el-input v-model="purchaseForm.supplier"></el-input>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="purchaseDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitPurchase">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { Plus, ShoppingCart } from '@element-plus/icons-vue'
import { ElMessage, ElMessageBox } from 'element-plus'

const loading = ref(false)
const purchaseLoading = ref(false)
const materials = ref([])
const purchases = ref([])

const filterForm = ref({
  name: '',
  type: ''
})

// 对话框相关
const materialDialogVisible = ref(false)
const purchaseDialogVisible = ref(false)
const isEdit = ref(false)
const materialFormRef = ref()
const purchaseFormRef = ref()

const materialForm = reactive({
  id: null,
  name: '',
  type: '',
  spec: '',
  stock: 0,
  unit: '',
  supplier: ''
})

const purchaseForm = reactive({
  materialName: '',
  quantity: 1,
  unitPrice: 0,
  totalPrice: 0,
  supplier: ''
})

const materialRules = {
  name: [{ required: true, message: '请输入名称', trigger: 'blur' }],
  type: [{ required: true, message: '请选择类型', trigger: 'change' }],
  stock: [{ required: true, message: '请输入库存', trigger: 'blur' }],
  unit: [{ required: true, message: '请输入单位', trigger: 'blur' }]
}

const purchaseRules = {
  materialName: [{ required: true, message: '请选择农资', trigger: 'change' }],
  quantity: [{ required: true, message: '请输入数量', trigger: 'blur' }],
  unitPrice: [{ required: true, message: '请输入单价', trigger: 'blur' }],
  supplier: [{ required: true, message: '请输入供应商', trigger: 'blur' }]
}

// API 调用
const fetchMaterials = async () => {
  loading.value = true
  try {
    const response = await fetch('/api/materials')
    materials.value = await response.json()
  } catch (error) {
    ElMessage.error('获取农资列表失败')
  } finally {
    loading.value = false
  }
}

const fetchPurchases = async () => {
  purchaseLoading.value = true
  try {
    const response = await fetch('/api/purchases')
    purchases.value = await response.json()
  } catch (error) {
    ElMessage.error('获取采购记录失败')
  } finally {
    purchaseLoading.value = false
  }
}

onMounted(() => {
  fetchMaterials()
  fetchPurchases()
})

const addMaterial = () => {
  isEdit.value = false
  Object.assign(materialForm, {
    id: null,
    name: '',
    type: '',
    spec: '',
    stock: 0,
    unit: '',
    supplier: ''
  })
  materialDialogVisible.value = true
}

const editMaterial = (row) => {
  isEdit.value = true
  Object.assign(materialForm, row)
  materialDialogVisible.value = true
}

const submitMaterial = async () => {
  try {
    await materialFormRef.value.validate()
    const url = isEdit.value ? `/api/materials/${materialForm.id}` : '/api/materials'
    const method = isEdit.value ? 'PUT' : 'POST'
    
    const response = await fetch(url, {
      method,
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(materialForm)
    })
    
    if (response.ok) {
      ElMessage.success(isEdit.value ? '编辑成功' : '新增成功')
      materialDialogVisible.value = false
      fetchMaterials()
    } else {
      ElMessage.error('操作失败')
    }
  } catch (error) {
    console.error(error)
  }
}

const deleteMaterial = async (id) => {
  try {
    await ElMessageBox.confirm('确定要删除该农资吗？', '提示', { type: 'warning' })
    const response = await fetch(`/api/materials/${id}`, { method: 'DELETE' })
    if (response.ok) {
      ElMessage.success('删除成功')
      fetchMaterials()
    } else {
      ElMessage.error('删除失败')
    }
  } catch (error) {
    // 取消删除
  }
}

const addPurchase = () => {
  Object.assign(purchaseForm, {
    materialName: '',
    quantity: 1,
    unitPrice: 0,
    supplier: ''
  })
  purchaseDialogVisible.value = true
}

const submitPurchase = async () => {
  try {
    await purchaseFormRef.value.validate()
    purchaseForm.totalPrice = purchaseForm.quantity * purchaseForm.unitPrice
    
    const response = await fetch('/api/purchases', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(purchaseForm)
    })
    
    if (response.ok) {
      ElMessage.success('采购成功')
      purchaseDialogVisible.value = false
      fetchMaterials()
      fetchPurchases()
    } else {
      ElMessage.error('采购失败')
    }
  } catch (error) {
    console.error(error)
  }
}

const searchMaterial = () => {
  // 前端筛选
  if (!filterForm.value.name && !filterForm.value.type) {
    fetchMaterials()
    return
  }
  materials.value = materials.value.filter(m => {
    const nameMatch = !filterForm.value.name || m.name.includes(filterForm.value.name)
    const typeMatch = !filterForm.value.type || m.type === filterForm.value.type
    return nameMatch && typeMatch
  })
}

const resetFilter = () => {
  filterForm.value = { name: '', type: '' }
  fetchMaterials()
}

const viewPurchase = (row) => {
  ElMessage.info(`采购编号: ${row.id}, 农资: ${row.materialName}, 数量: ${row.quantity}`)
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
