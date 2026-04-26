<template>
  <!-- 登录页面 -->
  <div v-if="$route.path === '/login'">
    <router-view />
  </div>
  <!-- 专家登录页面 -->
  <div v-else-if="$route.path === '/expert/login'">
    <router-view />
  </div>
  <!-- 专家控制台页面 -->
  <div v-else-if="$route.path.startsWith('/expert/')">
    <router-view />
  </div>
  <!-- 主应用页面 -->
  <div v-else class="app-container">
    <!-- 侧边栏 -->
    <aside class="sidebar" :class="{ 'collapsed': !sidebarOpen }">
      <div class="sidebar-header">
        <h1 class="logo">智慧农业</h1>
        <el-button type="text" @click="toggleSidebar" class="collapse-btn">
          <el-icon><arrow-right /></el-icon>
        </el-button>
      </div>
      <nav class="sidebar-menu">
        <router-link to="/dashboard" class="menu-item">
          <el-icon><data-analysis /></el-icon>
          <span>统计分析</span>
        </router-link>
        <router-link to="/environment" class="menu-item">
          <el-icon><monitor /></el-icon>
          <span>环境监测</span>
        </router-link>
        <router-link to="/farm" class="menu-item">
          <el-icon><location /></el-icon>
          <span>农田信息管理</span>
        </router-link>
        <router-link to="/expert" class="menu-item">
          <el-icon><user /></el-icon>
          <span>专家咨询</span>
        </router-link>
        <!-- 农资管理下拉菜单 -->
        <div class="dropdown-item">
          <div class="dropdown-header" @click="toggleDropdown('materials')">
            <el-icon><goods /></el-icon>
            <span>农资管理</span>
            <el-icon class="dropdown-arrow" :class="{ 'rotated': dropdowns.materials }"><arrow-down /></el-icon>
          </div>
          <div class="dropdown-menu" v-show="dropdowns.materials">
            <router-link to="/materials/purchase" class="dropdown-menu-item">
              <el-icon><shopping-cart /></el-icon>
              <span>农资采购</span>
            </router-link>
            <router-link to="/materials/products" class="dropdown-menu-item">
              <el-icon><box /></el-icon>
              <span>产品出售</span>
            </router-link>
            <router-link to="/materials/inventory" class="dropdown-menu-item">
              <el-icon><trend-charts /></el-icon>
              <span>库存</span>
            </router-link>
          </div>
        </div>
        <router-link to="/system" class="menu-item">
          <el-icon><setting /></el-icon>
          <span>系统管理</span>
        </router-link>
      </nav>
    </aside>
    
    <!-- 主内容区 -->
    <main class="main-content">
      <!-- 顶部导航 -->
      <header class="top-nav">
        <div class="nav-left">
          <el-button type="text" @click="toggleSidebar">
            <el-icon><menu /></el-icon>
          </el-button>
          <h2 class="page-title">{{ $route.meta.title }}</h2>
        </div>
        <div class="nav-right">
          <el-dropdown>
            <span class="user-info">
              <el-avatar :size="32" :src="userAvatar"></el-avatar>
              <span>{{ user.name }}</span>
            </span>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item>个人中心</el-dropdown-item>
                <el-dropdown-item>退出登录</el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </div>
      </header>
      
      <!-- 内容区域 -->
      <div class="content-wrapper">
        <router-view />
      </div>
    </main>
  </div>
</template>

<script setup>
import { computed, ref } from 'vue'
import { useAppStore } from './stores/index.js'
import {
  ArrowRight,
  DataAnalysis,
  Monitor,
  Location,
  User,
  Goods,
  ShoppingCart,
  Setting,
  Menu,
  ArrowDown,
  TrendCharts,
  Box
} from '@element-plus/icons-vue'

const store = useAppStore()
const sidebarOpen = computed(() => store.sidebarOpen)
const user = computed(() => store.user)
const userAvatar = computed(() => 'https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=user%20avatar%20portrait&image_size=square')

const dropdowns = ref({
  materials: false
})

const toggleSidebar = () => {
  store.toggleSidebar()
}

const toggleDropdown = (key) => {
  dropdowns.value[key] = !dropdowns.value[key]
}
</script>

<style scoped>
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

.sidebar.collapsed {
  width: 64px;
}

.sidebar-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 20px;
  border-bottom: 1px solid rgba(255, 255, 255, 0.1);
}

.logo {
  font-size: 18px;
  font-weight: bold;
  margin: 0;
}

.sidebar.collapsed .logo {
  display: none;
}

.collapse-btn {
  color: #fff;
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

.sidebar.collapsed .menu-item span {
  opacity: 0;
  width: 0;
  overflow: hidden;
  margin-left: 0;
}

.sidebar.collapsed .dropdown-header span {
  opacity: 0;
  width: 0;
  overflow: hidden;
  margin-left: 0;
}

.sidebar.collapsed .dropdown-header {
  padding: 12px 20px;
  justify-content: center;
}

.sidebar.collapsed .dropdown-arrow {
  display: none;
}

/* 下拉菜单样式 */
.dropdown-item {
  position: relative;
  cursor: pointer;
  display: block;
}

.dropdown-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  width: 100%;
  padding: 12px 20px;
  box-sizing: border-box;
}

.dropdown-header:hover {
  background-color: rgba(255, 255, 255, 0.1);
}

.dropdown-header.router-link-active {
  background-color: #3498db;
  color: #fff;
}

.dropdown-arrow {
  transition: transform 0.3s ease;
  margin-left: auto;
}

.dropdown-arrow.rotated {
  transform: rotate(180deg);
}

.dropdown-menu {
  background-color: #2c3e50;
  overflow: hidden;
  transition: all 0.3s ease;
}

.dropdown-menu-item {
  display: flex;
  align-items: center;
  padding: 12px 20px 12px 48px;
  color: #ecf0f1;
  text-decoration: none;
  transition: all 0.3s ease;
  width: 100%;
  box-sizing: border-box;
}

.dropdown-menu-item:hover {
  background-color: rgba(255, 255, 255, 0.1);
}

.dropdown-menu-item.router-link-active {
  background-color: #3498db;
  color: #fff;
}

.dropdown-menu-item span {
  margin-left: 12px;
  transition: opacity 0.3s ease;
}

.sidebar.collapsed .dropdown-menu {
  display: none !important;
}

.sidebar.collapsed .dropdown-menu-item span {
  opacity: 0;
  width: 0;
  overflow: hidden;
  margin-left: 0;
}

.main-content {
  flex: 1;
  display: flex;
  flex-direction: column;
  background-color: #f5f7fa;
  overflow: hidden;
}

.top-nav {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 20px;
  height: 64px;
  background-color: #fff;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
}

.nav-left {
  display: flex;
  align-items: center;
}

.page-title {
  font-size: 16px;
  font-weight: 600;
  margin: 0 0 0 12px;
}

.user-info {
  display: flex;
  align-items: center;
  cursor: pointer;
}

.user-info span {
  margin-left: 8px;
}

.content-wrapper {
  flex: 1;
  padding: 20px 0;
  overflow: hidden;
  height: calc(100vh - 64px);
}

.content-wrapper > * {
  height: 100%;
  overflow: auto;
  padding: 0 20px;
}
</style>
