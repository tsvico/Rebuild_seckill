import router from './router'
import store from './store'
import {
  getToken
} from '@/utils/auth' // 验权

const whiteList = ['/login', '/adminlogin'] // 不重定向白名单
router.beforeEach((to, from, next) => {
  if (to.meta.title) {
    // 设置标题
    document.title = to.meta.title
  }
  if (getToken()) {
    // console.log(to)
    if (to.path === '/login') {
      next({
        path: '/'
      })
    } else if (to.path === '/adminlogin') {
      next({
        path: '/admin'
      })
    } else if (to.path.indexOf('/admin') !== -1) {
      if (store.getters.roles.length === 0) {
        store.dispatch('GetInfo').then(res => { // 拉取用户信息
          let menus = res.data.menus
          let username = res.data.username
          store.dispatch('GenerateRoutes', {
            menus,
            username
          }).then(() => { // 生成可访问的路由表
            router.addRoutes(store.getters.addRouters) // 动态添加可访问路由表
            next({
              ...to,
              replace: true
            })
          })
        }).catch(() => {
          // console.log('异常3')
          // _this.$message.error(err)
          next({
            path: '/'
          })
        })
      } else {
        next()
      }
    } else {
      if (to.matched.length === 0) {
        next('/404') // 判断此跳转路由的来源路由是否存在，存在的情况跳转到来源路由，否则跳转到404页面
      }
      next()
    }
  } else {
    if (whiteList.indexOf(to.path) !== -1) {
      next()
    } else {
      next('/login')
    }
  }
})

router.afterEach(() => { // 结束Progress
})
