<template>
  <div class="expert-login-container">
    <div class="expert-login-wrapper">
      <h2 class="login-title">专家登录</h2>
      <el-form :model="loginForm" :rules="rules" ref="loginFormRef" label-width="80px">
        <el-form-item label="专家编号" prop="expertId">
          <el-input v-model="loginForm.expertId" placeholder="请输入专家编号"></el-input>
        </el-form-item>
        <el-form-item label="密码" prop="password">
          <el-input v-model="loginForm.password" type="password" placeholder="请输入密码"></el-input>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="login" style="width: 100%">登录</el-button>
        </el-form-item>
      </el-form>
      <div class="login-footer">
        <router-link to="/login">用户登录</router-link>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'

const router = useRouter()
const loginFormRef = ref(null)

const loginForm = reactive({
  expertId: '',
  password: ''
})

const rules = {
  expertId: [
    { required: true, message: '请输入专家编号', trigger: 'blur' }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' }
  ]
}

const login = () => {
  loginFormRef.value.validate((valid) => {
    if (valid) {
      // 模拟专家登录
      console.log('专家登录:', loginForm)
      // 登录成功后跳转到专家控制台
      router.push('/expert/dashboard')
    }
  })
}
</script>

<style scoped>
.expert-login-container {
  width: 100vw;
  height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  overflow: hidden;
}

.expert-login-wrapper {
  width: 400px;
  max-width: 90%;
  background: #fff;
  border-radius: 12px;
  box-shadow: 0 10px 30px rgba(0, 0, 0, 0.1);
  padding: 40px;
  transition: all 0.3s ease;
}

.login-title {
  text-align: center;
  font-size: 24px;
  font-weight: 600;
  margin-bottom: 30px;
  color: #303133;
}

.login-footer {
  margin-top: 20px;
  text-align: center;
  font-size: 14px;
  color: #606266;
}

.login-footer a {
  color: #409eff;
  text-decoration: none;
}

.login-footer a:hover {
  text-decoration: underline;
}

@media (max-width: 768px) {
  .expert-login-wrapper {
    padding: 30px;
  }
  
  .login-title {
    font-size: 20px;
  }
}
</style>
