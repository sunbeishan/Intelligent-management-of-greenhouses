<template>
  <div class="login-container">
    <div class="login-form-wrapper">
      <div class="login-header">
        <h1 class="login-title">智慧农业管理系统</h1>
        <p class="login-subtitle">请登录以访问系统</p>
      </div>
      <el-form :model="loginForm" :rules="loginRules" ref="loginFormRef" class="login-form">
        <el-form-item prop="username">
          <el-input v-model="loginForm.username" placeholder="用户名">
            <template #prefix>
              <el-icon><user /></el-icon>
            </template>
          </el-input>
        </el-form-item>
        <el-form-item prop="password">
          <el-input v-model="loginForm.password" type="password" placeholder="密码">
            <template #prefix>
              <el-icon><lock /></el-icon>
            </template>
          </el-input>
        </el-form-item>
        <el-form-item>
          <el-checkbox v-model="loginForm.remember">记住我</el-checkbox>
          <el-link type="primary" class="forgot-password" @click="goToRegister">注册账号</el-link>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" class="login-button" @click="handleLogin">
            登录
          </el-button>
        </el-form-item>
      </el-form>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { useAppStore } from '../stores/index.js'
import { User, Lock } from '@element-plus/icons-vue'

const router = useRouter()
const store = useAppStore()
const loginFormRef = ref()

const loginForm = reactive({
  username: '',
  password: '',
  remember: false
})

const loginRules = {
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' }
  ]
}

const handleLogin = async () => {
  try {
    await loginFormRef.value.validate()
    // 模拟登录成功
    store.setUser({ name: loginForm.username, role: 'admin' })
    router.push('/dashboard')
  } catch (error) {
    console.error('登录验证失败:', error)
  }
}

const goToRegister = () => {
  router.push('/register')
}
</script>

<style scoped>
.login-container {
  width: 100vw;
  height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  overflow: hidden;
}

.login-form-wrapper {
  width: 400px;
  max-width: 90%;
  background: #fff;
  border-radius: 12px;
  box-shadow: 0 10px 30px rgba(0, 0, 0, 0.1);
  padding: 40px;
  transition: all 0.3s ease;
}

@media (max-width: 768px) {
  .login-form-wrapper {
    padding: 30px;
  }
  
  .login-title {
    font-size: 20px;
  }
}

.login-header {
  text-align: center;
  margin-bottom: 32px;
}

.login-title {
  font-size: 24px;
  font-weight: 600;
  color: #333;
  margin: 0 0 8px 0;
}

.login-subtitle {
  font-size: 14px;
  color: #666;
  margin: 0;
}

.login-form {
  width: 100%;
}

.login-button {
  width: 100%;
  height: 48px;
  font-size: 16px;
  font-weight: 500;
}

.forgot-password {
  float: right;
  font-size: 14px;
  margin-left: 20px;
}
</style>
