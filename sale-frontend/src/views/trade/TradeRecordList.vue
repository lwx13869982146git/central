<template>
  <div class="trade-record-list">
    <el-card class="box-card">
      <div slot="header" class="card-header">
        <span>交易记录</span>
      </div>

      <!-- 查询条件 -->
      <el-form :inline="true" :model="searchForm" class="search-form">
        <el-form-item label="产品名称">
          <el-input v-model="searchForm.productName" placeholder="请输入产品名称" clearable></el-input>
        </el-form-item>
        <el-form-item label="产品代码">
          <el-input v-model="searchForm.productCode" placeholder="请输入产品代码" clearable></el-input>
        </el-form-item>
        <el-form-item label="交易类型">
          <el-select v-model="searchForm.tradeType" placeholder="请选择交易类型" clearable style="width: 150px;">
            <el-option label="买入" value="BUY"></el-option>
            <el-option label="卖出" value="SELL"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="交易日期">
          <el-date-picker
            v-model="searchForm.tradeDate"
            type="date"
            placeholder="选择日期"
            value-format="yyyy-MM-dd"
            style="width: 200px;"
          ></el-date-picker>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" icon="el-icon-search" @click="handleSearch">查询</el-button>
          <el-button icon="el-icon-refresh" @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>

      <!-- 数据表格 -->
      <el-table
        :data="tradeRecordList"
        border
        stripe
        v-loading="loading"
        style="width: 100%"
      >
        <el-table-column prop="id" label="ID" width="80" align="center"></el-table-column>
        <el-table-column prop="productName" label="产品名称" min-width="150"></el-table-column>
        <el-table-column prop="productCode" label="产品代码" width="120" align="center"></el-table-column>
        <el-table-column prop="tradeDate" label="交易日期" width="120" align="center"></el-table-column>
        <el-table-column prop="tradeType" label="交易类型" width="100" align="center">
          <template slot-scope="scope">
            <el-tag :type="scope.row.tradeType === 'BUY' ? 'success' : 'danger'">
              {{ scope.row.tradeType === 'BUY' ? '买入' : '卖出' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="netValue" label="净值" width="120" align="right">
          <template slot-scope="scope">
            ¥{{ scope.row.netValue || '-' }}
          </template>
        </el-table-column>
        <el-table-column prop="shares" label="份额" width="120" align="right">
          <template slot-scope="scope">
            {{ scope.row.shares || '-' }}
          </template>
        </el-table-column>
        <el-table-column prop="amount" label="金额" width="120" align="right">
          <template slot-scope="scope">
            <span :style="{ color: scope.row.tradeType === 'BUY' ? '#F56C6C' : '#67C23A', fontWeight: 'bold' }">
              ¥{{ scope.row.amount || '-' }}
            </span>
          </template>
        </el-table-column>
        <el-table-column prop="notes" label="备注" min-width="150" show-overflow-tooltip></el-table-column>
        <el-table-column label="操作" width="150" fixed="right" align="center">
          <template slot-scope="scope">
            <el-button size="mini" type="danger" @click="handleDelete(scope.row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>

      <!-- 分页 -->
      <el-pagination
        @current-change="handlePageChange"
        :current-page="pagination.currentPage"
        :page-size="pagination.pageSize"
        :total="pagination.total"
        layout="total, prev, pager, next"
        style="margin-top: 20px; text-align: right;"
      ></el-pagination>
    </el-card>
  </div>
</template>

<script>
import request from '@/utils/request';

export default {
  name: 'TradeRecordList',
  data() {
    return {
      loading: false,
      tradeRecordList: [],
      searchForm: {
        productName: '',
        productCode: '',
        tradeType: '',
        tradeDate: ''
      },
      pagination: {
        currentPage: 1,
        pageSize: 10,
        total: 0
      }
    };
  },
  created() {
    this.fetchTradeRecordList();
  },
  methods: {
    async fetchTradeRecordList() {
      this.loading = true;
      try {
        const params = {
          pageNum: this.pagination.currentPage,
          pageSize: this.pagination.pageSize,
          ...this.searchForm
        };
        const response = await request.get('/trade-records/list', { params });
        this.tradeRecordList = response.data.list || [];
        this.pagination.total = response.data.total || 0;
      } catch (error) {
        this.$message.error('获取交易记录列表失败');
        console.error(error);
      } finally {
        this.loading = false;
      }
    },
    handleSearch() {
      this.pagination.currentPage = 1;
      this.fetchTradeRecordList();
    },
    handleReset() {
      this.searchForm = {
        productName: '',
        productCode: '',
        tradeType: '',
        tradeDate: ''
      };
      this.pagination.currentPage = 1;
      this.fetchTradeRecordList();
    },
    handlePageChange(page) {
      this.pagination.currentPage = page;
      this.fetchTradeRecordList();
    },
    handleDelete(row) {
      this.$confirm('确认删除该交易记录吗？', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(async () => {
        try {
          await request.delete(`/trade-records/${row.id}`);
          this.$message.success('删除成功');
          this.fetchTradeRecordList();
        } catch (error) {
          this.$message.error('删除失败');
          console.error(error);
        }
      }).catch(() => {});
    }
  }
};
</script>

<style scoped>
.trade-record-list {
  padding: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.search-form {
  margin-bottom: 20px;
}
</style>
