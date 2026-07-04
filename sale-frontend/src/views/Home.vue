<template>
  <div class="home-container">
    <!-- 欢迎卡片 -->
    <el-card class="welcome-card" shadow="hover">
      <div class="welcome-content">
        <h1 class="welcome-title">欢迎使用交易系统</h1>
        <p class="welcome-desc">这是一个专业的金融产品管理平台，帮助您更好地管理股票、基金等投资产品</p>
      </div>
    </el-card>

    <!-- 快捷功能卡片 -->
    <el-row :gutter="20" class="feature-cards">
      <el-col :span="8">
        <el-card class="feature-card" shadow="hover" @click.native="goToProductList">
          <div class="feature-icon product-icon">
            <i class="el-icon-goods"></i>
          </div>
          <div class="feature-info">
            <h3>产品管理</h3>
            <p>查看和管理您的金融产品</p>
            <el-button type="primary" size="small">进入</el-button>
          </div>
        </el-card>
      </el-col>
      
      <el-col :span="8">
        <el-card class="feature-card" shadow="hover">
          <div class="feature-icon chart-icon">
            <i class="el-icon-data-analysis"></i>
          </div>
          <div class="feature-info">
            <h3>数据分析</h3>
            <p>查看投资收益分析图表</p>
            <el-button type="success" size="small">即将上线</el-button>
          </div>
        </el-card>
      </el-col>
      
      <el-col :span="8">
        <el-card class="feature-card" shadow="hover">
          <div class="feature-icon setting-icon">
            <i class="el-icon-setting"></i>
          </div>
          <div class="feature-info">
            <h3>系统设置</h3>
            <p>配置个人偏好和系统参数</p>
            <el-button type="info" size="small">即将上线</el-button>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 统计信息 -->
    <el-row :gutter="20" class="stats-cards">
      <el-col :span="6">
        <el-card class="stat-card" shadow="hover">
          <div class="stat-content">
            <div class="stat-icon" style="background-color: #409EFF;">
              <i class="el-icon-tickets"></i>
            </div>
            <div class="stat-info">
              <p class="stat-label">产品总数</p>
              <h2 class="stat-value">{{ stats.totalProducts }}</h2>
            </div>
          </div>
        </el-card>
      </el-col>
      
      <el-col :span="6">
        <el-card class="stat-card" shadow="hover">
          <div class="stat-content">
            <div class="stat-icon" style="background-color: #67C23A;">
              <i class="el-icon-success"></i>
            </div>
            <div class="stat-info">
              <p class="stat-label">活跃产品</p>
              <h2 class="stat-value">{{ stats.activeProducts }}</h2>
            </div>
          </div>
        </el-card>
      </el-col>
      
      <el-col :span="6">
        <el-card class="stat-card" shadow="hover">
          <div class="stat-content">
            <div class="stat-icon" style="background-color: #E6A23C;">
              <i class="el-icon-money"></i>
            </div>
            <div class="stat-info">
              <p class="stat-label">总盈亏</p>
              <h2 class="stat-value" :style="{ color: stats.totalProfit >= 0 ? '#67C23A' : '#F56C6C' }">
                {{ stats.totalProfit >= 0 ? '+' : '' }}{{ stats.totalProfit }}
              </h2>
            </div>
          </div>
        </el-card>
      </el-col>
      
      <el-col :span="6">
        <el-card class="stat-card" shadow="hover">
          <div class="stat-content">
            <div class="stat-icon" style="background-color: #909399;">
              <i class="el-icon-date"></i>
            </div>
            <div class="stat-info">
              <p class="stat-label">今日日期</p>
              <h2 class="stat-value">{{ currentDate }}</h2>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 最近操作记录 -->
    <el-card class="recent-card" shadow="hover">
      <div slot="header" class="card-header">
        <span><i class="el-icon-time"></i> 最近操作</span>
      </div>
      <el-table :data="recentOperations" style="width: 100%" border>
        <el-table-column prop="time" label="时间" width="180"></el-table-column>
        <el-table-column prop="type" label="操作类型" width="120">
          <template slot-scope="scope">
            <el-tag :type="getOperationType(scope.row.type)">
              {{ scope.row.type }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="product" label="产品名称" min-width="150"></el-table-column>
        <el-table-column prop="detail" label="详情" min-width="200"></el-table-column>
      </el-table>
    </el-card>
  </div>
</template>

<script>
export default {
  name: 'Home',
  data() {
    return {
      stats: {
        totalProducts: 0,
        activeProducts: 0,
        totalProfit: 0
      },
      currentDate: '',
      recentOperations: [
        { time: '2024-01-15 10:30:00', type: '新增', product: '贵州茅台', detail: '添加股票产品' },
        { time: '2024-01-14 15:20:00', type: '编辑', product: '易方达基金', detail: '更新持有数量' },
        { time: '2024-01-13 09:15:00', type: '删除', product: '某债券', detail: '移除已卖出产品' }
      ]
    };
  },
  created() {
    this.initDate();
    this.fetchStats();
  },
  methods: {
    // 初始化日期
    initDate() {
      const now = new Date();
      const year = now.getFullYear();
      const month = String(now.getMonth() + 1).padStart(2, '0');
      const day = String(now.getDate()).padStart(2, '0');
      this.currentDate = `${year}-${month}-${day}`;
    },

    // 获取统计数据
    async fetchStats() {
      try {
        const response = await this.$http.get('/products');
        if (response.code === 200) {
          const products = response.data || [];
          this.stats.totalProducts = products.length;
          this.stats.activeProducts = products.filter(p => p.status === 'active').length;
          this.stats.totalProfit = products.reduce((sum, p) => sum + (p.profitLoss || 0), 0);
        }
      } catch (error) {
        console.error('获取统计数据失败:', error);
      }
    },

    // 跳转到产品列表
    goToProductList() {
      this.$router.push('/product/list');
    },

    // 获取操作类型样式
    getOperationType(type) {
      const typeMap = {
        '新增': 'success',
        '编辑': 'warning',
        '删除': 'danger'
      };
      return typeMap[type] || 'info';
    }
  }
};
</script>

<style scoped>
.home-container {
  padding: 20px;
}

.welcome-card {
  margin-bottom: 20px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border: none;
}

.welcome-card >>> .el-card__body {
  padding: 40px;
}

.welcome-content {
  text-align: center;
  color: white;
}

.welcome-title {
  font-size: 32px;
  margin-bottom: 10px;
  font-weight: bold;
}

.welcome-desc {
  font-size: 16px;
  opacity: 0.9;
}

.feature-cards {
  margin-bottom: 20px;
}

.feature-card {
  cursor: pointer;
  transition: all 0.3s;
  height: 200px;
  display: flex;
  align-items: center;
  padding: 20px;
}

.feature-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 8px 16px rgba(0, 0, 0, 0.1);
}

