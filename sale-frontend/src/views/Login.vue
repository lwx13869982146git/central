<template>
  <div class="login-container">
    <div class="login-box">
      <div class="login-header">
        <h2>金融交易系统</h2>
        <p>欢迎登录</p>
      </div>
      
      <el-form :model="loginForm" :rules="loginRules" ref="loginForm" class="login-form">
        <el-form-item prop="username">
          <el-input
            v-model="loginForm.username"
            placeholder="请输入用户名"
            prefix-icon="el-icon-user"
            size="medium"
          ></el-input>
        </el-form-item>
        
        <el-form-item prop="password">
          <el-input
            v-model="loginForm.password"
            type="password"
            placeholder="请输入密码"
            prefix-icon="el-icon-lock"
            size="medium"
            @keyup.enter.native="handleLogin"
          ></el-input>
        </el-form-item>
        
        <el-form-item>
          <el-button
            type="primary"
            size="medium"
            :loading="loading"
            @click="handleLogin"
            style="width: 100%;"
          >
            登 录
          </el-button>
        </el-form-item>
      </el-form>
      
      <div class="login-tips">
        <p>默认账号：admin / admin123</p>
      </div>
    </div>
  </div>
</template>

<script>
import { authRequest } from '@/utils/request';

export default {
  name: 'Login',
  data() {
    return {
      loginForm: {
        username: '',
        password: ''
      },
      loginRules: {
        username: [
          { required: true, message: '请输入用户名', trigger: 'blur' }
        ],
        password: [
          { required: true, message: '请输入密码', trigger: 'blur' }
        ]
      },
      loading: false
    };
  },
  methods: {
    handleLogin() {
      this.$refs.loginForm.validate(async (valid) => {
        if (valid) {
          this.loading = true;
          try {
            const response = await authRequest.post('/auth/login', this.loginForm);
            
            // 保存 token 和用户信息
            localStorage.setItem('token', response.data.token);
            localStorage.setItem('userInfo', JSON.stringify({
              username: response.data.username,
              nickname: response.data.nickname,
              userId: response.data.userId
            }));
            
            this.$message.success('登录成功');
            
            // 跳转到首页
            this.$router.push('/');
          } catch (error) {
            this.$message.error(error.message || '登录失败');
          } finally {
            this.loading = false;
          }
        }
      });
    }
  }
};
</script>

<style scoped>
.login-container {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 100vh;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
}

.login-box {
  width: 400px;
  padding: 40px;
  background: white;
  border-radius: 10px;
  box-shadow: 0 10px 40px rgba(0, 0, 0, 0.2);
}

.login-header {
  text-align: center;
  margin-bottom: 30px;
}

.login-header h2 {
  margin: 0 0 10px 0;
  color: #333;
  font-size: 28px;
}

.login-header p {
  margin: 0;
  color: #999;
  font-size: 14px;
}

.login-form {
  margin-top: 20px;
}

.login-tips {
  text-align: center;
  margin-top: 20px;
  color: #999;
  font-size: 12px;
}

.login-tips p {
  margin: 5px 0;
}
</style>
