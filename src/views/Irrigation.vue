<template>
  <div class="irrigation">
    <div class="tabs-container">
      <el-tabs v-model="activeTab" @tab-change="handleTabChange">
        <el-tab-pane v-if="isAdmin" label="设备管理" name="devices">
          <IrrigationDevices v-if="activeTab === 'devices'" :key="devicesKey" @refresh="refreshDevices" />
        </el-tab-pane>
        <el-tab-pane label="灌溉计划" name="plans">
          <IrrigationPlans v-if="activeTab === 'plans'" :key="plansKey" @refresh="refreshPlans" />
        </el-tab-pane>
        <el-tab-pane label="灌溉记录" name="records">
          <IrrigationRecords v-if="activeTab === 'records'" :key="recordsKey" @refresh="refreshRecords" />
        </el-tab-pane>
      </el-tabs>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useAppStore } from '../stores/index.js'
import IrrigationDevices from './IrrigationDevices.vue'
import IrrigationPlans from './IrrigationPlans.vue'
import IrrigationRecords from './IrrigationRecords.vue'

const store = useAppStore()
const isAdmin = computed(() => store.user.role === '管理员')
const activeTab = ref('plans')

onMounted(() => {
  activeTab.value = isAdmin.value ? 'devices' : 'plans'
})
const devicesKey = ref(0)
const plansKey = ref(0)
const recordsKey = ref(0)

const handleTabChange = (tab) => {
  activeTab.value = tab
}

const refreshDevices = () => {
  devicesKey.value++
}

const refreshPlans = () => {
  plansKey.value++
}

const refreshRecords = () => {
  recordsKey.value++
}
</script>

<style scoped>
.irrigation {
  height: 100%;
  display: flex;
  flex-direction: column;
  box-sizing: border-box;
}

.tabs-container {
  flex: 1;
  display: flex;
  flex-direction: column;
  min-height: 0;
}

.tabs-container :deep(.el-tabs) {
  height: 100%;
  display: flex;
  flex-direction: column;
}

.tabs-container :deep(.el-tabs__content) {
  flex: 1;
  min-height: 0;
  overflow: auto;
}

.tabs-container :deep(.el-tab-pane) {
  height: 100%;
}
</style>