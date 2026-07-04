<template>
  <div class="price-list">
    <el-card class="box-card">
      <div slot="header" class="card-header">
        <span>行情列表</span>
        <div>
          <el-button type="success" size="small" @click="handleImport">
            <i class="el-icon-upload2"></i> 批量导入
          </el-button>
        </div>
      </div>

      <!-- 查询条件 -->
      <el-form :inline="true" :model="searchForm" class="search-form">
        <el-form-item label="产品名称">
          <el-input v-model="searchForm.productName" placeholder="请输入产品名称" clearable></el-input>
        </el-form-item>
        <el-form-item label="产品代码">
          <el-input v-model="searchForm.productCode" placeholder="请输入产品代码" clearable></el-input>
        </el-form-item>
        <el-form-item label="日期">
          <el-date-picker
            v-model="searchForm.priceDate"
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
        :data="priceList"
        border
        stripe
        v-loading="loading"
        style="width: 100%"
      >
        <el-table-column prop="id" label="ID" width="80" align="center"></el-table-column>
        <el-table-column prop="productName" label="产品名称" min-width="150"></el-table-column>
        <el-table-column prop="productCode" label="产品代码" width="120" align="center"></el-table-column>
        <el-table-column prop="priceDate" label="日期" width="120" align="center"></el-table-column>
        <el-table-column prop="closePrice" label="收盘价" width="120" align="right">
          <template slot-scope="scope">
            <span v-if="scope.row.closePrice" style="color: #409EFF; font-weight: bold;">¥{{ scope.row.closePrice }}</span>
            <span v-else>-</span>
          </template>
        </el-table-column>
        <el-table-column prop="volume" label="成交量" width="150" align="right">
          <template slot-scope="scope">
            <span v-if="scope.row.volume">{{ scope.row.volume }}</span>
            <span v-else>-</span>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="180" fixed="right" align="center">
          <template slot-scope="scope">
            <el-button size="mini" type="primary" @click="handleEdit(scope.row)">编辑</el-button>
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

    <!-- 编辑弹窗 -->
    <el-dialog
      title="编辑行情"
      :visible.sync="dialogVisible"
      width="500px"
      @close="handleDialogClose"
    >
      <el-form :model="priceForm" :rules="priceRules" ref="priceForm" label-width="80px">
        <el-form-item label="产品名称">
          <el-input v-model="priceForm.productName" disabled></el-input>
        </el-form-item>
        <el-form-item label="产品代码">
          <el-input v-model="priceForm.productCode" disabled></el-input>
        </el-form-item>
        <el-form-item label="日期" prop="priceDate">
          <el-date-picker
            v-model="priceForm.priceDate"
            type="date"
            placeholder="选择日期"
            value-format="yyyy-MM-dd"
            style="width: 100%;"
          ></el-date-picker>
        </el-form-item>
        <el-form-item label="收盘价" prop="closePrice">
          <el-input-number v-model="priceForm.closePrice" :precision="2" :step="0.01" style="width: 100%;"></el-input-number>
        </el-form-item>
        <el-form-item label="成交量" prop="volume">
          <el-input-number v-model="priceForm.volume" :precision="0" :step="1" style="width: 100%;"></el-input-number>
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmit" :loading="submitLoading">确定</el-button>
      </div>
    </el-dialog>

    <!-- 批量导入对话框 -->
    <el-dialog
      title="批量导入行情"
      :visible.sync="importDialogVisible"
      width="600px"
    >
      <el-upload
        ref="upload"
        class="upload-demo"
        drag
        action="/api/product-prices/import"
        :on-success="handleImportSuccess"
        :on-error="handleImportError"
        :before-upload="beforeUpload"
        :auto-upload="false"
        accept=".xlsx,.xls"
      >
        <i class="el-icon-upload"></i>
        <div class="el-upload__text">将文件拖到此处，或<em>点击上传</em></div>
        <div slot="tip" class="el-upload__tip">
          只能上传 xlsx/xls 文件
        </div>
      </el-upload>
      
      <div class="import-tips">
        <p><strong>Excel 格式说明：</strong></p>
        <p>1. 产品代码（必填）</p>
        <p>2. 日期（必填，支持格式：yyyy-MM-dd、yyyy/MM/dd、yyyy/M/d）</p>
        <p>3. 收盘价（必填）</p>
        <p>4. 开盘价（可选）</p>
        <p>5. 最高价（可选）</p>
        <p>6. 最低价（可选）</p>
        <p>7. 成交量（可选）</p>
        <p style="color: #E6A23C;">注意：如果日期已存在，将覆盖原有数据</p>
        <p style="margin-top: 10px;">
          <el-button type="text" size="small" @click="downloadTemplate">
            <i class="el-icon-download"></i> 下载导入模板
          </el-button>
        </p>
      </div>
      
      <div slot="footer" class="dialog-footer">
        <el-button @click="importDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitImport" :loading="importLoading">开始导入</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import request from '@/utils/request';

