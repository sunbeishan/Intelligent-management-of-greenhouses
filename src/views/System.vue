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
          <el-table :data="users" style="width: 100%" v-loading="loading">
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
          <el-table :data="roles" style="width: 100%" v-loading="roleLoading">
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

    <!-- 新增/编辑用户对话框 -->
    <el-dialog v-model="userDialogVisible" :title="isEditUser ? '编辑用户' : '新增用户'" width="500px">
      <el-form :model="userForm" :rules="userRules" ref="userFormRef" label-width="80px">
        <el-form-item label="用户名" prop="username">
          <el-input v-model="userForm.username"></el-input>
        </el-form-item>
        <el-form-item label="密码" prop="password">
          <el-input v-model="userForm.password" type="password"></el-input>
        </el-form-item>
        <el-form-item label="姓名" prop="name">
          <el-input v-model="userForm.name"></el-input>
        </el-form-item>
        <el-form-item label="角色" prop="role">
          <el-select v-model="userForm.role" style="width: 100%">
            <el-option v-for="role in roles" :key="role.id" :label="role.name" :value="role.name"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-select v-model="userForm.status" style="width: 100%">
            <el-option label="启用" value="启用"></el-option>
            <el-option label="禁用" value="禁用"></el-option>
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="userDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitUser">确定</el-button>
      </template>
    </el-dialog>

    <!-- 新增/编辑角色对话框 -->
    <el-dialog v-model="roleDialogVisible" :title="isEditRole ? '编辑角色' : '新增角色'" width="500px">
      <el-form :model="roleForm" :rules="roleRules" ref="roleFormRef" label-width="80px">
        <el-form-item label="角色名称" prop="name">
          <el-input v-model="roleForm.name"></el-input>
        </el-form-item>
        <el-form-item label="描述" prop="description">
          <el-input v-model="roleForm.description" type="textarea"></el-input>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="roleDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitRole">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { Plus } from '@element-plus/icons-vue'
import { ElMessage, ElMessageBox } from 'element-plus'

const activeTab = ref('user')
const loading = ref(false)
const roleLoading = ref(false)

const users = ref([])
const roles = ref([])

const systemSettings = ref({
  systemName: '智慧农业管理系统',
  version: '1.0.0',
  email: 'contact@example.com',
  phone: '12345678901'
})

// 对话框相关
const userDialogVisible = ref(false)
const roleDialogVisible = ref(false)
const isEditUser = ref(false)
const isEditRole = ref(false)
const userFormRef = ref()
const roleFormRef = ref()

const userForm = reactive({
  id: null,
  username: '',
  password: '',
  name: '',
  role: '',
  status: '启用'
})

const roleForm = reactive({
  id: null,
  name: '',
  description: ''
})

const userRules = {
  username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
  password: [{ required: true, message: '请输入密码', trigger: 'blur' }],
  name: [{ required: true, message: '请输入姓名', trigger: 'blur' }],
  role: [{ required: true, message: '请选择角色', trigger: 'change' }],
  status: [{ required: true, message: '请选择状态', trigger: 'change' }]
}

const roleRules = {
  name: [{ required: true, message: '请输入角色名称', trigger: 'blur' }],
  description: [{ required: true, message: '请输入描述', trigger: 'blur' }]
}

// API 调用
const fetchUsers = async () => {
  loading.value = true
  try {
    const response = await fetch('/api/users')
    users.value = await response.json()
  } catch (error) {
    ElMessage.error('获取用户列表失败')
  } finally {
    loading.value = false
  }
}

const fetchRoles = async () => {
  roleLoading.value = true
  try {
    const response = await fetch('/api/roles')
    roles.value = await response.json()
  } catch (error) {
    ElMessage.error('获取角色列表失败')
  } finally {
    roleLoading.value = false
  }
}

onMounted(() => {
  fetchUsers()
  fetchRoles()
})

const addUser = () => {
  isEditUser.value = false
  Object.assign(userForm, {
    id: null,
    username: '',
    password: '',
    name: '',
    role: '',
    status: '启用'
  })
  userDialogVisible.value = true
}

const editUser = (row) => {
  isEditUser.value = true
  Object.assign(userForm, {
    id: row.id,
    username: row.username,
    password: '',
    name: row.name,
    role: row.role,
    status: row.status
  })
  userDialogVisible.value = true
}

const submitUser = async () => {
  try {
    await userFormRef.value.validate()
    const url = isEditUser.value ? `/api/users/${userForm.id}` : '/api/users'
    const method = isEditUser.value ? 'PUT' : 'POST'
    
    const response = await fetch(url, {
      method,
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(userForm)
    })
    
    if (response.ok) {
      ElMessage.success(isEditUser.value ? '编辑成功' : '新增成功')
      userDialogVisible.value = false
      fetchUsers()
    } else {
      ElMessage.error('操作失败')
    }
  } catch (error) {
    console.error(error)
  }
}

const deleteUser = async (id) => {
  try {
    await ElMessageBox.confirm('确定要删除该用户吗？', '提示', { type: 'warning' })
    const response = await fetch(`/api/users/${id}`, { method: 'DELETE' })
    if (response.ok) {
      ElMessage.success('删除成功')
      fetchUsers()
    } else {
      ElMessage.error('删除失败')
    }
  } catch (error) {
    // 取消删除
  }
}

const addRole = () => {
  isEditRole.value = false
  Object.assign(roleForm, {
    id: null,
    name: '',
    description: ''
  })
  roleDialogVisible.value = true
}

const editRole = (row) => {
  isEditRole.value = true
  Object.assign(roleForm, {
    id: row.id,
    name: row.name,
    description: row.description
  })
  roleDialogVisible.value = true
}

const submitRole = async () => {
  try {
    await roleFormRef.value.validate()
    const url = isEditRole.value ? `/api/roles/${roleForm.id}` : '/api/roles'
    const method = isEditRole.value ? 'PUT' : 'POST'
    
    const response = await fetch(url, {
      method,
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(roleForm)
    })
    
    if (response.ok) {
      ElMessage.success(isEditRole.value ? '编辑成功' : '新增成功')
      roleDialogVisible.value = false
      fetchRoles()
      fetchUsers()
    } else {
      ElMessage.error('操作失败')
    }
  } catch (error) {
    console.error(error)
  }
}

const deleteRole = async (id) => {
  try {
    await ElMessageBox.confirm('确定要删除该角色吗？', '提示', { type: 'warning' })
    const response = await fetch(`/api/roles/${id}`, { method: 'DELETE' })
    if (response.ok) {
      ElMessage.success('删除成功')
      fetchRoles()
    } else {
      ElMessage.error('删除失败')
    }
  } catch (error) {
    // 取消删除
  }
}

const saveSettings = () => {
  ElMessage.success('设置保存成功')
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