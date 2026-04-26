<template>
  <div class="system">
    <!-- 标签页 -->
    <el-tabs v-model="activeTab">
      <el-tab-pane label="用户管理" name="user">
        <!-- 用户管理内容 -->
        <div class="tab-content">
          <div class="action-bar">
            <el-button type="primary" @click="addUser">
              <el-icon><plus /></el-icon>
              新增用户
            </el-button>
          </div>
          <el-table :data="users" style="width: 100%">
            <el-table-column prop="id" label="ID" width="80"></el-table-column>
            <el-table-column prop="username" label="用户名"></el-table-column>
            <el-table-column prop="name" label="姓名" width="120"></el-table-column>
            <el-table-column prop="role" label="角色" width="120"></el-table-column>
            <el-table-column prop="status" label="状态" width="100">
              <template #default="scope">
                <el-tag :type="getUserStatusTag(scope.row.status)">{{ scope.row.status }}</el-tag>
              </template>
            </el-table-column>
            <el-table-column prop="createTime" label="创建时间" width="180"></el-table-column>
            <el-table-column label="操作" width="150">
              <template #default="scope">
                <el-button type="primary" size="small" @click="editUser(scope.row)">
                  编辑
                </el-button>
                <el-button type="danger" size="small" @click="deleteUser(scope.row.id)">
                  删除
                </el-button>
              </template>
            </el-table-column>
          </el-table>
        </div>
      </el-tab-pane>
      <el-tab-pane label="角色管理" name="role">
        <!-- 角色管理内容 -->
        <div class="tab-content">
          <div class="action-bar">
            <el-button type="primary" @click="addRole">
              <el-icon><plus /></el-icon>
              新增角色
            </el-button>
          </div>
          <el-table :data="roles" style="width: 100%">
            <el-table-column prop="id" label="ID" width="80"></el-table-column>
            <el-table-column prop="name" label="角色名称"></el-table-column>
            <el-table-column prop="description" label="描述"></el-table-column>
            <el-table-column label="操作" width="150">
              <template #default="scope">
                <el-button type="primary" size="small" @click="editRole(scope.row)">
                  编辑
                </el-button>
                <el-button type="danger" size="small" @click="deleteRole(scope.row.id)">
                  删除
                </el-button>
              </template>
            </el-table-column>
          </el-table>
        </div>
      </el-tab-pane>
      <el-tab-pane label="系统设置" name="setting">
        <!-- 系统设置内容 -->
        <div class="tab-content">
          <el-form :model="systemSettings" label-width="120px">
            <el-form-item label="系统名称">
              <el-input v-model="systemSettings.systemName" placeholder="输入系统名称"></el-input>
            </el-form-item>
            <el-form-item label="系统版本">
              <el-input v-model="systemSettings.version" placeholder="输入系统版本" disabled></el-input>
            </el-form-item>
            <el-form-item label="联系邮箱">
              <el-input v-model="systemSettings.email" placeholder="输入联系邮箱"></el-input>
            </el-form-item>
            <el-form-item label="联系电话">
              <el-input v-model="systemSettings.phone" placeholder="输入联系电话"></el-input>
            </el-form-item>
            <el-form-item>
              <el-button type="primary" @click="saveSettings">保存设置</el-button>
            </el-form-item>
          </el-form>
        </div>
      </el-tab-pane>
    </el-tabs>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { Plus } from '@element-plus/icons-vue'

const activeTab = ref('user')

const users = ref([
  {
    id: 1,
    username: 'admin',
    name: '管理员',
    role: '管理员',
    status: '启用',
    createTime: '2026-01-01 00:00:00'
  },
  {
    id: 2,
    username: 'user1',
    name: '用户1',
    role: '普通用户',
    status: '启用',
    createTime: '2026-01-02 00:00:00'
  },
  {
    id: 3,
    username: 'user2',
    name: '用户2',
    role: '普通用户',
    status: '禁用',
    createTime: '2026-01-03 00:00:00'
  }
])

const roles = ref([
  {
    id: 1,
    name: '管理员',
    description: '系统管理员，拥有所有权限'
  },
  {
    id: 2,
    name: '普通用户',
    description: '普通用户，拥有基本操作权限'
  }
])

const systemSettings = ref({
  systemName: '智慧农业管理系统',
  version: '1.0.0',
  email: 'contact@example.com',
  phone: '12345678901'
})

const addUser = () => {
  // 新增用户
  console.log('新增用户')
}

const editUser = (row) => {
  // 编辑用户
  console.log('编辑用户:', row)
}

const deleteUser = (id) => {
  // 删除用户
  console.log('删除用户:', id)
}

const addRole = () => {
  // 新增角色
  console.log('新增角色')
}

const editRole = (row) => {
  // 编辑角色
  console.log('编辑角色:', row)
}

const deleteRole = (id) => {
  // 删除角色
  console.log('删除角色:', id)
}

const saveSettings = () => {
  // 保存设置
  console.log('保存设置:', systemSettings.value)
}

const getUserStatusTag = (status) => {
  const tagMap = {
    '启用': 'success',
    '禁用': 'danger'
  }
  return tagMap[status] || 'default'
}
</script>

<style scoped>
.system {
  display: flex;
  flex-direction: column;
  gap: 20px;
  height: 100%;
  box-sizing: border-box;
}

.system :deep(.el-tabs__content) {
  height: calc(100% - 55px);
  overflow: auto;
}

.tab-content {
  padding: 20px 0;
  height: 100%;
}

.action-bar {
  margin-bottom: 20px;
}
</style>
