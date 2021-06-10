import request from '@/utils/request'
export function fetchList (params) {
  return request({
    url: '/service-other/goods/alllist',
    method: 'get',
    params: params
  })
}

export function fetchSimpleList (params) {
  return request({
    url: '/service-other/product/simpleList',
    method: 'get',
    params: params
  })
}

export function updateDeleteStatus (params) {
  return request({
    url: '/service-other/product/update/deleteStatus',
    method: 'post',
    params: params
  })
}



export function updatePublishStatus (params) {
  return request({
    url: '/service-other/product/update/publishStatus',
    method: 'post',
    params: params
  })
}

export function createProduct (data) {
  return request({
    url: '/service-other/admin/goods/create',
    method: 'post',
    data: data
  })
}

export function updateProduct (id, data) {
  return request({
    url: '/service-other/admin/goods/update/' + id,
    method: 'post',
    data: data
  })
}

export function getProduct (id) {
  return request({
    url: '/service-other/goods/to_detail/' + id,
    method: 'get'
  })
}
