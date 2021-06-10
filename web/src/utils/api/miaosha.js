import request from '../request'

import {
  getverifyCodetoken
} from '@/utils/auth'

export function register (data) {
  return request({
    url: '/user/register',
    method: 'post',
    data
  })
}

export function login (data) {
  return request({
    url: '/service-other/user/login',
    method: 'post',
    headers: {
      'verifyCodetoken': getverifyCodetoken()
    },
    data
  })
}

export function getUserInfo () {
  return request({
    url: '/service-other/user/userinfo',
    method: 'get'
  })
}

export function getAllMiaoshaGoods () {
  return request({
    url: '/service-other/goods/list',
    method: 'GET'
  })
}

export function getMiaoshaGoods (goodsId) {
  return request({
    url: `/service-miaosha/seckill/to_detail/${goodsId}`,
    method: 'GET'
  })
}
export function getVerifyCode (goodsId) {
  return request({
    url: '/service-miaosha/seckill/verifyCode',
    method: 'GET',
    params: {
      goodsId: goodsId
    }
  })
}
export function getMiaoshaPath (goodsId, verifyCode) {
  return request({
    url: '/service-miaosha/seckill/path',
    method: 'GET',
    params: {
      goodsId: goodsId,
      verifyCode: verifyCode
    }
  })
}
export function seckill (path, goodsId) {
  return request({
    url: `/service-miaosha/seckill/${path}/`,
    method: 'POST',
    params: {
      goodsId: goodsId,
      path: path
    }
  })
}

export function getMiaoshaResult (goodsId) {
  return request({
    url: '/service-miaosha/seckill/result',
    method: 'GET',
    params: {
      goodsId: goodsId
    }
  })
}
export function getOrder (orderId) {
  return request({
    url: `/service-other/order/orderDetail/${orderId}`,
    method: 'GET',
    params: {
      orderId: orderId
    }
  })
}

export function getCartOrders () {
  return request({
    url: '/service-other/order/orders',
    method: 'GET'
  })
}
