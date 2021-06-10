import request from '@/utils/request'
export function fetchList(cid,params) {
  return request({
    url:'/service-other/productAttribute/list/'+cid,
    method:'get',
    params:params
  })
}

export function deleteProductAttr(data) {
  return request({
    url:'/service-other/productAttribute/delete',
    method:'post',
    data:data
  })
}

export function createProductAttr(data) {
  return request({
    url:'/service-other/productAttribute/create',
    method:'post',
    data:data
  })
}

export function updateProductAttr(id,data) {
  return request({
    url:'/service-other/productAttribute/update/'+id,
    method:'post',
    data:data
  })
}
export function getProductAttr(id) {
  return request({
    url:'/service-other/productAttribute/'+id,
    method:'get'
  })
}

export function getProductAttrInfo(productCategoryId) {
  return request({
    url:'/service-other/productAttribute/attrInfo/'+productCategoryId,
    method:'get'
  })
}
