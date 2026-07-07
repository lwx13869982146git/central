<template>
  <div class="product-list">
    <el-card class="box-card">
      <div slot="header" class="card-header">
        <span>产品列表</span>
        <el-button type="primary" size="small" @click="handleAdd" style="float: right;">
          <i class="el-icon-plus"></i> 新增产品
        </el-button>
      </div>

      <!-- 产品表格 -->
      <el-table
        :data="productList"
        style="width: 100%"
        v-loading="loading"
        border
      >
        <el-table-column prop="id" label="ID" width="80"></el-table-column>
        <el-table-column prop="productName" label="产品名称" min-width="150"></el-table-column>
        <el-table-column prop="productCode" label="产品代码" width="120"></el-table-column>
        <el-table-column prop="productType" label="产品类型" width="100">
          <template slot-scope="scope">
            <el-tag :type="getTypeColor(scope.row.productType)">
              {{ getTypeName(scope.row.productType) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="currentPrice" label="当前价格" width="120">
          <template slot-scope="scope">
            ¥{{ scope.row.currentPrice || '-' }}
          </template>
        </el-table-column>
        <el-table-column prop="holdingPrice" label="持有价格" width="120">
          <template slot-scope="scope">
            ¥{{ scope.row.holdingPrice || '-' }}
          </template>
        </el-table-column>
        <el-table-column prop="quantity" label="持有数量" width="120">
          <template slot-scope="scope">
            {{ scope.row.quantity || '-' }}
          </template>
        </el-table-column>
        <el-table-column prop="profitLoss" label="盈亏" width="120">
          <template slot-scope="scope">
            <span :style="{ color: scope.row.profitLoss >= 0 ? '#67C23A' : '#F56C6C' }">
              {{ scope.row.profitLoss ? (scope.row.profitLoss >= 0 ? '+' : '') + scope.row.profitLoss : '-' }}
            </span>
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="100">
          <template slot-scope="scope">
            <el-tag :type="getStatusType(scope.row.status)">
              {{ getStatusName(scope.row.status) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="400" fixed="right">
          <template slot-scope="scope">
            <el-button size="mini" @click="handleView(scope.row)">
              <i class="el-icon-view"></i> 查看
            </el-button>
            <el-button size="mini" type="warning" @click="handleTrade(scope.row)">
              <i class="el-icon-exchange"></i> 交易
            </el-button>
            <el-button size="mini" type="success" @click="handlePrice(scope.row)">
              <i class="el-icon-data-line"></i> 行情
            </el-button>
            <el-button size="mini" type="primary" @click="handleEdit(scope.row)">编辑</el-button>
            <el-button size="mini" type="danger" @click="handleDelete(scope.row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <!-- 新增/编辑产品对话框 -->
    <el-dialog
      :title="dialogTitle"
      :visible.sync="dialogVisible"
      width="600px"
      @close="handleDialogClose"
    >
      <el-form :model="productForm" :rules="rules" ref="productForm" label-width="100px">
        <el-form-item label="产品名称" prop="productName">
          <el-input v-model="productForm.productName" placeholder="请输入产品名称"></el-input>
        </el-form-item>
        <el-form-item label="产品代码" prop="productCode">
          <el-input v-model="productForm.productCode" placeholder="请输入产品代码"></el-input>
        </el-form-item>
        <el-form-item label="产品类型" prop="productType">
          <el-select v-model="productForm.productType" placeholder="请选择产品类型" style="width: 100%;">
            <el-option label="股票" value="stock"></el-option>
            <el-option label="基金" value="fund"></el-option>
            <el-option label="债券" value="bond"></el-option>
            <el-option label="ETF" value="etf"></el-option>
            <el-option label="期权" value="option"></el-option>
            <el-option label="期货" value="future"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="所属市场" prop="market">
          <el-input v-model="productForm.market" placeholder="请输入所属市场"></el-input>
        </el-form-item>
        <el-form-item label="持有价格" prop="holdingPrice">
          <el-input-number v-model="productForm.holdingPrice" :precision="4" :step="0.01" style="width: 100%;" disabled placeholder="持有价格不可编辑"></el-input-number>
        </el-form-item>
        <el-form-item label="持有数量" prop="quantity">
          <el-input-number v-model="productForm.quantity" :precision="4" :step="1" style="width: 100%;" disabled placeholder="持有数量不可编辑"></el-input-number>
        </el-form-item>
        <el-form-item label="目标价格" prop="targetPrice">
          <el-input-number v-model="productForm.targetPrice" :precision="4" :step="0.01" style="width: 100%;"></el-input-number>
        </el-form-item>
        <el-form-item label="止损价格" prop="stopLossPrice">
          <el-input-number v-model="productForm.stopLossPrice" :precision="4" :step="0.01" style="width: 100%;"></el-input-number>
        </el-form-item>
        <el-form-item label="备注" prop="notes">
          <el-input v-model="productForm.notes" type="textarea" :rows="3" placeholder="请输入备注"></el-input>
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmit" :loading="submitLoading">确定</el-button>
      </div>
    </el-dialog>
    
    <!-- 行情价格对话框 -->
    <el-dialog
      title="添加行情价格"
      :visible.sync="priceDialogVisible"
      width="450px"
      @close="handlePriceDialogClose"
    >
      <el-form :model="priceForm" :rules="priceRules" ref="priceForm" label-width="80px">
        <el-form-item label="产品名称">
          <el-input v-model="currentProduct.productName" disabled></el-input>
        </el-form-item>
        <el-form-item label="产品代码">
          <el-input v-model="currentProduct.productCode" disabled></el-input>
        </el-form-item>
        <el-form-item label="价格日期" prop="priceDate">
          <el-date-picker
            v-model="priceForm.priceDate"
            type="date"
            placeholder="选择日期"
            style="width: 100%;"
            value-format="yyyy-MM-dd"
          ></el-date-picker>
        </el-form-item>
        <el-form-item label="收盘价" prop="closePrice">
          <el-input-number v-model="priceForm.closePrice" :precision="4" :step="0.01" style="width: 100%;" placeholder="请输入收盘价"></el-input-number>
        </el-form-item>
        <el-form-item label="成交量" prop="volume">
          <el-input-number v-model="priceForm.volume" :precision="4" :step="1" style="width: 100%;" placeholder="请输入成交量（可选）"></el-input-number>
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button @click="priceDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handlePriceSubmit" :loading="priceSubmitLoading">确定</el-button>
      </div>
    </el-dialog>

    <!-- 交易记录对话框 -->
    <el-dialog
      title="添加交易记录"
      :visible.sync="tradeDialogVisible"
      width="500px"
      @close="handleTradeDialogClose"
    >
      <el-form :model="tradeForm" :rules="tradeRules" ref="tradeForm" label-width="80px">
        <el-form-item label="产品名称">
          <el-input v-model="currentProduct.productName" disabled></el-input>
        </el-form-item>
        <el-form-item label="产品代码">
          <el-input v-model="currentProduct.productCode" disabled></el-input>
        </el-form-item>
        <el-form-item label="交易日期" prop="tradeDate">
          <el-date-picker
            v-model="tradeForm.tradeDate"
            type="date"
            placeholder="选择日期"
            style="width: 100%;"
            value-format="yyyy-MM-dd"
          ></el-date-picker>
        </el-form-item>
        <el-form-item label="交易类型" prop="tradeType">
          <el-radio-group v-model="tradeForm.tradeType">
            <el-radio label="BUY">买入</el-radio>
            <el-radio label="SELL">卖出</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="净值" prop="netValue">
          <el-input-number v-model="tradeForm.netValue" :precision="4" :step="0.0001" style="width: 100%;" placeholder="请输入净值"></el-input-number>
        </el-form-item>
        <el-form-item label="份额" prop="shares">
          <el-input-number v-model="tradeForm.shares" :precision="4" :step="1" style="width: 100%;" placeholder="请输入份额"></el-input-number>
        </el-form-item>
        <el-form-item label="备注">
          <el-input v-model="tradeForm.notes" type="textarea" :rows="3" placeholder="请输入备注（可选）"></el-input>
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button @click="tradeDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleTradeSubmit" :loading="tradeSubmitLoading">确定</el-button>
      </div>
    </el-dialog>

    <!-- 查看产品详情对话框 -->
    <el-dialog
      title="产品详情"
      :visible.sync="viewDialogVisible"
      width="800px"
    >
      <div v-if="currentProduct">
        <!-- 价格信息 -->
        <el-descriptions :column="2" border style="margin-bottom: 20px;">
          <el-descriptions-item label="产品名称">{{ currentProduct.productName }}</el-descriptions-item>
          <el-descriptions-item label="产品代码">{{ currentProduct.productCode }}</el-descriptions-item>
          <el-descriptions-item label="当前价格">
            <span style="color: #409EFF; font-weight: bold;">¥{{ currentProduct.currentPrice || '-' }}</span>
          </el-descriptions-item>
          <el-descriptions-item label="持有价格">
            <span style="color: #E6A23C; font-weight: bold;">¥{{ currentProduct.holdingPrice || '-' }}</span>
          </el-descriptions-item>
          <el-descriptions-item label="目标价格">
            <span style="color: #67C23A; font-weight: bold;">¥{{ currentProduct.targetPrice || '-' }}</span>
          </el-descriptions-item>
          <el-descriptions-item label="止损价格">
            <span style="color: #F56C6C; font-weight: bold;">¥{{ currentProduct.stopLossPrice || '-' }}</span>
          </el-descriptions-item>
          <el-descriptions-item label="持有数量">{{ currentProduct.quantity || '-' }}</el-descriptions-item>
          <el-descriptions-item label="盈亏">
            <span :style="{ color: currentProduct.profitLoss >= 0 ? '#67C23A' : '#F56C6C', fontWeight: 'bold' }">
              {{ currentProduct.profitLoss ? (currentProduct.profitLoss >= 0 ? '+' : '') + currentProduct.profitLoss : '-' }}
            </span>
          </el-descriptions-item>
        </el-descriptions>

        <!-- 行情折线图 -->
        <div style="margin-top: 20px;">
          <h4 style="margin-bottom: 10px;">行情走势</h4>
          <div id="priceChart" style="width: 100%; height: 400px;"></div>
        </div>
      </div>
      <div slot="footer" class="dialog-footer">
        <el-button @click="viewDialogVisible = false">关闭</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import request from '@/utils/request';

export default {
  name: 'ProductList',
  data() {
    return {
      productList: [],
      loading: false,
      dialogVisible: false,
      dialogTitle: '新增产品',
      submitLoading: false,
      priceDialogVisible: false,
      priceSubmitLoading: false,
      tradeDialogVisible: false,
      tradeSubmitLoading: false,
      viewDialogVisible: false,
      currentProduct: {},
      productForm: {
        id: null,
        productName: '',
        productCode: '',
        productType: '',
        market: '',
        holdingPrice: null,
        quantity: null,
        targetPrice: null,
        stopLossPrice: null,
        notes: '',
        status: 'active'
      },
      priceForm: {
        productId: null,
        productCode: '',
        priceDate: '',
        closePrice: null,
        volume: null
      },
      rules: {
        productName: [
          { required: true, message: '请输入产品名称', trigger: 'blur' }
        ],
        productCode: [
          { required: true, message: '请输入产品代码', trigger: 'blur' }
        ],
        productType: [
          { required: true, message: '请选择产品类型', trigger: 'change' }
        ]
      },
      priceRules: {
        priceDate: [
          { required: true, message: '请选择价格日期', trigger: 'change' }
        ],
        closePrice: [
          { required: true, message: '请输入收盘价', trigger: 'blur' }
        ]
      },
      tradeForm: {
        productId: null,
        productCode: '',
        tradeDate: '',
        tradeType: 'BUY',
        netValue: null,
        shares: null,
        notes: ''
      },
      tradeRules: {
        tradeDate: [
          { required: true, message: '请选择交易日期', trigger: 'change' }
        ],
        tradeType: [
          { required: true, message: '请选择交易类型', trigger: 'change' }
        ],
        netValue: [
          { required: true, message: '请输入净值', trigger: 'blur' }
        ],
        shares: [
          { required: true, message: '请输入份额', trigger: 'blur' }
        ]
      }
    };
  },
  created() {
    this.fetchProductList();
  },
  methods: {
    // 获取产品列表
    async fetchProductList() {
      this.loading = true;
      try {
        const response = await request.get('/products');
        this.productList = response.data || [];
      } catch (error) {
        console.error('获取产品列表失败:', error);
        this.$message.error('获取产品列表失败');
      } finally {
        this.loading = false;
      }
    },

    // 新增产品
    handleAdd() {
      this.dialogTitle = '新增产品';
      this.dialogVisible = true;
    },

    // 编辑产品
    handleEdit(row) {
      this.dialogTitle = '编辑产品';
      this.productForm = { ...row };
      this.dialogVisible = true;
    },

    // 删除产品
    handleDelete(row) {
      this.$confirm('确认删除该产品吗?', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(async () => {
        try {
          await request.delete(`/products/${row.id}`);
          this.$message.success('删除成功');
          this.fetchProductList();
        } catch (error) {
          console.error('删除失败:', error);
          this.$message.error('删除失败');
        }
      }).catch(() => {});
    },

    // 提交表单
    handleSubmit() {
      this.$refs.productForm.validate(async (valid) => {
        if (valid) {
          this.submitLoading = true;
          try {
            if (this.productForm.id) {
              // 编辑
              await request.put('/products', this.productForm);
            } else {
              // 新增
              await request.post('/products', this.productForm);
            }
            
            this.$message.success(this.productForm.id ? '编辑成功' : '新增成功');
            this.dialogVisible = false;
            this.fetchProductList();
          } catch (error) {
            console.error('操作失败:', error);
            this.$message.error('操作失败');
          } finally {
            this.submitLoading = false;
          }
        }
      });
    },

    // 关闭对话框
    handleDialogClose() {
      this.$refs.productForm.resetFields();
      this.productForm = {
        id: null,
        productName: '',
        productCode: '',
        productType: '',
        market: '',
        holdingPrice: null,
        quantity: null,
        targetPrice: null,
        stopLossPrice: null,
        notes: '',
        status: 'active'
      };
    },

    // 打开行情对话框
    handlePrice(row) {
      this.currentProduct = row;
      this.priceForm = {
        productId: row.id,
        productCode: row.productCode,
        priceDate: '',
        openPrice: null,
        closePrice: null,
        highPrice: null,
        lowPrice: null,
        volume: null
      };
      this.priceDialogVisible = true;
    },

    // 关闭行情对话框
    handlePriceDialogClose() {
      if (this.$refs.priceForm) {
        this.$refs.priceForm.resetFields();
      }
      this.priceForm = {
        productId: null,
        productCode: '',
        priceDate: '',
        closePrice: null,
        volume: null
      };
      this.currentProduct = {};
    },

    // 提交行情价格
    async handlePriceSubmit() {
      this.$refs.priceForm.validate(async (valid) => {
        if (valid) {
          this.priceSubmitLoading = true;
          try {
            await request.post('/product-prices', this.priceForm);
            this.$message.success('添加行情价格成功');
            this.priceDialogVisible = false;
          } catch (error) {
            console.error('添加失败:', error);
            this.$message.error(error.message || '添加失败');
          } finally {
            this.priceSubmitLoading = false;
          }
        }
      });
    },

    // 打开交易对话框
    handleTrade(row) {
      this.currentProduct = row;
      this.tradeForm = {
        productId: row.id,
        productCode: row.productCode,
        tradeDate: '',
        tradeType: 'BUY',
        netValue: null,
        shares: null,
        notes: ''
      };
      this.tradeDialogVisible = true;
    },

    // 关闭交易对话框
    handleTradeDialogClose() {
      if (this.$refs.tradeForm) {
        this.$refs.tradeForm.resetFields();
      }
      this.tradeForm = {
        productId: null,
        productCode: '',
        tradeDate: '',
        tradeType: 'BUY',
        netValue: null,
        shares: null,
        notes: ''
      };
      this.currentProduct = {};
    },

    // 提交交易记录
    async handleTradeSubmit() {
      this.$refs.tradeForm.validate(async (valid) => {
        if (valid) {
          this.tradeSubmitLoading = true;
          try {
            await request.post('/trade-records', this.tradeForm);
            this.$message.success('添加交易记录成功');
            this.tradeDialogVisible = false;
          } catch (error) {
            console.error('添加失败:', error);
            this.$message.error(error.message || '添加失败');
          } finally {
            this.tradeSubmitLoading = false;
          }
        }
      });
    },

    // 获取类型名称
    getTypeName(type) {
      const typeMap = {
        stock: '股票',
        fund: '基金',
        bond: '债券',
        etf: 'ETF',
        option: '期权',
        future: '期货'
      };
      return typeMap[type] || type;
    },

    // 获取类型颜色
    getTypeColor(type) {
      const colorMap = {
        stock: '',
        fund: 'success',
        bond: 'warning',
        etf: 'info',
        option: 'danger',
        future: ''
      };
      return colorMap[type] || '';
    },

    // 获取状态名称
    getStatusName(status) {
      const statusMap = {
        active: '活跃',
        inactive: '非活跃',
        sold: '已卖出'
      };
      return statusMap[status] || status;
    },

    // 获取状态类型
    getStatusType(status) {
      const typeMap = {
        active: 'success',
        inactive: 'info',
        sold: 'warning'
      };
      return typeMap[status] || '';
    },

    // 查看产品详情
    handleView(row) {
      this.currentProduct = { ...row };
      this.viewDialogVisible = true;
      // 延迟加载图表，确保 DOM 已渲染
      this.$nextTick(() => {
        this.loadPriceChart(row.id);
      });
    },

    // 加载行情折线图
    async loadPriceChart(productId) {
      try {
        const response = await request.get(`/product-prices/product/${productId}`);
        const prices = response.data || [];
        
        if (!prices || prices.length === 0) {
          return;
        }

        // 按日期排序
        prices.sort((a, b) => new Date(a.priceDate) - new Date(b.priceDate));

        // 准备图表数据
        const dates = prices.map(p => p.priceDate);
        const closePrices = prices.map(p => p.closePrice);

        // 使用 ECharts 绘制折线图
        this.drawChart(dates, closePrices);
      } catch (error) {
        console.error('加载行情数据失败:', error);
      }
    },

    // 绘制图表
    drawChart(dates, prices) {
      // 检查是否已经引入 ECharts
      if (typeof echarts === 'undefined') {
        // 动态加载 ECharts
        this.loadECharts().then(() => {
          this.initChart(dates, prices);
        });
      } else {
        this.initChart(dates, prices);
      }
    },

    // 动态加载 ECharts
    loadECharts() {
      return new Promise((resolve, reject) => {
        if (typeof echarts !== 'undefined') {
          resolve();
          return;
        }
        const script = document.createElement('script');
        script.src = 'https://cdn.jsdelivr.net/npm/echarts@5.4.3/dist/echarts.min.js';
        script.onload = resolve;
        script.onerror = reject;
        document.head.appendChild(script);
      });
    },

    // 初始化图表
    initChart(dates, prices) {
      const chartDom = document.getElementById('priceChart');
      if (!chartDom) return;

      const myChart = echarts.init(chartDom);
      const option = {
        title: {
          text: '收盘价走势',
          left: 'center'
        },
        tooltip: {
          trigger: 'axis',
          formatter: function(params) {
            const data = params[0];
            return `${data.name}<br/>收盘价: ¥${data.value}`;
          }
        },
        grid: {
          left: '3%',
          right: '4%',
          bottom: '3%',
          containLabel: true
        },
        xAxis: {
          type: 'category',
          data: dates,
          boundaryGap: false
        },
        yAxis: {
          type: 'value',
          axisLabel: {
            formatter: '¥{value}'
          }
        },
        series: [
          {
            name: '收盘价',
            type: 'line',
            data: prices,
            smooth: true,
            lineStyle: {
              color: '#409EFF',
              width: 2
            },
            areaStyle: {
              color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
                {
                  offset: 0,
                  color: 'rgba(64, 158, 255, 0.3)'
                },
                {
                  offset: 1,
                  color: 'rgba(64, 158, 255, 0.1)'
                }
              ])
            },
            itemStyle: {
              color: '#409EFF'
            }
          }
        ]
      };

      myChart.setOption(option);

      // 响应式调整
      window.addEventListener('resize', () => {
        myChart.resize();
      });
    }
  }
};
</script>

<style scoped>
.product-list {
  height: 100%;
}

.box-card {
  height: 100%;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
</style>