.feature-icon {
  width: 80px;
  height: 80px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-right: 20px;
  font-size: 36px;
  color: white;
}

.product-icon {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
}

.chart-icon {
  background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%);
}

.setting-icon {
  background: linear-gradient(135deg, #4facfe 0%, #00f2fe 100%);
}

.feature-info h3 {
  font-size: 20px;
  margin-bottom: 8px;
  color: #303133;
}

.feature-info p {
  font-size: 14px;
  color: #909399;
  margin-bottom: 15px;
}

.stats-cards {
  margin-bottom: 20px;
}

.stat-card {
  height: 120px;
}

.stat-content {
  display: flex;
  align-items: center;
}

.stat-icon {
  width: 60px;
  height: 60px;
  border-radius: 10px;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-right: 15px;
  font-size: 28px;
  color: white;
}

.stat-info {
  flex: 1;
}

.stat-label {
  font-size: 14px;
  color: #909399;
  margin-bottom: 5px;
}

.stat-value {
  font-size: 24px;
  font-weight: bold;
  color: #303133;
  margin: 0;
}

.recent-card {
  margin-bottom: 20px;
}

.card-header {
  display: flex;
  align-items: center;
  font-size: 16px;
  font-weight: bold;
}

.card-header i {
  margin-right: 8px;
  color: #409EFF;
}
</style>
