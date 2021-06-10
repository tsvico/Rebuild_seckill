import request from '@/utils/request'
export function fetchList (params) {
  return request({
    url: '/service-other/goods/list',
    method: 'get',
    params: params
  })
}
export function createFlashProductRelation (data) {
  return request({
    url: `/service-other/admin/seckill/create/${data[0].productId}`,
    method: 'post',
    data: data[0].productId
  })
}
export function deleteFlashProductRelation (id) {
  return request({
    url: '/service-other/flashProductRelation/delete/' + id,
    method: 'post'
  })
}
export function updateFlashProductRelation (id, data) {
  return request({
    url: '/service-other/admin/seckill/update/' + id,
    method: 'post',
    data: data
  })
}
