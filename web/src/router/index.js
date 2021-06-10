import Vue from 'vue'
import Router from 'vue-router'
import Home from '@/views/Home'
import Login from '@/views/Login'
import GoodsDetail from '@/views/GoodsDetail'
import OrderDetail from '@/views/OrderDetail'

import Layout from '../views/admin/layout/Layout'

Vue.use(Router)

export const constantRouterMap = [{
  path: '/',
  name: 'home',
  component: Home,
  meta: {
    title: '秒杀商城'
  }
},
{
  path: '/login',
  name: 'login',
  component: Login,
  meta: {
    title: '登录'
  }
},
{
  path: '/goods/:id',
  name: 'goodsDetail',
  component: GoodsDetail,
  meta: {
    title: '商品详情'
  }
},
{
  path: '/order/:id',
  name: 'orderDetail',
  component: OrderDetail,
  meta: {
    title: '订单详情'
  }
},
{
  path: '/Cart',
  name: 'cart',
  component: () => import('@/views/Cart'),
  meta: {
    title: '购物车'
  }
},
{
  path: '/404',
  component: () => import('@/views/404'),
  hidden: true
},
{
  path: '',
  component: Layout,
  redirect: '/admin',
  children: [{
    path: 'admin',
    name: 'admin',
    component: () => import('@/views/admin/index'),
    meta: {
      title: '后台',
      icon: 'el-icon-eleme'
    }
  }]
}
]
export const asyncRouterMap = [{
  path: '/admin/pms',
  component: Layout,
  redirect: '/admin/pms/product',
  name: 'pms',
  meta: {
    title: '商品',
    icon: 'el-icon-goods'
  },
  children: [{
    path: 'product',
    name: 'product',
    component: () => import('@/views/admin/pms/product/index'),
    meta: {
      title: '商品列表',
      icon: 'el-icon-s-operation'
    }
  },
  {
    path: 'addProduct',
    name: 'addProduct',
    component: () => import('@/views/admin/pms/product/add'),
    meta: {
      title: '添加商品',
      icon: 'el-icon-folder-add'
    }
  },
  {
    path: 'updateProduct',
    name: 'updateProduct',
    component: () => import('@/views/admin/pms/product/update'),
    meta: {
      title: '修改商品',
      icon: 'product-add'
    },
    hidden: true
  }
  ]
},
{
  path: '/admin/oms',
  component: Layout,
  redirect: '/admin/oms/order',
  name: 'oms',
  meta: {
    title: '订单',
    icon: 'el-icon-document'
  },
  children: [{
    path: 'order',
    name: 'order',
    component: () => import('@/views/admin/oms/order/index'),
    meta: {
      title: '订单列表',
      icon: 'el-icon-notebook-2'
    }
  },
  {
    path: 'orderdetail',
    name: 'orderdetail',
    component: () => import('@/views/admin/oms/order/orderDetail'),
    meta: {
      title: '订单详情'
    },
    hidden: true
  }
  ]
},
{
  path: '/admin/sms',
  component: Layout,
  redirect: '/admin/sms/coupon',
  name: 'sms',
  meta: {
    title: '营销',
    icon: 'el-icon-collection'
  },
  children: [{
    path: 'flashProductRelation',
    name: 'flashProductRelation',
    component: () => import('@/views/admin/sms/flash/productRelationList'),
    meta: {
      title: '秒杀商品列表'
    },
    hidden: false
  }
  ]
},
{
  path: '*',
  redirect: '/404',
  hidden: true
}
]

export default new Router({
  routes: constantRouterMap
})
