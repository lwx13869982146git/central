import axios from 'axios';

// 创建 axios 实例
const request = axios.create({
  baseURL: '/api', // 使用 webpack devServer 代理
  timeout: 10000, // 请求超时时间
  headers: {
    'Content-Type': 'application/json;charset=UTF-8'
  }
});

// 创建用于认证的 axios 实例（指向 security 服务）
const authRequest = axios.create({
  baseURL: '/auth-api', // 认证服务 API
  timeout: 10000,
  headers: {
    'Content-Type': 'application/json;charset=UTF-8'
  }
});

// 请求拦截器
request.interceptors.request.use(
  config => {
    // 添加 token 认证信息
    const token = localStorage.getItem('token');
    if (token) {
      config.headers['Authorization'] = `Bearer ${token}`;
    }
    return config;
  },
  error => {
    console.error('请求错误:', error);
    return Promise.reject(error);
  }
);

// 响应拦截器
request.interceptors.response.use(
  response => {
    const res = response.data;
    
    // 如果返回的状态码不是 200，则认为是错误
    if (res.code !== 200) {
      console.error('响应错误:', res.message || 'Error');
      // 可以根据具体的错误码进行统一处理
      return Promise.reject(new Error(res.message || 'Error'));
    } else {
      return res;
    }
  },
  error => {
    console.error('响应错误:', error.message);
    // 处理常见的 HTTP 错误状态码
    if (error.response) {
      switch (error.response.status) {
        case 401:
          console.error('未授权，请重新登录');
          // 清除 token 并跳转到登录页
          localStorage.removeItem('token');
          localStorage.removeItem('userInfo');
          window.location.href = '/login';
          break;
        case 403:
          console.error('拒绝访问');
          break;
        case 404:
          console.error('请求地址出错');
          break;
        case 500:
          console.error('服务器内部错误');
          break;
        default:
          console.error(`连接错误${error.response.status}`);
      }
    } else if (error.request) {
      console.error('网络异常，请检查网络连接');
    }
    return Promise.reject(error);
  }
);

export default request;
export { authRequest };