export default {
  name: 'PriceList',
  data() {
    return {
      loading: false,
      priceList: [],
      searchForm: {
        productName: '',
        productCode: '',
        priceDate: ''
      },
      pagination: {
        currentPage: 1,
        pageSize: 10,
        total: 0
      },
      dialogVisible: false,
      submitLoading: false,
      importDialogVisible: false,
      importLoading: false,
      priceForm: {
        id: null,
        productId: null,
        productName: '',
        productCode: '',
        priceDate: '',
        closePrice: null,
        volume: null
      },
      priceRules: {
        priceDate: [
          { required: true, message: '请选择价格日期', trigger: 'change' }
        ],
        closePrice: [
          { required: true, message: '请输入收盘价', trigger: 'blur' }
        ]
      }
    };
  },
  created() {
    this.fetchPriceList();
  },
  methods: {
    async fetchPriceList() {
      this.loading = true;
      try {
        const params = {
          pageNum: this.pagination.currentPage,
          pageSize: this.pagination.pageSize,
          ...this.searchForm
        };
        const response = await request.get('/product-prices/list', { params });
        this.priceList = response.data.list || [];
        this.pagination.total = response.data.total || 0;
      } catch (error) {
        this.$message.error('获取行情列表失败');
        console.error(error);
      } finally {
        this.loading = false;
      }
    },
    handleSearch() {
      this.pagination.currentPage = 1;
      this.fetchPriceList();
    },
    handleReset() {
      this.searchForm = {
        productName: '',
        productCode: '',
        priceDate: ''
      };
      this.pagination.currentPage = 1;
      this.fetchPriceList();
    },
    handlePageChange(page) {
      this.pagination.currentPage = page;
      this.fetchPriceList();
    },
    handleEdit(row) {
      this.priceForm = {
        id: row.id,
        productId: row.productId,
        productName: row.productName,
        productCode: row.productCode,
        priceDate: row.priceDate,
        closePrice: row.closePrice,
        volume: row.volume
      };
      this.dialogVisible = true;
    },
    async handleSubmit() {
      this.$refs.priceForm.validate(async (valid) => {
        if (valid) {
          this.submitLoading = true;
          try {
            await request.put('/product-prices', this.priceForm);
            this.$message.success('更新成功');
            this.dialogVisible = false;
            this.fetchPriceList();
          } catch (error) {
            this.$message.error('更新失败');
            console.error(error);
          } finally {
            this.submitLoading = false;
          }
        }
      });
    },
    handleDelete(row) {
      this.$confirm('确认删除该行情记录吗？', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(async () => {
        try {
          await request.delete(`/product-prices/${row.id}`);
          this.$message.success('删除成功');
          this.fetchPriceList();
        } catch (error) {
          this.$message.error('删除失败');
          console.error(error);
        }
      }).catch(() => {});
    },
    handleDialogClose() {
      this.$refs.priceForm.resetFields();
      this.priceForm = {
        id: null,
        productId: null,
        productName: '',
        productCode: '',
        priceDate: '',
        closePrice: null,
        volume: null
      };
    },
    // 打开导入对话框
    handleImport() {
      this.importDialogVisible = true;
    },
    // 上传前验证
    beforeUpload(file) {
      const isExcel = file.type === 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet' || 
                      file.type === 'application/vnd.ms-excel';
      if (!isExcel) {
        this.$message.error('只能上传 Excel 文件！');
        return false;
      }
      return true;
    },
    // 提交导入
    submitImport() {
      this.$refs.upload.submit();
      this.importLoading = true;
    },
    // 导入成功
    handleImportSuccess(response) {
      this.importLoading = false;
      if (response.code === 200) {
        this.$message.success(response.data);
        this.importDialogVisible = false;
        this.fetchPriceList();
      } else {
        this.$message.error(response.message || '导入失败');
      }
    },
    // 导入失败
    handleImportError() {
      this.importLoading = false;
      this.$message.error('导入失败，请检查文件格式');
    },
    // 下载导入模板
    downloadTemplate() {
      window.open('/api/product-prices/template', '_blank');
    }
  }
};
</script>

<style scoped>
.price-list {
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

.import-tips {
  margin-top: 10px;
  padding: 10px;
  background-color: #f5f7fa;
  border-radius: 4px;
  font-size: 12px;
  color: #606266;
}

.import-tips p {
  margin: 5px 0;
}
</style>
