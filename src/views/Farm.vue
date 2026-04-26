<template>
  <div class="farm">
    <!-- 操作按钮 -->
    <div class="action-bar">
      <el-button type="primary" @click="addFarm">
        <el-icon><plus /></el-icon>
        新增农田
      </el-button>
      <el-button type="success" @click="importData">
        <el-icon><upload /></el-icon>
        导入数据
      </el-button>
      <el-button @click="exportData">
        <el-icon><download /></el-icon>
        导出数据
      </el-button>
    </div>

    <!-- 搜索和筛选 -->
    <el-card shadow="hover" class="filter-card">
      <div class="filter-content">
        <el-form :inline="true" :model="filterForm" class="filter-form">
          <el-form-item label="农田名称">
            <el-input v-model="filterForm.name" placeholder="输入农田名称" style="width: 200px"></el-input>
          </el-form-item>
          <el-form-item label="区域">
            <el-select v-model="filterForm.area" placeholder="选择区域">
              <el-option label="东区" value="east"></el-option>
              <el-option label="西区" value="west"></el-option>
              <el-option label="南区" value="south"></el-option>
              <el-option label="北区" value="north"></el-option>
            </el-select>
          </el-form-item>
          <el-form-item>
            <el-button type="primary" @click="searchFarm">查询</el-button>
            <el-button @click="resetFilter">重置</el-button>
          </el-form-item>
        </el-form>
      </div>
    </el-card>

    <!-- 农田列表 -->
    <el-card shadow="hover" class="farm-list-card">
      <template #header>
        <div class="card-header">
          <span>农田信息列表</span>
          <span class="total-count">共 {{ farmList.length }} 条记录</span>
        </div>
      </template>
      <el-table :data="farmList" style="width: 100%">
        <el-table-column type="index" label="序号" width="80"></el-table-column>
        <el-table-column prop="name" label="农田名称"></el-table-column>
        <el-table-column prop="area" label="区域" width="120"></el-table-column>
        <el-table-column prop="areaSize" label="面积(亩)" width="120"></el-table-column>
        <el-table-column prop="soilType" label="土壤类型" width="150"></el-table-column>
        <el-table-column prop="crop" label="种植作物" width="150"></el-table-column>
        <el-table-column prop="status" label="状态" width="100">
          <template #default="scope">
            <el-tag :type="getFarmStatusTag(scope.row.status)">{{ scope.row.status }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="200">
          <template #default="scope">
            <el-button type="primary" size="small" @click="editFarm(scope.row)">
              编辑
            </el-button>
            <el-button type="danger" size="small" @click="deleteFarm(scope.row.id)">
              删除
            </el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <!-- 分页 -->
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
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { Plus, Upload, Download } from '@element-plus/icons-vue'

const filterForm = ref({
  name: '',
  area: ''
})

const farmList = ref([
  {
    id: 1,
    name: '大棚A',
    area: '东区',
    areaSize: 50,
    soilType: '沙壤土',
    crop: '西红柿',
    status: '种植中'
  },
  {
    id: 2,
    name: '大棚B',
    area: '东区',
    areaSize: 45,
    soilType: '壤土',
    crop: '黄瓜',
    status: '种植中'
  },
  {
    id: 3,
    name: '大棚C',
    area: '西区',
    areaSize: 60,
    soilType: '黏土',
    crop: '茄子',
    status: '种植中'
  },
  {
    id: 4,
    name: '露天农田1',
    area: '南区',
    areaSize: 120,
    soilType: '沙壤土',
    crop: '玉米',
    status: '种植中'
  },
  {
    id: 5,
    name: '露天农田2',
    area: '北区',
    areaSize: 100,
    soilType: '壤土',
    crop: '小麦',
    status: '休耕'
  }
])

const currentPage = ref(1)
const pageSize = ref(10)
const total = ref(farmList.value.length)

const addFarm = () => {
  // 新增农田
  console.log('新增农田')
}

const importData = () => {
  // 导入数据
  console.log('导入数据')
}

const exportData = () => {
  // 导出数据
  console.log('导出数据')
}

const searchFarm = () => {
  // 查询农田
  console.log('查询农田:', filterForm.value)
}

const resetFilter = () => {
  // 重置筛选
  filterForm.value = {
    name: '',
    area: ''
  }
}

const editFarm = (row) => {
  // 编辑农田
  console.log('编辑农田:', row)
}

const deleteFarm = (id) => {
  // 删除农田
  console.log('删除农田:', id)
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
