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
            <el-table-column label="头像" width="80">
              <template #default="scope">
                <img v-if="scope.row.avatar" :src="scope.row.avatar" class="avatar-mini" alt="头像" />
                <div v-else class="avatar-placeholder-mini">
                  {{ scope.row.name ? scope.row.name.charAt(0) : '?' }}
                </div>
              </template>
            </el-table-column>
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
      <el-tab-pane label="专家管理" name="expert">
        <!-- 专家管理内容 -->
        <div class="tab-content">
          <div class="action-bar">
            <el-button type="primary" @click="addExpert">
              <el-icon><plus /></el-icon>
              新增专家
            </el-button>
          </div>
          <el-table :data="experts" style="width: 100%" v-loading="expertLoading">
            <el-table-column prop="id" label="ID" width="80"></el-table-column>
            <el-table-column label="头像" width="80">
              <template #default="scope">
                <img v-if="scope.row.avatar" :src="scope.row.avatar" class="avatar-mini" alt="头像" />
                <div v-else class="avatar-placeholder-mini">
                  {{ scope.row.name ? scope.row.name.charAt(0) : '?' }}
                </div>
              </template>
            </el-table-column>
            <el-table-column prop="username" label="用户名"></el-table-column>
            <el-table-column prop="name" label="姓名" width="120"></el-table-column>
            <el-table-column prop="phone" label="电话" width="150"></el-table-column>
            <el-table-column prop="specialty" label="专业领域" width="180"></el-table-column>
            <el-table-column prop="status" label="状态" width="100">
              <template #default="scope">
                <el-tag :type="getExpertStatusTag(scope.row.status)">{{ scope.row.status }}</el-tag>
              </template>
            </el-table-column>
            <el-table-column prop="createTime" label="创建时间" width="180"></el-table-column>
            <el-table-column label="操作" width="150">
              <template #default="scope">
                <el-button type="primary" size="small" @click="editExpert(scope.row)">
                  编辑
                </el-button>
                <el-button type="danger" size="small" @click="deleteExpert(scope.row.id)">
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
        <el-form-item label="头像">
          <div class="avatar-upload">
            <img v-if="userForm.avatar" :src="userForm.avatar" class="avatar-preview" />
            <div v-else class="avatar-placeholder">
              <span>点击上传头像</span>
            </div>
            <input type="file" accept="image/*" class="avatar-input" @change="handleUserAvatarUpload" />
          </div>
        </el-form-item>
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

    <!-- 新增/编辑专家对话框 -->
    <el-dialog v-model="expertDialogVisible" :title="isEditExpert ? '编辑专家' : '新增专家'" width="500px">
      <el-form :model="expertForm" :rules="expertRules" ref="expertFormRef" label-width="80px">
        <el-form-item label="头像">
          <div class="avatar-upload">
            <img v-if="expertForm.avatar" :src="expertForm.avatar" class="avatar-preview" />
            <div v-else class="avatar-placeholder">
              <span>点击上传头像</span>
            </div>
            <input type="file" accept="image/*" class="avatar-input" @change="handleAvatarUpload" />
          </div>
        </el-form-item>
        <el-form-item label="用户名" prop="username">
          <el-input v-model="expertForm.username"></el-input>
        </el-form-item>
        <el-form-item label="密码" prop="password">
          <el-input v-model="expertForm.password" type="password"></el-input>
        </el-form-item>
        <el-form-item label="姓名" prop="name">
          <el-input v-model="expertForm.name"></el-input>
        </el-form-item>
        <el-form-item label="电话" prop="phone">
          <el-input v-model="expertForm.phone"></el-input>
        </el-form-item>
        <el-form-item label="邮箱" prop="email">
          <el-input v-model="expertForm.email"></el-input>
        </el-form-item>
        <el-form-item label="专业领域" prop="specialty">
          <el-input v-model="expertForm.specialty"></el-input>
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-select v-model="expertForm.status" style="width: 100%">
            <el-option label="启用" value="启用"></el-option>
            <el-option label="禁用" value="禁用"></el-option>
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="expertDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitExpert">确定</el-button>
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
  status: '启用',
  avatar: ''
})

const roleForm = reactive({
  id: null,
  name: '',
  description: ''
})

// 专家管理相关
const expertLoading = ref(false)
const experts = ref([])
const expertDialogVisible = ref(false)
const isEditExpert = ref(false)
const expertFormRef = ref()

const expertForm = reactive({
  id: null,
  username: '',
  password: '',
  name: '',
  phone: '',
  email: '',
  specialty: '',
  status: '启用',
  avatar: ''
})

const expertRules = {
  username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
  password: [{ required: true, message: '请输入密码', trigger: 'blur' }],
  name: [{ required: true, message: '请输入姓名', trigger: 'blur' }],
  specialty: [{ required: true, message: '请输入专业领域', trigger: 'blur' }]
}

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

const fetchExperts = async () => {
  expertLoading.value = true
  try {
    const response = await fetch('/api/experts')
    experts.value = await response.json()
  } catch (error) {
    ElMessage.error('获取专家列表失败')
  } finally {
    expertLoading.value = false
  }
}

