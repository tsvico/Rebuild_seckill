import axios from 'axios'
import store from '@/store'
import qs from 'qs'

const service = axios.create({
  baseURL: 'http://localhost:8666',
  timeout: 5000,
  // 修改为form Data传参
  headers: {
    'Content-Type': 'application/x-www-form-urlencoded'
  }
})

service.interceptors.request.use(
  config => {
    // 转为formdata数据格式
    config.data = qs.stringify(config.data)
    if (store.state.user.token) {
      config.headers.Authorization = `${store.state.user.token}`
    }
    return config
  }, error => {
    console.log(error)
    return Promise.reject(error)
  })

service.interceptors.response.use(response => {
  return Promise.resolve(response)
}, error => {
  let httpError
  // 当响应异常时做一些处理
  if (error && error.response) {
    switch (error.response.status) {
      case 400: error.message = '请求错误(400)'; break
      case 401: error.message = '未授权，请重新登录(401)'; break
      case 403: error.message = '拒绝访问(403)'; break
      case 404: error.message = '请求出错(404)'; break
      case 408: error.message = '请求超时(408)'; break
      case 500: error.message = '服务器错误(500)'; break
      case 501: error.message = '服务未实现(501)'; break
      case 502: error.message = '网络错误(502)'; break
      case 503: error.message = '服务不可用(503)'; break
      case 504: error.message = '网络超时(504)'; break
      case 505: error.message = 'HTTP版本不受支持(505)'; break
      default: error.message = `连接出错(${error.response.status})!`
    }
    httpError = {
      status: error.response.status | 503,
      statusText: error.response.statusText | error.message
    }
  } else {
    error.message = '连接服务器失败!'
    httpError = {
      status: 503,
      statusText: error.message
    }
  }

  console.log('异常2')
  store.dispatch('http/setHttpError', httpError).then(() => {
    return Promise.reject(error)
  })
})

export default service
