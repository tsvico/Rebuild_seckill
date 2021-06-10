import request from '@/utils/request'

export function createProductAttrCate (data) {
  return request({
    url: '/service-other/productAttribute/category/create',
    method: 'post',
    data: data
  })
}

export function deleteProductAttrCate (id) {
  return request({
    url: '/service-other/productAttribute/category/delete/' + id,
    method: 'get'
  })
}

export function updateProductAttrCate (id, data) {
  return request({
    url: '/service-other/productAttribute/category/update/' + id,
    method: 'post',
    data: data
  })
}
export function fetchListWithAttr () {
  return request({
    url: '/service-other/productAttribute/category/list/withAttr',
    method: 'get'
  })
}