onMounted(() => {
  fetchUsers()
  fetchRoles()
  fetchExperts()
})

const addUser = () => {
  isEditUser.value = false
  Object.assign(userForm, {
    id: null,
    username: '',
    password: '',
    name: '',
    role: '',
    status: '启用',
    avatar: ''
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
    status: row.status,
    avatar: row.avatar || ''
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

const getExpertStatusTag = (status) => {
  const tagMap = {
    '启用': 'success',
    '禁用': 'danger'
  }
  return tagMap[status] || 'default'
}

const addExpert = () => {
  isEditExpert.value = false
  Object.assign(expertForm, {
    id: null,
    username: '',
    password: '',
    name: '',
    phone: '',
    email: '',
    specialty: '',
    status: '启用',
    avatar: ''
  })
  expertDialogVisible.value = true
}

const editExpert = (row) => {
  isEditExpert.value = true
  Object.assign(expertForm, {
    id: row.id,
    username: row.username,
    password: '',
    name: row.name,
    phone: row.phone || '',
    email: row.email || '',
    specialty: row.specialty,
    status: row.status,
    avatar: row.avatar || ''
  })
  expertDialogVisible.value = true
}

const handleAvatarUpload = (event) => {
  const file = event.target.files[0]
  if (file) {
    const reader = new FileReader()
    reader.onload = (e) => {
      const img = new Image()
      img.onload = () => {
        const canvas = document.createElement('canvas')
        const maxWidth = 200
        const maxHeight = 200
        let width = img.width
        let height = img.height

        if (width > height) {
          if (width > maxWidth) {
            height = height * (maxWidth / width)
            width = maxWidth
          }
        } else {
          if (height > maxHeight) {
            height = height * (maxHeight / height)
            width = maxWidth
          }
        }

        canvas.width = width
        canvas.height = height

        const ctx = canvas.getContext('2d')
        ctx.drawImage(img, 0, 0, width, height)

        expertForm.avatar = canvas.toDataURL('image/jpeg', 0.6)
      }
      img.src = e.target.result
    }
    reader.readAsDataURL(file)
  }
}

const handleUserAvatarUpload = (event) => {
  const file = event.target.files[0]
  if (file) {
    const reader = new FileReader()
    reader.onload = (e) => {
      const img = new Image()
      img.onload = () => {
        const canvas = document.createElement('canvas')
        const maxWidth = 200
        const maxHeight = 200
        let width = img.width
        let height = img.height

        if (width > height) {
          if (width > maxWidth) {
            height = height * (maxWidth / width)
            width = maxWidth
          }
        } else {
          if (height > maxHeight) {
            height = height * (maxHeight / height)
            width = maxWidth
          }
        }

        canvas.width = width
        canvas.height = height

        const ctx = canvas.getContext('2d')
        ctx.drawImage(img, 0, 0, width, height)

        userForm.avatar = canvas.toDataURL('image/jpeg', 0.6)
      }
      img.src = e.target.result
    }
    reader.readAsDataURL(file)
  }
}

const submitExpert = async () => {
  try {
    await expertFormRef.value.validate()
    const url = isEditExpert.value ? `/api/experts/${expertForm.id}` : '/api/experts'
    const method = isEditExpert.value ? 'PUT' : 'POST'
    
    const response = await fetch(url, {
      method,
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(expertForm)
    })
    
    if (response.ok) {
      ElMessage.success(isEditExpert.value ? '编辑成功' : '新增成功')
      expertDialogVisible.value = false
      fetchExperts()
    } else {
      ElMessage.error('操作失败')
    }
  } catch (error) {
    console.error(error)
  }
}

const deleteExpert = async (id) => {
  try {
    await ElMessageBox.confirm('确定要删除该专家吗？', '提示', { type: 'warning' })
    const response = await fetch(`/api/experts/${id}`, { method: 'DELETE' })
    if (response.ok) {
      ElMessage.success('删除成功')
      fetchExperts()
    } else {
      ElMessage.error('删除失败')
    }
  } catch (error) {
    // 取消删除
  }
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

.avatar-upload {
  position: relative;
  width: 120px;
  height: 120px;
  cursor: pointer;
}

.avatar-preview {
  width: 100%;
  height: 100%;
  border-radius: 50%;
  object-fit: cover;
  border: 2px solid #e0e0e0;
}

.avatar-placeholder {
  width: 100%;
  height: 100%;
  border-radius: 50%;
  background-color: #f5f5f5;
  display: flex;
  align-items: center;
  justify-content: center;
  border: 2px dashed #ccc;
}

.avatar-placeholder span {
  font-size: 12px;
  color: #999;
}

.avatar-input {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  opacity: 0;
  cursor: pointer;
}

.avatar-mini {
  width: 40px;
  height: 40px;
  border-radius: 50%;
  object-fit: cover;
}

.avatar-placeholder-mini {
  width: 40px;
  height: 40px;
  border-radius: 50%;
  background-color: #e0e0e0;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 14px;
  color: #666;
}
</style>