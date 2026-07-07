import Vue from 'vue';
import VueRouter from 'vue-router';
import Layout from '@/views/Layout.vue';
import Home from '@/views/Home.vue';
import Login from '@/views/Login.vue';
import ProductList from '@/views/product/ProductList.vue';
import PriceList from '@/views/price/PriceList.vue';
import TradeRecordList from '@/views/trade/TradeRecordList.vue';

Vue.use(VueRouter);

const routes = [
  {
    path: '/login',
    name: 'Login',
    component: Login,
    meta: { title: '登录' }
  },
  {
    path: '/',
    component: Layout,
    children: [
      {
        path: '',
        name: 'Home',
        component: Home,
        meta: { title: '首页' }
      },
      {
        path: '/product/list',
        name: 'ProductList',
        component: ProductList,
        meta: { title: '产品列表' }
      },
      {
        path: '/price/list',
        name: 'PriceList',
        component: PriceList,
        meta: { title: '行情列表' }
      },
      {
        path: '/trade/record-list',
        name: 'TradeRecordList',
        component: TradeRecordList,
        meta: { title: '交易记录' }
      }
    ]
  }
];

const router = new VueRouter({
  mode: 'history',
  base: process.env.BASE_URL,
  routes
});

// 路由守卫
router.beforeEach((to, from, next) => {
  const token = localStorage.getItem('token');
  
  if (to.path === '/login') {
    // 如果已登录，跳转到首页
    if (token) {
      next('/');
    } else {
      next();
    }
  } else {
    // 需要登录的页面
    if (token) {
      next();
    } else {
      next('/login');
    }
  }
});

export default router;
