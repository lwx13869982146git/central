<template>
  <div class="layout-container">
    <!-- 顶部标题栏 -->
    <el-header class="header">
      <h1 class="title">交易系统</h1>
      <div class="user-info">
        <el-dropdown @command="handleCommand">
          <span class="username">
            <i class="el-icon-user"></i>
            {{ userInfo.nickname || userInfo.username }}
            <i class="el-icon-arrow-down"></i>
          </span>
          <el-dropdown-menu slot="dropdown">
            <el-dropdown-item command="logout">
              <i class="el-icon-switch-button"></i> 退出登录
            </el-dropdown-item>
          </el-dropdown-menu>
        </el-dropdown>
      </div>
    </el-header>

    <el-container class="main-container">
      <!-- 左侧导航栏 -->
      <el-aside width="200px" class="sidebar">
        <el-menu
          :default-active="activeMenu"
          class="menu"
          background-color="#304156"
          text-color="#bfcbd9"
          active-text-color="#409EFF"
          router
        >
          <el-menu-item index="/">
            <i class="el-icon-s-home"></i>
            <span slot="title">首页</span>
          </el-menu-item>
          
          <el-submenu index="2">
            <template slot="title">
              <i class="el-icon-goods"></i>
              <span>产品</span>
            </template>
            <el-menu-item index="/product/list">
              <i class="el-icon-tickets"></i>
              <span slot="title">产品列表</span>
            </el-menu-item>
          </el-submenu>

          <el-submenu index="3">
            <template slot="title">
              <i class="el-icon-data-line"></i>
              <span>行情</span>
            </template>
            <el-menu-item index="/price/list">
              <i class="el-icon-s-data"></i>
              <span slot="title">行情列表</span>
            </el-menu-item>
          </el-submenu>

          <el-submenu index="4">
            <template slot="title">
              <i class="el-icon-s-order"></i>
              <span>交易</span>
            </template>
            <el-menu-item index="/trade/record-list">
              <i class="el-icon-tickets"></i>
              <span slot="title">交易记录</span>
            </el-menu-item>
          </el-submenu>
        </el-menu>
      </el-aside>

      <!-- 主内容区 -->
      <el-main class="content">
        <router-view />
      </el-main>
    </el-container>
  </div>
</template>

<script>
export default {
  name: 'Layout',
  data() {
    return {
      userInfo: {}
    };
  },
  created() {
    // 获取用户信息
    const userInfo = localStorage.getItem('userInfo');
    if (userInfo) {
      this.userInfo = JSON.parse(userInfo);
    }
  },
  computed: {
    activeMenu() {
      return this.$route.path;
    }
  },
  methods: {
    handleCommand(command) {
      if (command === 'logout') {
        this.handleLogout();
      }
    },
    handleLogout() {
      this.$confirm('确定要退出登录吗？', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        // 清除 token 和用户信息
        localStorage.removeItem('token');
        localStorage.removeItem('userInfo');
        // 跳转到登录页
        this.$router.push('/login');
        this.$message.success('已退出登录');
      }).catch(() => {});
    }
  }
};
</script>

<style scoped>
.layout-container {
  height: 100vh;
  display: flex;
  flex-direction: column;
}

.header {
  background-color: #409EFF;
  color: white;
  display: flex;
  align-items: center;
  padding: 0 20px;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
}

.title {
  font-size: 24px;
  font-weight: bold;
  margin: 0;
}

.user-info {
  margin-left: auto;
}

.username {
  color: white;
  cursor: pointer;
  font-size: 14px;
  padding: 8px 12px;
  border-radius: 4px;
  transition: background-color 0.3s;
}

.username:hover {
  background-color: rgba(255, 255, 255, 0.1);
}

.main-container {
  flex: 1;
  overflow: hidden;
}

.sidebar {
  background-color: #304156;
  overflow-y: auto;
}

.menu {
  border-right: none;
  height: 100%;
}

.content {
  background-color: #f0f2f5;
  padding: 20px;
  overflow-y: auto;
}
</style>
