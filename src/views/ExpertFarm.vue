<template>
  <div class="expert-farm">
    <div class="app-container">
      <!-- 侧边栏 -->
      <aside class="sidebar">
        <div class="sidebar-header">
          <h1 class="logo">专家控制台</h1>
        </div>
        <nav class="sidebar-menu">
          <router-link to="/expert/dashboard" class="menu-item">
            <el-icon><chat-dot-round /></el-icon>
            <span>回复模块</span>
          </router-link>
          <router-link to="/expert/environment" class="menu-item">
            <el-icon><monitor /></el-icon>
            <span>环境监测</span>
          </router-link>
          <router-link to="/expert/farm" class="menu-item">
            <el-icon><location /></el-icon>
            <span>农田信息管理</span>
          </router-link>
        </nav>
      </aside>
      
      <!-- 主内容区 -->
      <main class="main-content">
        <!-- 顶部导航 -->
        <header class="expert-header">
          <div class="header-left">
            <h2>农田信息管理</h2>
          </div>
          <div class="header-right">
            <el-dropdown>
              <span class="user-info">
                <el-avatar :size="32" :src="expertAvatar"></el-avatar>
                <span>{{ expertName }}</span>
              </span>
              <template #dropdown>
                <el-dropdown-menu>
                  <el-dropdown-item>个人中心</el-dropdown-item>
                  <el-dropdown-item @click="logout">退出登录</el-dropdown-item>
                </el-dropdown-menu>
              </template>
            </el-dropdown>
          </div>
        </header>

        <!-- 农田列表 -->
        <el-card shadow="hover" class="farm-list-card">
          <template #header>
            <div class="card-header">
              <span>农田列表</span>
            </div>
          </template>
          <el-table :data="farms" style="width: 100%" @row-click="handleRowClick">
            <el-table-column prop="id" label="编号" width="80"></el-table-column>
            <el-table-column prop="name" label="农田名称"></el-table-column>
            <el-table-column prop="area" label="面积(亩)" width="100"></el-table-column>
            <el-table-column prop="location" label="位置"></el-table-column>
            <el-table-column prop="soilType" label="土壤类型" width="120"></el-table-column>
            <el-table-column prop="cropType" label="作物类型" width="120"></el-table-column>
            <el-table-column prop="status" label="状态" width="100">
              <template #default="scope">
                <el-tag :type="scope.row.status === '正常' ? 'success' : 'warning'">
                  {{ scope.row.status }}
                </el-tag>
              </template>
            </el-table-column>
          </el-table>
        </el-card>

        <!-- 农田详情 -->
        <el-card shadow="hover" class="farm-detail-card">
          <template #header>
            <div class="card-header">
              <span>农田详情</span>
            </div>
          </template>
          <div class="farm-detail">
            <div class="detail-row">
              <div class="detail-item">
                <label>农田名称：</label>
                <span>{{ selectedFarm.name }}</span>
              </div>
              <div class="detail-item">
                <label>面积：</label>
                <span>{{ selectedFarm.area }} 亩</span>
              </div>
              <div class="detail-item">
                <label>位置：</label>
                <span>{{ selectedFarm.location }}</span>
              </div>
            </div>
            <div class="detail-row">
              <div class="detail-item">
                <label>土壤类型：</label>
                <span>{{ selectedFarm.soilType }}</span>
              </div>
              <div class="detail-item">
                <label>作物类型：</label>
                <span>{{ selectedFarm.cropType }}</span>
              </div>
              <div class="detail-item">
                <label>状态：</label>
                <el-tag :type="selectedFarm.status === '正常' ? 'success' : 'warning'">
                  {{ selectedFarm.status }}
                </el-tag>
              </div>
            </div>
            <div class="detail-row">
              <div class="detail-item full-width">
                <label>备注：</label>
                <span>{{ selectedFarm.remark }}</span>
              </div>
            </div>
          </div>
        </el-card>
      </main>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import {
  ChatDotRound,
  Monitor,
  Location
} from '@element-plus/icons-vue'

const router = useRouter()

const expertName = ref('张教授')
const expertAvatar = ref('https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=professional%20agriculture%20expert%20portrait&image_size=square')

const farms = ref([])

const selectedFarm = ref({
  id: null,
  name: '',
  area: 0,
  location: '',
  soilType: '',
  cropType: '',
  status: '',
  remark: ''
})

const logout = () => {
  router.push('/expert/login')
}

const handleRowClick = (row) => {
  selectedFarm.value = row
}

const fetchFarmList = async () => {
  try {
    const response = await fetch('/api/farmland')
    const data = await response.json()
    farms.value = data.map(item => ({
      id: item.id,
      name: item.name,
      area: item.acreage,
      location: item.area,
      soilType: item.soilType,
      cropType: item.crop,
      status: item.status === '种植中' ? '正常' : item.status,
      remark: ''
    }))
    if (farms.value.length > 0) {
      selectedFarm.value = farms.value[0]
    }
  } catch (error) {
    console.error('获取农田列表失败:', error)
  }
}

onMounted(() => {
  fetchFarmList()
})
</script>

<style scoped>
.expert-farm {
  height: 100vh;
  overflow: hidden;
}

.app-container {
  display: flex;
  height: 100vh;
  overflow: hidden;
}

.sidebar {
  width: 240px;
  background-color: #2c3e50;
  color: #fff;
  transition: width 0.3s ease;
  display: flex;
  flex-direction: column;
}

.sidebar-header {
  display: flex;
  align-items: center;
  padding: 20px;
  border-bottom: 1px solid rgba(255, 255, 255, 0.1);
}

.logo {
  font-size: 18px;
  font-weight: bold;
  margin: 0;
}

.sidebar-menu {
  flex: 1;
  padding: 20px 0;
}

.menu-item {
  display: flex;
  align-items: center;
  padding: 12px 20px;
  color: #ecf0f1;
  text-decoration: none;
  transition: all 0.3s ease;
}

.menu-item:hover {
  background-color: rgba(255, 255, 255, 0.1);
}

.menu-item.router-link-active {
  background-color: #3498db;
  color: #fff;
}

.menu-item span {
  margin-left: 12px;
  transition: opacity 0.3s ease;
}

.main-content {
  flex: 1;
  display: flex;
  flex-direction: column;
  background-color: #f5f7fa;
  overflow: hidden;
}

.expert-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 20px;
  height: 64px;
  background-color: #fff;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
  flex-shrink: 0;
}

.header-left h2 {
  font-size: 18px;
  font-weight: 600;
  margin: 0;
}

.user-info {
  display: flex;
  align-items: center;
  cursor: pointer;
}

.user-info span {
  margin-left: 8px;
}

.farm-list-card {
  margin: 20px;
  flex-shrink: 0;
}

.farm-detail-card {
  margin: 0 20px 20px;
  flex: 1;
  display: flex;
  flex-direction: column;
}

.farm-detail-card :deep(.el-card__body) {
  flex: 1;
  overflow: auto;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.farm-detail {
  background-color: #f9f9f9;
  padding: 20px;
  border-radius: 8px;
}

.detail-row {
  display: flex;
  margin-bottom: 16px;
  flex-wrap: wrap;
}

.detail-item {
  flex: 1;
  min-width: 200px;
  margin-right: 20px;
  margin-bottom: 12px;
}

.detail-item.full-width {
  flex: 100%;
  margin-right: 0;
}

.detail-item label {
  display: inline-block;
  width: 100px;
  font-weight: 600;
  color: #606266;
}

.detail-item span {
  color: #303133;
}
</style>
